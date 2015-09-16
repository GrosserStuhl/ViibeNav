package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import android.preference.PreferenceManager;
import android.widget.Toast;
import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.ExportImportDB;

/**
 * Created by Dima on 28/07/2015.
 */
public class SettingsActivity extends Activity {

    public static final String KEY_PREF_ORI = "pref_orientationUnit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private ExportImportDB expImpDB;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            expImpDB = new ExportImportDB(getActivity());

            Preference myPref = findPreference("showImportActivity");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    expImpDB.importDB();
                    return true;
                }
            });
        }
    }
}


