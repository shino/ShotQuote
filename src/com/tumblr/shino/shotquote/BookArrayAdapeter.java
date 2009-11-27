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

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BookArrayAdapeter extends ArrayAdapter<Book> {
	private final Activity context;
	private final List<Book> books;
	private final int itemLayout;
//	SearchHeader header;

	public BookArrayAdapeter(Activity context, int itemLayout, List<Book> books) {
		super(context, itemLayout, books);
		this.context = context;
		this.itemLayout = itemLayout;
		this.books = books;
	}

	@Override
	public View getView(final int position, final View convertView,
	        final ViewGroup parent){
		final View view = ((Activity) this.context).getLayoutInflater().inflate(
	                this.itemLayout, null);
		Book book = books.get(position);
		TextView titleView = (TextView) view.findViewById(R.id.book_item_title);
		titleView.setText(book.getTitle());
		TextView authorsView = (TextView) view.findViewById(R.id.book_item_authors);
		authorsView.setText(book.getAuthor());
		TextView isbnView = (TextView) view.findViewById(R.id.book_item_isbn);
		isbnView.setText("[ISBN:" + book.getIsbn() + "]");
		
		return view;
	}
}
