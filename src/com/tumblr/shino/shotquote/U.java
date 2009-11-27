package com.tumblr.shino.shotquote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

/**
 * A collection of utility (external) methods.
 * @author shino
 *
 */
public class U {
	
	// decode Y, U, and V values on the YUV 420 buffer described as YCbCr_422_SP
	// by Android
	// David Manpearl 081201
	// Copied from Android Camera Preview Filter Using Camera.PreviewCallback.onPreviewFrame
	//  - Android Developers | Google グループ http://groups.google.com/group/android-developers/msg/d3b29d3ddc8abf9b
	public static void decodeYUV(int[] out, byte[] fg, int width, int height)
			throws NullPointerException, IllegalArgumentException {
		final int sz = width * height;
		if (out == null)
			throw new NullPointerException("buffer 'out' is	null");
		if (out.length < sz)
			throw new IllegalArgumentException("buffer 'out' size "
					+ out.length + " < minimum " + sz);
		if (fg == null)
			throw new NullPointerException("buffer 'fg' is null");
		if (fg.length < sz)
			throw new IllegalArgumentException("buffer 'fg' size " + fg.length
					+ " < minimum " + sz * 3 / 2);
		int i, j;
		int Y, Cr = 0, Cb = 0;
		for (j = 0; j < height; j++) {
			int pixPtr = j * width;
			final int jDiv2 = j >> 1;
			for (i = 0; i < width; i++) {
				Y = fg[pixPtr];
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
					Cb = fg[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = fg[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;
				out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
			}
		}

	}
	
	public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
        	byte[] buffer = new byte[2048];
        	int size = 0;
        	while(true){
        		size = inputStream.read(buffer);
        		if(size <= 0) break;
        		outputStream.write(buffer, 0, size);
        	}
        	return outputStream.toByteArray();
        } finally {
        	outputStream.close();
        }
	}

	public static void showToast(final Activity activity, int textResId){
		showToast(activity, activity.getString(textResId));
	}

	public static void showToast(final Activity activity, String text){
		Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
		toast.show();
	}
	
	public static void showDialog(final Activity activity, String title, String text){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setMessage(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.setResult(Activity.RESULT_OK);
			}
		});
		builder.create();
		builder.show();
	}

	public static void errorLog(Object caller, String message, Exception e){
		Log.e(caller.getClass().getSimpleName(), message, e);
	}

	public static void debugLog(Object caller, String label, Object data){
		if(data == null) data = "[*null reference*]";
		Log.d(caller.getClass().getSimpleName(), "========= " + label + ": " + data.toString());
	}

	public static void infoLog(Object caller, String label, Object data){
		if(data == null) data = "[*null reference*]";
		Log.i(caller.getClass().getSimpleName(), label + ": " + data.toString());
	}
}
