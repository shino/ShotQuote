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
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TumblrPost {

	private String email;
	private String password;
	private String hostname;

    public TumblrPost(String email, String password, String hostname){
    	this.email = email;
    	this.password = password;
    	this.hostname = hostname;
    }

    public void post(String title, String comment) {
        OutputStream os = null;
        InputStream is = null;
        try {
        	URL url = new URL("http://" + this.hostname + "/api/write");
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
    
            String email = this.email;
            String passwd = this.password;
            String type = "regular";
            String body = comment;
            StringBuffer post = new StringBuffer();
            post.append("email=").append(URLEncoder.encode(email));
            post.append("&").append("password=").append(URLEncoder.encode(passwd));
            post.append("&").append("type=").append(URLEncoder.encode(type));
            post.append("&").append("title=").append(URLEncoder.encode(title));
            post.append("&").append("body=").append(URLEncoder.encode(body));
            post.append("&").append("generator=").append(URLEncoder.encode("ShotQuote"));
            os = uc.getOutputStream();
            os.write((post.toString()).getBytes("UTF-8"));
    
            is = uc.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int len = -1;
            while ((len = is.read(tmp)) >= 0) {
                bos.write(tmp, 0, len);
                bos.flush();
                if (len < tmp.length) {
                    break;
                }
            }
    
            String postId = new String(bos.toByteArray());
            System.out.println("post's ID : " + postId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
