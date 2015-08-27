package com.example.indoorbeacon.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Dima on 28/07/2015.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
    }
}
