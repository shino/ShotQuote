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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CameraCaputureController extends SurfaceView 
implements SurfaceHolder.Callback, OnClickListener{
	private SurfaceHolder holder;
	private Camera camera;
	private Button captureButton;
	private TickHandler autofocusHandler;

	public CameraCaputureController(Context context, Button captureButton) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.captureButton = captureButton;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);

			autofocusHandler = new TickHandler(camera);
			
		} catch (IOException e){
			U.errorLog(this, "in staring camera", e);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
//		parameters.setPreviewSize(180, 120);
		parameters.setPreviewFormat(PixelFormat.JPEG);
		parameters.set("jpeg-quality", 100);
		camera.setParameters(parameters);

		camera.startPreview();
//		camera.autoFocus(new SQAutoFocusCallback());
	}
	
	private static class SQAutoFocusCallback implements Camera.AutoFocusCallback{
		public void onAutoFocus(boolean success, Camera camera) {
			U.debugLog(this, "auto focused", success);
			if(success) {
				// TODO: how to use auto focus
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		autofocusHandler.finish();
		try {
			camera.stopPreview();
		} finally {
			camera.release();
			camera = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			takePicture();
			camera.startPreview();
		}
		return true;
	}
	
	public void takePicture(){
		camera.takePicture(null, null, new Camera.PictureCallback() {
			
			public void onPictureTaken(byte[] data, Camera camera) {
				try{
					storeData(getContext(), data, "test.jpg");
				} catch (Exception e) {
					U.errorLog(this, "in storing data", e);
				}
				
			}
		});
	}

	private void storeData(Context context, byte[] data, String fileName) throws IOException{
		// TODO: what kind of implementation is suitable for the purpose?
		U.debugLog(this, "data length(bytes)", data.length);
//		OutputStream outputStream = this.getContext().openFileOutput(fileName, Context.MODE_WORLD_READABLE);
		File file;
		OutputStream outputStream = null;
		try{
			file = new File("/sdcard/ShotQuote/" + fileName);
			U.debugLog(this, file.getAbsolutePath() + " exists?", file.exists());
			U.debugLog(this, file.getAbsolutePath() + " writable?", file.canWrite());
			
			outputStream = new FileOutputStream(file);
			int[] rgb8888Data = new int[data.length];
			
			int width = camera.getParameters().getPreviewSize().width;
			int height = camera.getParameters().getPreviewSize().height;
			U.decodeYUV(rgb8888Data, data, width, height);
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(rgb8888Data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);
//			outputStream.write(outData, 0, data.length);
		} finally {
			if(outputStream != null) outputStream.close();
		}
	}

	private class TickHandler extends Handler {
		private final Camera camera;
		public TickHandler(Camera camera) {
			this.camera = camera;
			this.sleep(10);
		}

		@Override
		public void handleMessage(Message message){
			camera.autoFocus(new SQAutoFocusCallback());
			U.debugLog(this, "message is handled.", message.toString());
			this.sleep(5000);
		}
		
		public void sleep(long delayMillis){
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
		
		public void finish(){
			removeMessages(0);
		}
	}
	

	public void onClick(View target) {
		if(target == captureButton){
			U.debugLog(this, "the capture button was clicked", captureButton);
			autofocusHandler.finish();
			camera.autoFocus(new AutoFocusCallback() {
				public void onAutoFocus(boolean success, Camera camera) {
					if(success) {
						camera.setOneShotPreviewCallback(new PreviewCallback() {
							
							public void onPreviewFrame(byte[] data, Camera camera) {
								try {
									storeData(getContext(), data, "sq_test75.jpg");
								} catch (IOException e) {
									U.errorLog(this, "storing preview data to a file", e);
								}
							}
						});
					}
				}
			});
		}
	}
}
