package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.view.adapter.CustomResultExpListAdapter;

/**
 * Created by Dima on 15/09/2015.
 */
public class InstructionListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_instruction_list);

        ListView list = (ListView) findViewById(R.id.instructionListView);
        String[] array = new String[]{"hallo", "ballo", "kallo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_simple_listitem,
                R.id.instructionListTextView, array);
        list.setAdapter(adapter);
    }
}
