// Copyright 2009 Shun'ichi Shinohara

// This file is part of ShotQuote.
//
// ShotQuote is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ShotQuote is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ShotQuote.  If not, see <http://www.gnu.org/licenses/>.

package com.tumblr.shino.shotquote;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabase {
	private static final String DB_NAME = "shotquote.db";
	private static final String DB_TABLE = "books";
	private static final int DB_VERSION = 1;
	
	private static final int STATUS_VALID = 0;

	private Context context;
	private SQLiteDatabase db;
	
	public BookDatabase(Context context){
		this.context = context;
		DBHelper dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public void add(Book book){
		ContentValues values = new ContentValues();
		values.put("isbn", book.getIsbn());
		values.put("status", STATUS_VALID);
		values.put("authors", book.getAuthor());
		values.put("url", book.getUrl());
		values.put("title", book.getTitle());
		db.beginTransaction();
		try {
			long result = db.insert(DB_TABLE, "", values);
			if(result >= 0){
				db.setTransactionSuccessful();			
				U.debugLog(this, "Book was inserted", values);
			} else {
				throw new RuntimeException("Book inseartion failed. Result value is :" + result);
			}
		} catch (RuntimeException e){
			U.errorLog(this, e.toString(), e);
			throw e;
		} finally {
			db.endTransaction();
		}

	}
	
	public void delete(Book book) {
		db.beginTransaction();
		try {
			long result = db.delete(DB_TABLE, "id=?", new String[]{Integer.toString(book.getId())});
			if(result == 1){
				db.setTransactionSuccessful();
				U.debugLog(this, "Book was deleted", book.getId() + ":" + book.getTitle());
			} else {
				throw new RuntimeException("Book deletion failed. Result value is :" + result);
			}
		} catch (RuntimeException e){
			U.errorLog(this, e.toString(), e);
			throw e;
		} finally {
			db.endTransaction();
		}
	}
	
	public List<Book> allBooks(){
		Cursor cursor = db.query(DB_TABLE,
				new String[]{"id", "status", "isbn", "authors", "title", "url", "extras"},
				"", null, null, null, "id desc");
		cursor.moveToFirst();
		int count = cursor.getCount();
		List<Book> books = new ArrayList<Book>(count);
		U.debugLog(this, "Stored books found", count);
		while(true){
			if(cursor.isAfterLast()){
				cursor.close();
				return books;
			}
			Book book = new Book();
			book.setId(cursor.getInt(0));
			book.setStatus(cursor.getInt(1));
			book.setIsbn(cursor.getString(2));
			book.setAuthor(cursor.getString(3));
			book.setTitle(cursor.getString(4));
			book.setUrl(cursor.getString(5));
			book.setExtras(cursor.getString(6));
			U.debugLog(context, "book found in database", book);
			books.add(book);
			cursor.move(1);
		}
	}
	
	private static class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table if not exists "
					+ DB_TABLE 
					+ " (id integer primary key autoincrement, "
					+ " status int,"
					+ " isbn text,"
					+ " title text, authors text,"
					+ " url text, extras text)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			// TODO: more suitable logic should be used not to loose data.
			db.execSQL("drop table if exists " + DB_TABLE);
		}
	}
}
