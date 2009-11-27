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

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Control a camera device.
 * @author shino
 *
 */
public class CaptureImage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(new CaptureImageView(this));
		setContentView(R.layout.caputure_image);
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.capture_image_surface);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		Button button = (Button) findViewById(R.id.capture_image_button);
		CameraCaputureController captureImageView = new CameraCaputureController(this, button);
		button.setOnClickListener(captureImageView);
		surfaceHolder.addCallback(captureImageView);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
//		button.setFocusable(true);
//		button.setFocusableInTouchMode(true);

	}
	
	@Override
	public void onStop(){
		super.onPause();
		// TODO: this may move to the Top activity.
		System.exit(0);
	}
	
}
