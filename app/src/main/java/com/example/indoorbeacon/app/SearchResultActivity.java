package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.dbmodels.InfoDBModel;
import com.example.indoorbeacon.app.view.adapter.CustomResultExpListAdapter;
import com.example.indoorbeacon.app.view.adapter.CustomResultListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dima on 28/07/2015.
 */
public class SearchResultActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        String suchInhalt = intent.getStringExtra("suchInhalt");

        ArrayList<String> persons = new ArrayList<>(DBHandler.getDB().getSearchSpecificPersonEntries(suchInhalt));
        ArrayList<String> rooms = new ArrayList<>(DBHandler.getDB().getSearchSpecificRoomEntries(suchInhalt));
        HashMap<String, List<String>> results = new HashMap<>();

        if (persons.size() > 0)
            results.put("Personen", persons);
        if (rooms.size() > 0)
            results.put("Räume", rooms);

        TextView resultText = (TextView) findViewById(R.id.searchResultTextView);
        String text = resultText.getText().toString();
        text = text.replace("X", suchInhalt);
        text = text.replace("#", persons.size() + rooms.size() + "");
        resultText.setText(text);

        ViewStub stub = (ViewStub) findViewById(R.id.viewStub);

        if (results.size() != 0) {
            ArrayList<String> categories = new ArrayList<>(results.keySet());

            stub.setLayoutResource(R.layout.search_results_exp_list);
            stub.inflate();

//            final ListView list = (ListView) findViewById(R.id.resultsListView);
            final ExpandableListView list = (ExpandableListView) findViewById(R.id.resultExpListView);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) list.getLayoutParams();
//            params.setMarginStart(25);
//            list.setLayoutParams(params);

//            CustomResultListAdapter adapter = new CustomResultListAdapter(this, searchResults);
            CustomResultExpListAdapter adapter = new CustomResultExpListAdapter(this, results, categories);
            list.setAdapter(adapter);

            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String value = String.valueOf(parent.getChildAt(childPosition));
                    Intent intent = new Intent(SearchResultActivity.this, NavigationActivity.class);
                    intent.putExtra("Ziel", value);
                    startActivity(intent);
                    return true;
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
