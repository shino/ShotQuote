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
