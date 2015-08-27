package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Arrays;

/**
 * Created by Dima on 27/07/2015.
 */
public class RoomlistActivity extends Activity {

    public static String[] categories = {"Büros", "Sekretäriat", "WCs"};
    public static String[] option1_items = {"Robert Tscharn", "Tobias Grundgeiger", "Diana Löffler"};
    public static String[] option2_items = {"Sandra Schubert"};
    public static String[] option3_items = {"WC1", "WC2", "WC3"};
    private String[] chosenCategory = null;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_raumliste);

        list = (ListView) findViewById(R.id.roomListView);
        CustomAdapter adapter = new CustomAdapter(this, categories);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = String.valueOf(parent.getItemAtPosition(position));
                if (Arrays.asList(categories).contains(value)) {

                    switch (value) {
                        case "Büros":
                            chosenCategory = option1_items;
                            break;
                        case "Sekretäriat":
                            chosenCategory = option2_items;
                            break;
                        case "WCs":
                            chosenCategory = option3_items;
                            break;
                    }

                    list.setAdapter(new CustomAdapter(RoomlistActivity.this, chosenCategory));
                    list.deferNotifyDataSetChanged();
                } else {
                    Intent intent = new Intent(RoomlistActivity.this, NavigationActivity.class);
                    intent.putExtra("Ziel", value);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (chosenCategory == null || chosenCategory == categories)
            super.onBackPressed();
        else {
            chosenCategory = categories;
            list.setAdapter(new CustomAdapter(RoomlistActivity.this, chosenCategory));
            list.deferNotifyDataSetChanged();
        }
    }
}
