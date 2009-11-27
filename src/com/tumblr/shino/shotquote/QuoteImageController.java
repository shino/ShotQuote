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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class QuoteImageController extends Activity 
	implements OnClickListener {
	
	private Button finishButton;
	private ImageView imageView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote_image);
		finishButton = (Button) findViewById(R.id.quote_image_finish);
		finishButton.setOnClickListener(this);
		imageView = (ImageView) findViewById(R.id.quote_image_view);
		
		Bundle bundle = getIntent().getExtras();
		Bitmap bitmap = (Bitmap) bundle.get("data");
		U.debugLog(this, "iamge width", bitmap.getWidth());
		U.debugLog(this, "iamge height", bitmap.getHeight());
		imageView.setImageBitmap(bitmap);
    }
	
	public void onClick(View target) {
		if(target == finishButton){
			U.debugLog(this, "the finish button was clicked", finishButton);
			Intent result = new Intent();
			result.putExtra("text", "dummy text for testing");
			setResult(RESULT_OK, result);
			// TODO: how to implement when activity is finished NOT successfully
			// setResult(RESULT_CANCELED, data) // canceled? Is there way to express FAILURE
			finish();
		}
	}

}
