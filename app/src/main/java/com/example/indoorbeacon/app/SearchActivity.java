package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dima on 27/07/2015.
 */
public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_suchen);

        final EditText searchField = (EditText) findViewById(R.id.searchField);
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

        ImageButton searchButton = (ImageButton) findViewById(R.id.sucheStartenImgButton);
        searchButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String suchInhalt = searchField.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("suchInhalt", suchInhalt);
                startActivity(intent);
            }
        });

        ImageButton speechButton = (ImageButton) findViewById(R.id.spracheingabeButton);
        speechButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
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
               EditText searchField = (EditText) findViewById(R.id.searchField);
               searchField.setText(matches.get(0));
            }
        }
    }
}
