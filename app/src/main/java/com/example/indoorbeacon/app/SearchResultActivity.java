package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.dbmodels.InfoDBModel;
import com.example.indoorbeacon.app.view.adapter.CustomResultListAdapter;

/**
 * Created by Dima on 28/07/2015.
 */
public class SearchResultActivity extends Activity {

    private InfoDBModel[] searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        String suchInhalt = intent.getStringExtra("suchInhalt");

        searchResults = DBHandler.getDB().getSearchSpecificInfoEntries(suchInhalt);

        TextView resultText = (TextView) findViewById(R.id.searchResultTextView);
        String text = resultText.getText().toString();
        text = text.replace("X", suchInhalt);
        text = text.replace("#", searchResults.length + "");
        resultText.setText(text);

        ViewStub stub = (ViewStub) findViewById(R.id.viewStub);

        if (searchResults.length != 0) {
            stub.setLayoutResource(R.layout.search_results_list_content);
            stub.inflate();

            final ListView list = (ListView) findViewById(R.id.resultsListView);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) list.getLayoutParams();
//            params.setMarginStart(25);
//            list.setLayoutParams(params);

            CustomResultListAdapter adapter = new CustomResultListAdapter(this, searchResults);
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
        } else {

            stub.setLayoutResource(R.layout.search_results_nothingfound_content);
            stub.inflate();

//            TextView nothingFoundText = (TextView) findViewById(R.id.nothingFoundTextView);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) nothingFoundText.getLayoutParams();
//            params.setMarginStart(10);
//            nothingFoundText.setLayoutParams(params);
        }
    }

    public void openListActivity(View view) {
        Intent intent = new Intent(this, RoomlistActivity.class);
        startActivity(intent);
    }

}
