package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.BluetoothScan;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.model.SensorHelper;
import com.example.indoorbeacon.app.model.TTS;
import com.example.indoorbeacon.app.model.WiFiConnector;
import com.example.indoorbeacon.app.model.dbmodels.Database;


public class MainActivity extends Activity {

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        setContentView(R.layout.activity_main);
        if (darkBackground) {
            TextView suchText = (TextView) findViewById(R.id.suchenTextView);
            suchText.setTextColor(Color.WHITE);
            TextView listeText = (TextView) findViewById(R.id.listeTextView);
            listeText.setTextColor(Color.WHITE);
            View root = getWindow().getDecorView().getRootView();
            root.setBackgroundColor(Color.parseColor(Definitions.DARK_BACKGROUND_COLOR));
        }

        database = Database.createDB(this, null, null, 1);

        WiFiConnector.getConnector(this);
        BluetoothScan.getBtScan(this);
        SensorHelper.getSensorHelper(this);

        TTS.getTTS(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public void openSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openListActivity(View view) {
        Intent intent = new Intent(this, RoomlistActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }
}
