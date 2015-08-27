package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dima on 28/07/2015.
 */
public class SearchResultActivity extends Activity {

    public static String[] results = {"Robert Tscharn", "Tobias Grundgeiger", "Diana Löffler"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        String suchInhalt = intent.getStringExtra("suchInhalt");

        final ListView list = (ListView) findViewById(R.id.resultsListView);
        TextView resultText = (TextView) findViewById(R.id.resultTextView);
        String text = resultText.getText().toString();
        text = text.replace("X", suchInhalt);
        text = text.replace("#", results.length + "");
        resultText.setText(text);

        CustomAdapter adapter = new CustomAdapter(this, results);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = String.valueOf(parent.getItemAtPosition(position));
                Intent intent = new Intent(SearchResultActivity.this, NavigationActivity.class);
                intent.putExtra("Ziel", value);
                startActivity(intent);
            }
        });
    }
}
