package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.indoorbeacon.app.model.BluetoothScan;
import com.example.indoorbeacon.app.model.Connector;

/**
 * Created by #Dima on 28/07/2015.
 */
public class NavigationActivity extends Activity implements SensorEventListener {

    private static final String TAG = "NavigationActivity";

    private SensorHelper sensorHelper;

    private GestureDetector mDetector;

    private MediaPlayer mp;
    private int[] anweisungen = new int[3];

    private ImageView dotImgView;
    private ImageView arrowImage;
    private TextView instructionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDetector = new GestureDetector(this, new MyGestureListener());

        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_navigation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        String ziel = intent.getStringExtra("Ziel");

        sensorHelper = SensorHelper.getSensorHelper(this);

        dotImgView = (ImageView) findViewById(R.id.walkIndicatorImgView);
        arrowImage = (ImageView) findViewById(R.id.arrowImageView);
        instructionTextView = (TextView) findViewById(R.id.instructionTextView);


        initGUI();
        initHandler();
    }

    private void initGUI() {

    }

    ;

    private void initHandler() {

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isWalking = intent.getBooleanExtra("isWalking", false);

                if (isWalking)
                    dotImgView.setImageResource(R.drawable.green_dot);
                else
                    dotImgView.setImageResource(R.drawable.red_dot);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("walking boolean changed"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sensorHelper.updateImage(arrowImage);
                sensorHelper.updateText(instructionTextView);
                new Handler().postDelayed(this, 250);
            }
        }, 250);


        anweisungen[0] = R.raw.anweisung1;
        anweisungen[1] = R.raw.anweisung2;
        anweisungen[2] = R.raw.anweisung3;
    }

    /**
     * By invoking this method you start median measurement for the beacons found nearby.
     * It will only start median measurement for the beacons already listed in the onyxBeaconHashMap.
     */
    public void startMeasurement() {
//        *TODO
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    protected void onResume() {
        super.onResume();
        sensorHelper.onResumeOperation(this);


        // Turn Off WiFi signals on activity start as it mitigates position estimation
        if (Connector.getConnector().WiFiEnabled())
            Connector.getConnector().disableWiFi();

        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (BluetoothScan.getBluetoothScan().getmBluetoothAdapter() == null || !BluetoothScan.getBluetoothScan().getmBluetoothAdapter().isEnabled()) {
            BluetoothScan.getBluetoothScan().getmBluetoothAdapter().enable();
            return;
        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Begin scanning for LE devices
        BluetoothScan.getBluetoothScan().startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!Connector.getConnector().WiFiEnabled())
            Connector.getConnector().enableWiFi();

        BluetoothScan.getBluetoothScan().getmBluetoothAdapter().disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!Connector.getConnector().WiFiEnabled())
            Connector.getConnector().enableWiFi();

        BluetoothScan.getBluetoothScan().getmBluetoothAdapter().disable();
    }

    protected void onPause() {
        super.onPause();
        sensorHelper.onPauseOperation(this);

        // When application is paused turn on WiFi again
        if (!Connector.getConnector().WiFiEnabled())
            Connector.getConnector().enableWiFi();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorHelper.onSensorChangedOperation(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    @Override
//    public Intent getSupportParentActivityIntent() {
//        Intent parentIntent = getIntent();
//        String className = parentIntent.getStringExtra("ParentClassName"); //getting the parent class name
//
//        Intent newIntent = null;
//        try {
//            //you need to define the class with package name
//            newIntent = new Intent(NavigationActivity.this, Class.forName("com.myapplication." + className));
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return newIntent;
//    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private int pointer = 0;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    Log.d("SWIPE GESTURES", "Swipe right");
                    Toast.makeText(NavigationActivity.this, "Swipe Right", Toast.LENGTH_SHORT).show();

                    if (pointer > 0) pointer--;
                } else {
                    Log.d("SWIPE GESTURES", "Swipe Left");
                    Toast.makeText(NavigationActivity.this, "Swipe Left", Toast.LENGTH_SHORT).show();
                    if (pointer < 2) {
                        pointer++;
                    }
                }

                if (mp != null) {
                    mp.release();
                    mp = null;
                }
                mp = MediaPlayer.create(NavigationActivity.this, anweisungen[pointer]);
                mp.start();

                return true;
            } else if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY < 0) {
                    Log.d("SWIPE GESTURES", "Swipe up");
                    Toast.makeText(NavigationActivity.this, "Swipe Up", Toast.LENGTH_SHORT).show();

                    if (mp != null) {
                        mp.release();
                        mp = null;
                    }
                    mp = MediaPlayer.create(NavigationActivity.this, anweisungen[pointer]);
                    mp.start();
                }
            }
            return false;
        }
    }
}
