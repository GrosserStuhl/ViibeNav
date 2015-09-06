package com.example.indoorbeacon.app.model;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;


/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class BluetoothScan {
    public static final String TAG = "BluetoothScan";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private static BluetoothScan singleton;
    private Advertisement advert;
    private BroadcastReceiver mReceiver;

    private BluetoothScan(Activity act) {
        advert = new Advertisement(act.getApplicationContext());

        BluetoothManager manager = (BluetoothManager) act.getSystemService(act.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = manager.getAdapter();
        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        turnOnBluetooth();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            turnOnBluetooth();
                            Log.d(TAG, "Bt OFF.");
//                            setButtonText("Bluetooth off");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            turnOnBluetooth();
                            Log.d(TAG, "Bt turning OFF.");
//                            setButtonText("Turning Bluetooth off...");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "Bt ON.");
                            startScan();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "Bt turning ON.");
//                            setButtonText("Turning Bluetooth on...");
                            break;
                    }
                }
            }
        };


        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        act.registerReceiver(mReceiver, filter);
    }

    public static BluetoothScan getBtScan(Activity act){
        synchronized (BluetoothScan.class){
            if(singleton == null)
                singleton = new BluetoothScan(act);
            return singleton;
        }
    }

    private void startScan(){
        // Scan for devices advertising the thermometer
        // does not work
//        ScanFilter beaconFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(Advertisement.filterUUID)).build();
//        // works
//        ScanFilter filter2 = new ScanFilter.Builder().setDeviceAddress(tagMAC).build();
//        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
//        // does not work
//        // filters.add(beaconFilter);
//        filters.add(filter2);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        Log.d(TAG,"Bluetooth enabled: "+mBluetoothAdapter.isEnabled() + " Try to start scanning...");
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
            OnyxBeacon beacon = advert.extractAD(result.getDevice().getAddress(), result.getRssi(), result.getScanRecord().getBytes());

//            Message msg = Message.obtain();
//            msg.obj = beacon;
//            mHandler.sendMessage(msg);
        }
    };

    public void turnOnBluetooth(){
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            //Bluetooth is disabled
////            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivity(enableBtIntent);
//            mBluetoothAdapter.enable();
//        }

        if(!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
        else
            startScan();
    }

    private boolean isSetup(){
        Log.d(TAG,"BTAdapter null "+(mBluetoothAdapter == null));
        Log.d(TAG,"BTAdapter enabled "+(mBluetoothAdapter.isEnabled()));
        Log.d(TAG,"BTScanner scanner "+(mBluetoothLeScanner == null));

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null)
            return true;
        return false;
    }

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
