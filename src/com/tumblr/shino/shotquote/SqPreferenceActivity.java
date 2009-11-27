package com.tumblr.shino.shotquote;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SqPreferenceActivity extends PreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.root_preferences);
    }

}

