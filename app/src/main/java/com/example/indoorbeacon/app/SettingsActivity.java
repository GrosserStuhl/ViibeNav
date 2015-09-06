package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import com.example.indoorbeacon.app.controller.ExportImportDB;

/**
 * Created by Dima on 28/07/2015.
 */
public class SettingsActivity extends Activity {

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
            // Load the preferences from an XML resource
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


