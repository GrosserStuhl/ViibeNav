package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.Definitions;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Dima on 27/07/2015.
 */
public class SearchActivity extends Activity {

    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        setContentView(R.layout.activity_suchen);
        if (darkBackground) {
            TextView textView = (TextView) findViewById(R.id.infoTextSuche);
            textView.setTextColor(Color.WHITE);
            EditText editText = (EditText) findViewById(R.id.searchField);
            editText.setTextColor(Color.WHITE);
            View root = getWindow().getDecorView().getRootView();
            root.setBackgroundColor(Color.parseColor(Definitions.DARK_BACKGROUND_COLOR));
        }

        searchField = (EditText) findViewById(R.id.searchField);

        searchField.setImeActionLabel("Suchen", KeyEvent.KEYCODE_ENTER);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String suchInhalt = searchField.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("suchInhalt", suchInhalt);
                startActivity(intent);
                return true;
            }
        });

        final ImageButton searchButton = (ImageButton) findViewById(R.id.sucheStartenImgButton);
        searchButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String suchInhalt = searchField.getText().toString().trim();

                if (suchInhalt.equals("")) {
                    searchField.setError("Bitte Suchbegriff eingeben!");
                } else {

                    Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                    intent.putExtra("suchInhalt", suchInhalt);
                    startActivity(intent);
                }
            }
        });

        ImageButton speechButton = (ImageButton) findViewById(R.id.spracheingabeButton);
        speechButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Spracheingabe");
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                searchField.setText(matches.get(0));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
