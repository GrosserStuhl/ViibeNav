package com.example.indoorbeacon.app.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.TextView;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.controller.SettingsActivity;
import com.example.indoorbeacon.app.model.dbmodels.InfoModel;

/**
 * Created by Dima on 27/07/2015.
 */
public class CustomSimpleListAdapter extends ArrayAdapter<String> {
    private Activity activity;
    private String[] items;

    public CustomSimpleListAdapter(Activity activity, String[] items) {
        super(activity, R.layout.custom_simple_listitem, items);
        this.activity = activity;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_simple_listitem, parent, false);
        }

        TextView listElement = (TextView) convertView.findViewById(R.id.instructionListTextView);
        listElement.setText(items[position]);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        if (darkBackground)
            listElement.setTextColor(Color.WHITE);

        return convertView;
    }
}
