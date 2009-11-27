package com.tumblr.shino.shotquote.tumblr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class TumblrImagePost {

	private static final String BOUNDARY_WITHOUT_HYPHENS = "=========XDTHORNGREUHHOPEENJW";
	private static final String BOUNDARY = "--" + BOUNDARY_WITHOUT_HYPHENS;
	
	private String email;
	private String password;
	private String hostname;

	private String title;
	private String caption;
	private String generator;
	private String fileUri;
	private String mimeType;
	private InputStream fileInputStream;
	
	private String postId;
	private Exception error;

    public TumblrImagePost(String email, String password, String hostname){
    	this.email = email;
    	this.password = password;
    	this.hostname = hostname;
    }

    public String post() throws Exception {
    	return post(title, caption, generator, fileUri, mimeType, fileInputStream);
    }
    
    /**
     * 
     * @param title
     * @param caption
     * @param generator
     * @param fileUriString
     * @param mimeType
     * @param fileInputStream
     * @return Post ID
     * @throws Exception
     */
    public String post(String title, String caption, String generator,
    		String fileUriString, String mimeType, InputStream fileInputStream) throws Exception {
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
            writeRequest(os, title, caption, generator, fileUriString, mimeType, fileInputStream);
            
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

    public String post(String title, String caption, String generator,
    		String fileUriString, InputStream fileInputStream) throws Exception {
        String mimeType = HttpURLConnection.guessContentTypeFromName(fileUriString);
    	return post(title, caption, generator, fileUriString, mimeType, fileInputStream);
    }
  	public String post(String title, String caption, String generator, URI fileUri) throws Exception {
    	return post(title, caption, generator, fileUri.toString(), fileUri.toURL().openStream());
    }

	public void writeRequest(OutputStream os, String title, String body, String generator,
			String fileUriString, InputStream fileInputStream) throws IOException {
        String mimeType = HttpURLConnection.guessContentTypeFromName(fileUriString);
        writeRequest(os, title, body, generator, fileUriString, mimeType, fileInputStream);
	}
	
	public void writeRequest(OutputStream os, String title, String body, String generator,
			String fileUriString, String mimeType, InputStream fileInputStream) throws IOException {
		
        Writer writer = new OutputStreamWriter(os, "UTF-8");
        
        writeContentDisposition(writer, "email", this.email);
        writeContentDisposition(writer, "password", this.password);
        writeContentDisposition(writer, "type", "photo");
        writeContentDisposition(writer, "title", title);
        writeContentDisposition(writer, "caption", body);
        writeContentDisposition(writer, "generator", generator);

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
	
	public void writeRequest(OutputStream os, String title, String body,
			String generator, URI fileUri) throws IOException {
		writeRequest(os, title, body, generator, fileUri.toString(), fileUri.toURL().openStream());
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
        int len = -1;
        while ((len = fileInputStream.read(tmp)) >= 0) {
            outputStream.write(tmp, 0, len);
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

	public String getFileUri() {
		return fileUri;
	}

	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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
