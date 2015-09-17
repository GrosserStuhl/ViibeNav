package com.example.indoorbeacon.app.controller;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.*;

import java.util.ArrayList;

/**
 * Created by #Dima on 28/07/2015.
 */
public class NavigationActivity extends Activity implements SensorEventListener {

    private static final String TAG = "NavigationActivity";

    private SensorHelper sensorHelper;
    private NavigationHelper navigationHelper;

    private GestureDetector mDetector;

    private ImageView dotImgView;
    private ImageView arrowImage;
    private TextView directionTextView;
    private TextView estimatedCoordTextView;
    private TextView estimatedAlgorithm;

    private boolean navigating;
    private Person person;
    private Handler triggerMeasuring;

    private boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDetector = new GestureDetector(this, new MyGestureListener());

        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean invertColors = preferences.getBoolean(SettingsActivity.KEY_PREF_INV, false);
        setContentView(R.layout.activity_navigation);
        if(invertColors){
            View someView = findViewById(R.id.arrowImageView);
            View root = someView.getRootView();
            root.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        String ziel = intent.getStringExtra("Ziel");
        Log.d(TAG, "Ziel: " + ziel);

        person = new Person(this);
        navigationHelper = new NavigationHelper(this, person, ziel);
        sensorHelper = SensorHelper.getSensorHelper(this);

        initGUI();
        initHandler();
        startMeasurementLoop();
    }

    private void initGUI() {
        dotImgView = (ImageView) findViewById(R.id.walkIndicatorImgView);
        arrowImage = (ImageView) findViewById(R.id.arrowImageView);
        directionTextView = (TextView) findViewById(R.id.instructionTextView);
        estimatedCoordTextView = (TextView) findViewById(R.id.estimatedCoords);
        estimatedAlgorithm = (TextView) findViewById(R.id.estimatedAlgorithm);
    }

    private void initHandler() {
        triggerMeasuring = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                initialized = false;
                person.getMostLikelyPosition();
            }
        };

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
                navigationHelper.updateImage(arrowImage, sensorHelper.getOrientation());
                navigationHelper.updateTextViews(directionTextView);
                new Handler().postDelayed(this, 250);
            }
        }, 250);

        // setup GUI updates + new Measurements
        BroadcastReceiver mCoordReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean startedMeasuring = intent.getBooleanExtra("startedMeasuring", false);
                if (!startedMeasuring) {
                    if (!initialized) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                estimatedCoordTextView.setText(person.getCurrentPos().toString());
                                estimatedAlgorithm.setText(person.getCurrentPosAlgorithm().toString());
                                triggerMeasuring.sendEmptyMessage(0);
                            }
                        }, 1500);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mCoordReceiver,
                new IntentFilter("measuring boolean changed"));

    }

    /**
     * By invoking this method you start median measurement for the beacons found nearby.
     * It will only start median measurement for the beacons already listed in the onyxBeaconHashMap.
     */
    public void startMeasurementLoop() {
        navigating = true;
        // trigger measurement loop
        triggerMeasuring.sendEmptyMessage(0);
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
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!Connector.getConnector().WiFiEnabled())
            Connector.getConnector().enableWiFi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!Connector.getConnector().WiFiEnabled())
            Connector.getConnector().enableWiFi();
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
        sensorHelper.onAccuracyChangedOperation(sensor, accuracy);
    }

    public void readOutInstructions(View view) {
        TTS.getTTS(this).speak(getResources().getString(R.string.infoInstructions));
    }

    public void openInstructionListActivity(View view) {
        Intent intent = new Intent(this, InstructionListActivity.class);
        intent.putStringArrayListExtra("instructionList", navigationHelper.getInstructionList());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

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
                    navigationHelper.previousInstruction();
                } else {
                    navigationHelper.nextInstruction();
                }
                return true;

            } else if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY < 0) {
                    navigationHelper.repeatInstruction();
                }
            }
            return false;
        }
    }
}
