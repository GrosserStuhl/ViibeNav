package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.AnchorPointDBModel;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.dbmodels.MedianDBModel;
import com.example.indoorbeacon.app.model.dbmodels.OnyxBeaconDBModel;
import com.example.indoorbeacon.app.view.adapter.CustomListAnchorAdapter;
import com.example.indoorbeacon.app.view.adapter.CustomListBeaconAdapter;
import com.example.indoorbeacon.app.view.adapter.CustomListMedianAdapter;

import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by TomTheBomb on 21.07.2015.
 */
public class TestAreaActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TestAreaActivity";


    private ScheduledExecutorService exec;

    Person person;
    Button test;
    String[] tables;
    Spinner spinner;
    private int selectedItem;


    CustomListAnchorAdapter customListAnchorAdapter;
    CustomListBeaconAdapter customListBeaconAdapter;
    CustomListMedianAdapter customListMedianAdapter;

    ListView listView;
    TextView tableHeader;

    TextView testCoords;
    CheckBox loop;

    boolean loopTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        customListAnchorAdapter = new CustomListAnchorAdapter(this);
        customListBeaconAdapter = new CustomListBeaconAdapter(this);
        customListMedianAdapter = new CustomListMedianAdapter(this);

        listView = (ListView) findViewById(R.id.listView);
        tableHeader = (TextView) findViewById(R.id.tableHeader);

        testCoords = (TextView) findViewById(R.id._testCoords);
        loop = (CheckBox) findViewById(R.id.loop);
        selectedItem = -1;

        initSpinner();

        loopTest = false;

        test = (Button) findViewById(R.id.test);
        person = new Person(this);

    }

    private void initSpinner(){
        tables = new String[3];
        tables[0] = "AnchorPoint";
        tables[1] = "Beacon";
        tables[2] = "Median";

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, tables);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void startTest(View view){
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                exec = Executors.newSingleThreadScheduledExecutor();
//                exec.scheduleAtFixedRate(new Runnable() {
//                    @Override
//                    public void run() {
//                        getMostLikelyPositionHandler.sendEmptyMessage(0);
//                    }
//                }, 0, 1100, TimeUnit.MILLISECONDS);
//            }
//        };
//        Thread th = new Thread(r);
//        th.start();



        // start one time measuring
        Runnable r = new Runnable(){
            @Override
            public void run() {

                    getMostLikelyPositionHandler.sendEmptyMessage(0);

            }
        };
        Thread th = new Thread(r);
        th.start();

    }

    public void deleteTables(View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete all tables?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DBHandler.getDB().deleteAllTables();
                    RadioMap.getData().clear();

                    switch(selectedItem){
                        case 0:
                            anchorListHandler.sendEmptyMessage(0);
                            break;
                        case 1:
                            beaconListHandler.sendEmptyMessage(0);
                            break;
                        case 2:
                            medianListHandler.sendEmptyMessage(0);
                            break;
                    }

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
    }

    public void updateLikelyCoordsView(){
        testCoords.setText("x: "+person.getCoord().getX()+" y: "+person.getCoord().getY()+" f: "+person.getCoord().getFloor());
    }

    private Handler getMostLikelyPositionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // clear Beacon map to make sure only beacons from the newest execution cycle are taken into account
//            OnyxBeacon.clearMap();
//            NOT SURE IF WORKS
            person.getMostLikelyPosition();
        }
    };

    public void triggerLoop(View view){
        if(loop.isChecked())
            loopTest = true;
        else
            loopTest = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int selected = spinner.getSelectedItemPosition();
        switch(selected){
            case 0:
                listView.setAdapter(customListAnchorAdapter);
                tableHeader.setText("  x  |" + "y  |" + "f  |" + "B1  |" + "B2  |" + "B3  |" + "B4  |");
                anchorListHandler.sendEmptyMessage(0);
                selectedItem = 0;
                break;
            case 1:
                listView.setAdapter(customListBeaconAdapter);
                tableHeader.setText("  major  |" + "minor  |" + "macAddress  ");
                beaconListHandler.sendEmptyMessage(0);
                selectedItem = 1;
                break;
            case 2:
                listView.setAdapter(customListMedianAdapter);
                tableHeader.setText("  median  |" + "macAddress  ");
                medianListHandler.sendEmptyMessage(0);
                selectedItem = 2;
                break;
        }
    }

    private Handler anchorListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DBHandler.getDB().getAllAnchors();

            customListAnchorAdapter.clear();
            customListAnchorAdapter.addAll(AnchorPointDBModel.getAllAnchors());
            customListAnchorAdapter.notifyDataSetChanged();
        }
    };

    private Handler beaconListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DBHandler.getDB().getAllBeacons();

            customListBeaconAdapter.clear();
            customListBeaconAdapter.addAll(OnyxBeaconDBModel.getAllBeacons());
            customListBeaconAdapter.notifyDataSetChanged();
        }
    };

    private Handler medianListHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DBHandler.getDB().getAllMedians();

            customListMedianAdapter.clear();
            customListMedianAdapter.addAll(MedianDBModel.getAllMedians());
            customListMedianAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Connector.getConnector().WiFiEnabled())
            Connector.getConnector().disableWiFi();
    }

    public boolean isLoopTest() {
        return loopTest;
    }
}
