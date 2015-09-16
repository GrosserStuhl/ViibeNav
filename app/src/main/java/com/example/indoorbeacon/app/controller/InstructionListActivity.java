package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.indoorbeacon.app.R;

import java.util.ArrayList;

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
        ArrayList<String> instructions = getIntent().getStringArrayListExtra("instructionList");
        String[] array = new String[instructions.size()];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_simple_listitem,
                R.id.instructionListTextView, instructions.toArray(array));
        list.setAdapter(adapter);
    }
}
