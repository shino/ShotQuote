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

package com.tumblr.shino.shotquote.tumblr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tumblr.shino.shotquote.SharingAggregate;
import com.tumblr.shino.shotquote.U;

import android.content.Context;
import android.net.Uri;

public class TumblrImagePost {

	private static final String BOUNDARY_WITHOUT_HYPHENS = "=========XDTHORNGREUHHOPEENJW";
	private static final String BOUNDARY = "--" + BOUNDARY_WITHOUT_HYPHENS;
	
	private String email;
	private String password;
	private String hostname;

	private String title;
	private String caption;
	private String generator;
	private Uri fileUri;
	private long fileSize;
	private String mimeType;
	private InputStream fileInputStream;
	
	private String postId;
	private Exception error;
	
	private SharingAggregate.PostingImageTask task;
	private Context context;
	private int min;
	private int max;

    public TumblrImagePost(String email, String password, String hostname){
    	this.email = email;
    	this.password = password;
    	this.hostname = hostname;
    }

    public String post() throws Exception {
    	return post(title, caption, generator, fileUri, mimeType, fileInputStream);
    }
    
    public String post(SharingAggregate.PostingImageTask task, Context context, int min, int max) throws Exception {
    	this.task = task;
    	this.context = context;
    	this.min = min;
    	this.max = max;
    	return post(title, caption, generator, fileUri, mimeType, fileInputStream);
    }
    
    /**
     * 
     * @param title
     * @param caption
     * @param generator
     * @param fileUri
     * @param mimeType
     * @param fileInputStream
     * @return Post ID
     * @throws Exception
     */
    public String post(String title, String caption, String generator,
    		Uri fileUri, String mimeType, InputStream fileInputStream) throws Exception {
    	OutputStream os = null;
        InputStream is = null;
        HttpURLConnection uc = null;
        try {
        	URL url = new URL("http://" + this.hostname + "/api/write");
            uc = (HttpURLConnection) url.openConnection();
            uc.addRequestProperty("Content-Type",
            		"multipart/form-data; boundary=" + BOUNDARY_WITHOUT_HYPHENS);
            uc.setDoOutput(true);

            os = uc.getOutputStream();
            writeRequest(os, title, caption, generator, fileUri, mimeType, fileInputStream);
            
            is = uc.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int len = -1;
            while ((len = is.read(tmp)) >= 0) {
                bos.write(tmp, 0, len);
            }
            String postId = new String(bos.toByteArray(), "UTF-8");
            this.postId = postId;
            return postId;
        } catch (Exception e) {
        	this.error = e;
        	throw e;
        } finally {
        	try {
			try {
				if (os != null)
					os.close();
			} finally {
				if (is != null)
					is.close();
			}
        	} finally {
        		if (uc != null) uc.disconnect();
        	}
        }
    }

    public void writeRequest(OutputStream os, String title, String body, String generator,
			Uri fileUri, InputStream fileInputStream) throws IOException {
        String mimeType = HttpURLConnection.guessContentTypeFromName(fileUri.toString());
        writeRequest(os, title, body, generator, fileUri, mimeType, fileInputStream);
	}
	
	public void writeRequest(OutputStream os, String title, String body, String generator,
			Uri fileUri, String mimeType, InputStream fileInputStream) throws IOException {
		
        Writer writer = new OutputStreamWriter(os, "UTF-8");
        
        writeContentDisposition(writer, "email", this.email);
        writeContentDisposition(writer, "password", this.password);
        writeContentDisposition(writer, "type", "photo");
        writeContentDisposition(writer, "title", title);
        writeContentDisposition(writer, "caption", body);
        writeContentDisposition(writer, "generator", generator);

        String fileUriString = fileUri.toString();
        int positionOfLastSeparator = fileUriString.lastIndexOf(File.separatorChar);
        String fileName = fileUriString.substring(positionOfLastSeparator + 1);
        writeContentDispositionOfFile(writer, "data", fileName, mimeType);
        writer.flush();

        writeFileContents(os, fileInputStream);
        os.write("\r\n".getBytes("UTF-8"));
        os.write(BOUNDARY.getBytes("UTF-8"));
        os.write("--".getBytes("UTF-8"));
        os.write("\r\n".getBytes("UTF-8"));
        os.flush();
        
	}
	
	public void writeContentDisposition(Writer writer, String name,
			String value) throws IOException {
		writer.write(BOUNDARY);
		writer.write("\r\n");
		writer.write("Content-Disposition: form-data; name=\"");
		writer.write(name);
		writer.write("\"\r\n");
		writer.write("\r\n");
		writer.write(value);
		writer.write("\r\n");
	}

	public void writeContentDispositionOfFile(Writer writer, String name,
			String filename, String mimeType) throws IOException {
		writer.write(BOUNDARY);
		writer.write("\r\n");
		writer.write("Content-Disposition: form-data; name=\"");
		writer.write(name);
		writer.write("\"; filename=\"");
		writer.write(filename);
		writer.write("\"\r\n");
		writer.write("Content-Type: ");
		writer.write(mimeType);
		writer.write("\r\n");
		writer.write("\r\n");
	}
	
	public void writeFileContents(OutputStream outputStream, InputStream fileInputStream) throws IOException {
        byte[] tmp = new byte[1024];
        float remain = fileSize * 1.0f;
    	U.debugLog(context, "fileSize", Long.toString(fileSize));
    	U.debugLog(context, "remain", Float.toString(remain));
        int len = -1;
        while ((len = fileInputStream.read(tmp)) >= 0) {
        	U.debugLog(context, "len", Integer.toString(len));
            remain -= len;
            outputStream.write(tmp, 0, len);
            int progress = (int)((min - max) * (remain/(fileSize)) + max);
        	U.debugLog(context, "progress", Integer.toString(progress));
            task.publishProgressAndSecondary(progress, progress);
        }
        outputStream.flush();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public Uri getFileUri() {
		return fileUri;
	}

	public void setFileUri(Uri fileUri) {
		this.fileUri = fileUri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}

}
