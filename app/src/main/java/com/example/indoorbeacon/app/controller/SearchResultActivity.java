package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.Database;
import com.example.indoorbeacon.app.view.adapter.CustomResultExpListAdapter;

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

        ArrayList<String> persons = new ArrayList<>(Database.getDB().getSearchSpecificPersonEntries(suchInhalt));
        ArrayList<String> rooms = new ArrayList<>(Database.getDB().getSearchSpecificRoomEntries(suchInhalt));
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

        ViewStub stub = (ViewStub) findViewById(R.id.searchResultsViewStub);

        if (results.size() != 0) {
            stub.setLayoutResource(R.layout.exp_list_view);
            stub.inflate();

            ExpandableListView list = (ExpandableListView) findViewById(R.id.expListView);
            ArrayList<String> categories = new ArrayList<>(results.keySet());
            CustomResultExpListAdapter adapter = new CustomResultExpListAdapter(this, results, categories);
            list.setAdapter(adapter);

            for (int i = 0; i < adapter.getGroupCount(); i++)
                list.expandGroup(i);

            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String value = String.valueOf(parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
                    Intent intent = new Intent(SearchResultActivity.this, NavigationActivity.class);
                    intent.putExtra("Ziel", value);
                    startActivity(intent);
                    return true;
                }
            });
        } else {
            stub.setLayoutResource(R.layout.search_results_nothingfound_content);
            stub.inflate();
        }
    }

    public void openListActivity(View view) {
        Intent intent = new Intent(this, RoomlistActivity.class);
        startActivity(intent);
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
