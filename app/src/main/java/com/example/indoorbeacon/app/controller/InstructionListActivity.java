package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.view.adapter.CustomSimpleListAdapter;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        setContentView(R.layout.activity_instruction_list);
        if (darkBackground) {
            View root = getWindow().getDecorView().getRootView();
            root.setBackgroundColor(Color.parseColor(Definitions.DARK_BACKGROUND_COLOR));
        }

        ListView list = (ListView) findViewById(R.id.instructionListView);
        ArrayList<String> instructions = getIntent().getStringArrayListExtra("instructionList");
        String[] array = new String[instructions.size()];
        CustomSimpleListAdapter adapter = new CustomSimpleListAdapter(this, instructions.toArray(array));
        list.setAdapter(adapter);
    }
}
