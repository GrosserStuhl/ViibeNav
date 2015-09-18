package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;

import android.widget.TextView;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.model.dbmodels.Database;
import com.example.indoorbeacon.app.model.dbmodels.InfoModel;
import com.example.indoorbeacon.app.view.adapter.CustomResultExpListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dima on 27/07/2015.
 */
public class RoomlistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        setContentView(R.layout.activity_raumliste);
        if (darkBackground) {
            TextView someView = (TextView) findViewById(R.id.roomlistTextView);
            someView.setTextColor(Color.WHITE);
            View root = getWindow().getDecorView().getRootView();
            root.setBackgroundColor(Color.parseColor(Definitions.DARK_BACKGROUND_COLOR));
        }

        HashMap<String, List<String>> allEntries = new HashMap<>();
        ArrayList<String> categories = Database.getDB().getAllDistinctCategories();
        if (categories.size() > 0) {
            for (String category : categories) {
                ArrayList<InfoModel> infoList = Database.getDB().getAllEntriesForSpecificCategory(category);
                if (infoList.size() > 0) {
                    ArrayList<String> entryList = new ArrayList<>();
                    for (InfoModel infoEntry : infoList) {
                        entryList.add("<font color='#000000'>" + infoEntry.getPerson_name() +
                                "</font><br/><font color='#006400'>" + infoEntry.getRoom_name() + "</font>");
                    }
                    allEntries.put(category, entryList);
                }
            }
        }

        ViewStub stub = (ViewStub) findViewById(R.id.roomlistViewStub);

        if (allEntries.size() != 0) {
            stub.setLayoutResource(R.layout.exp_list_view);
            stub.inflate();

            final ExpandableListView list = (ExpandableListView) findViewById(R.id.expListView);
            CustomResultExpListAdapter adapter = new CustomResultExpListAdapter(this, allEntries, categories);
            list.setAdapter(adapter);

            for (int i = 0; i < adapter.getGroupCount(); i++)
                list.expandGroup(i);

            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String value = String.valueOf(parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
                    value = value.substring("<font color='#000000'>".length());
                    value = value.split("<")[0];

                    Intent intent = new Intent(RoomlistActivity.this, NavigationActivity.class);
                    intent.putExtra("Ziel", value);
                    intent.putExtra("ParentClassName", RoomlistActivity.this.getClass().getSimpleName());
                    startActivity(intent);
                    return true;
                }
            });
        } else {
            stub.setLayoutResource(R.layout.roomlist_nothing_found_content);
            stub.inflate();
            if (darkBackground) {
                TextView textView = (TextView) findViewById(R.id.roomListNothingFoundTextView);
                textView.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
