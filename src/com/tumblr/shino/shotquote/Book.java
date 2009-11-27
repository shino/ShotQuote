package com.tumblr.shino.shotquote;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

import com.tumblr.shino.shotquote.Bookshelf.BookSearchTask;


public class Book implements Parcelable{
	// private static final String AMAZON_JP_URL = "http://ecs.amazonaws.jp/onca/xml?";
	private static final String AMAZON_JP_PROXY_URL = "http://honnomemo.appspot.com/rpaproxy/jp/?";
	private static final String AMAZON_ACCESS_KEY = "AKIAIT2M2HIBZSYWPZHA";
	private static final String AMAZON_URL = AMAZON_JP_PROXY_URL
				+ "Service=AWSECommerceService" 
				+ "&AWSAccessKeyId=" + AMAZON_ACCESS_KEY 
				+ "&Operation=ItemLookup&IdType=EAN"
				+ "&SearchIndex=Books"
				+ "&ResponseGroup=Small"
				+ "&ItemId=";

	private int id;
	private int status;
	private String isbn;
	private String title;
	private String author;
	private String url;
	private String extras;

	public Book() {
	}
	
	/**
	 * Search book by ISBN code.
	 * 
	 *  If there is no result from Amazon, return <code>book</code> object has only ISBN code (title is null, etc.)
	 *  If there occurs an error, this function may return null.
	 * 
	 * @param scannedResult
	 * @param context
	 * @param asyncTask
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Book searchAndCreate(Intent scannedResult, Context context, BookSearchTask asyncTask)
			throws IOException, XmlPullParserException {
        String ean_code = scannedResult.getStringExtra("SCAN_RESULT");
        String format = scannedResult.getStringExtra("SCAN_RESULT_FORMAT");
        U.debugLog(context, "Scanned code", ean_code);
        U.debugLog(scannedResult, "Scanned format", format);
        
        if(!format.equals("EAN_13")) throw new RuntimeException("Invalid scanned barcode: " + format);
        
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
        	String urlString = AMAZON_URL + ean_code; 
        	U.debugLog(context, "URL to search Amazon", urlString);
        	URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
        	connection.setRequestMethod("GET");
        	connection.connect();
        	int httpResponseCode = connection.getResponseCode();
        	U.debugLog(context, "Response code from Amazon", Integer.toString(httpResponseCode));
        	asyncTask.publishProgressAndSecondary(50, 90);
        	inputStream = connection.getInputStream();

        	final XmlPullParser parser = Xml.newPullParser();
        	parser.setInput(inputStream, "UTF-8");
        	String tagName = null;
        	Book book = new Book();
        	book.isbn = ean_code;
			for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser
					.next()) {
				switch (eventType) {
				// TAGの始まり
				case XmlPullParser.START_TAG:
					tagName = parser.getName();

					if (tagName.equals("Title")) {
						parser.next();
						if (parser.getEventType() == XmlPullParser.TEXT) {
							book.setTitle(parser.getText());
							U.debugLog(context, "title found", book.getTitle());
						}
					} else if (tagName.equals("Author")) {
						parser.next();
						if (parser.getEventType() == XmlPullParser.TEXT) {
							book.setAuthor(parser.getText());
							U.debugLog(context, "author found", book.getAuthor());
						}
					} else if (tagName.equals("DetailPageURL")) {
						parser.next();
						if (parser.getEventType() == XmlPullParser.TEXT) {
							book.setUrl(parser.getText());
							U.debugLog(context, "url found", book.getUrl());
						}
					}

				}
			}
        	asyncTask.publishProgressAndSecondary(90, 100);
        	return book;
		} finally {
			try {
				if(inputStream != null) inputStream.close();
			} finally {
				if(connection != null) connection.disconnect();
			}
		}
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return "Book{ id: " + id + ", status: " + status 
				+ ", isbn: " + isbn + ", title: " + title 
				+ ", authors: " + author + "}";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
	}

	public int describeContents() {
		return 0;
	}

	public Book(Parcel in){
		readFromParcel(in);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeInt(status);
		out.writeString(isbn);
		out.writeString(title);
		out.writeString(author);
		out.writeString(url);
		out.writeString(extras);
	}
	
	public void readFromParcel(Parcel in) {
		U.debugLog(this, "Book constructor (from percel)", in);
		id = in.readInt();
		status = in.readInt();
		isbn = in.readString();
		title = in.readString();
		author = in.readString();
		url = in.readString();
		extras = in.readString();
	}
	
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
    	public Book createFromParcel(Parcel in) {
    		return new Book(in);
    	}

    	public Book[] newArray(int size) {
    		return new Book[size];
    	}
    };
}
