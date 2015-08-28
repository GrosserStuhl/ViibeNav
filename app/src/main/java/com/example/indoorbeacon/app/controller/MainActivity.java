package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.RoomlistActivity;
import com.example.indoorbeacon.app.SearchActivity;
import com.example.indoorbeacon.app.SettingsActivity;
import com.example.indoorbeacon.app.model.BluetoothScan;
import com.example.indoorbeacon.app.model.Connector;
import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.RadioMap;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;


public class MainActivity extends Activity {

    Application applicationUI;
    RadioMap radioMap;
    BluetoothScan bluetoothScan;
    DBHandler dbHandler;

    Connector connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main);

        radioMap = new RadioMap(new Coordinate(0,0,0));
        dbHandler = DBHandler.createDB(this, null, null, 1);
        applicationUI = new Application(this);

        connect = Connector.createConnector((WifiManager) getSystemService(WIFI_SERVICE));


        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothScan = new BluetoothScan(manager.getAdapter());
    }

    public void openSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openListActivity(View view) {
        Intent intent = new Intent(this, RoomlistActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}