package com.example.indoorbeacon.app.model;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;

/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class BluetoothScan {

    public static final String TAG = "BluetoothScan";

    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothScan(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mHandler = standardHandler;

//        listAdapter = new CustomListAdapter(this);
//        ListView beaconListView = (ListView) findViewById(R.id.myListView);
//        beaconListView.setAdapter(listAdapter);
    }

    public void startScan(){
        // Scan for devices advertising the thermometer
        // does not work
//        ScanFilter beaconFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(Advertisement.filterUUID)).build();
//        // works
//        ScanFilter filter2 = new ScanFilter.Builder().setDeviceAddress(tagMAC).build();
//        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
//        // does not work
//        // filters.add(beaconFilter);
//        filters.add(filter2);

        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        mBluetoothLeScanner.startScan(null, settings, mScanCallback);

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            Log.d(TAG, "onScanResult");
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
//            Log.d(TAG, "onBatchScanResults: "+results.size()+" results");
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "LE Scan Failed: " + errorCode);
        }

        private void processResult(ScanResult result) {
//            Log.d(TAG, "New LE Device: " + result.getDevice().getName() + " @ " + result.getRssi());

            /*
             * Create a new beacon from the list of obtains AD structures
             * and pass it up to the main thread if it is not already listed in OnyxBeacon.onyxBeaconHashmap
             */
            OnyxBeacon beacon = Advertisement.extractAD(result.getDevice().getAddress(), result.getRssi(), result.getScanRecord().getBytes());


            if(beacon != null)
                beacon.checkState();


            Message msg = Message.obtain();
//            msg.obj = beacon;
//            mHandler.sendMessage(msg);
            mHandler.sendEmptyMessage(0);
        }
    };

    public void stopScan() {
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    /*
     * We have a Handler to process scan results on the main thread,
     * add them to our list adapter, and update the view
     */
    public Handler standardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            OnyxBeacon msgStr = (OnyxBeacon) msg.obj;
//            applicationUI.updateLayer2();

        }
    };

    public Handler mHandler;

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }
}
