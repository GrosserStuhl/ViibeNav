package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.dbmodels.InfoDBModel;
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
        setContentView(R.layout.activity_raumliste);

        HashMap<String, List<String>> allEntries = new HashMap<>();
        ArrayList<String> categories = DBHandler.getDB().getAllDistinctCategories();
        if (categories.size() > 0) {
            for (String category : categories) {
                ArrayList<InfoDBModel> infoList = DBHandler.getDB().getAllEntriesForSpecificCategory(category);
                if (infoList.size() > 0) {
                    ArrayList<String> entryList = new ArrayList<>();
                    for (InfoDBModel infoEntry : infoList) {
                        entryList.add("<font color='#000000'>" + infoEntry.getPerson_name() +
                                "</font><br/><font color='#006400'>" + infoEntry.getRoom_name() + "</font>");
                    }
                    allEntries.put(category, entryList);
                }
            }
        }

        ViewStub stub = (ViewStub) findViewById(R.id.roomlistViewStub);

        if (allEntries.size() != 0) {
            stub.setLayoutResource(R.layout.roomlist_exp_list);
            stub.inflate();

            final ExpandableListView list = (ExpandableListView) findViewById(R.id.roomExpListView);
            CustomResultExpListAdapter adapter = new CustomResultExpListAdapter(this, allEntries, categories);
            list.setAdapter(adapter);

            list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String value = String.valueOf(parent.getExpandableListAdapter().getChild(groupPosition, childPosition));
                    value = value.substring("<font color='#000000'>".length());
                    value = value.split("<")[0];
//
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
