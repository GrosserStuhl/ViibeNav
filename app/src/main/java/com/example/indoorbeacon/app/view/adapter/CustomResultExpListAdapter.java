package com.example.indoorbeacon.app.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.controller.SettingsActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dima on 31/08/2015.
 */
public class CustomResultExpListAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private HashMap<String, List<String>> results;
    private List<String> categories;

    public CustomResultExpListAdapter(Activity activity, HashMap<String, List<String>> results, List<String> categories) {
        this.activity = activity;
        this.results = results;
        this.categories = categories;
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return results.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return results.get(categories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.exp_list_view_parent, parent, false);
        }

        String parentText = (String) getGroup(groupPosition);
        TextView parentTextView = (TextView) convertView.findViewById(R.id.resultsExpListParentTextView);
        parentTextView.setText(parentText);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean darkBackground = preferences.getBoolean(SettingsActivity.KEY_PREF_DRK, false);
        if(darkBackground)
            parentTextView.setTextColor(Color.WHITE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.custom_listitem, parent, false);
        }

        String childText = (String) getChild(groupPosition, childPosition);
        Button childButton = (Button) convertView.findViewById(R.id.roomlistButton);
        childButton.setText(Html.fromHtml(childText));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
