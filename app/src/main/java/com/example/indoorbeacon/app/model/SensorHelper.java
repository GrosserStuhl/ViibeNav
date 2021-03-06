package com.example.indoorbeacon.app.model;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.indoorbeacon.app.controller.NavigationActivity;

import java.util.ArrayList;

/**
 * Created by Dima on 04/08/2015.
 */
public class SensorHelper {

    private static final String TAG = "SensorHelper";

    private Context context;
    private SensorManager mSensorManager;
    private static SensorHelper singleton;

    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private float relativeZThreshold = 0;
    private float relativeYThreshold = 0;
    private final static float SONY_Z_MAXRANGE = 19.6133f;

    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;

    private boolean enoughTimeForStep = false;
    private int stepCount = 0;
    //Wird benutzt um zu überprüfen ob es mehere Schritte nacheinander gab
    private ArrayList<Boolean> multipleStepRegister = new ArrayList<>();
    private boolean isWalking = false;

    private int grad = 0;

    /**
     * Ausrichtung des Smartphones zum Nordpol
     */
    private float orientation = 0;

    public float getOrientation() {
        return orientation;
    }

    public boolean isWalking() {
        return isWalking;
    }


    public static SensorHelper getSensorHelper(Context c) {
        synchronized (SensorHelper.class) {
            if (singleton == null)
                singleton = new SensorHelper(c);
        }
        return singleton;
    }


    public SensorHelper(Context c) {
        context = c;
        initializeAllSensors();
    }

    private void initializeAllSensors() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        float maxRangeMultRatio = SONY_Z_MAXRANGE / mAccelerometer.getMaximumRange();
        relativeZThreshold = Definitions.STEP_THRESHOLD_Z * maxRangeMultRatio;
        relativeYThreshold = Definitions.STEP_THRESHOLD_Y * maxRangeMultRatio;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!enoughTimeForStep)
                    enoughTimeForStep = true;
                else {
                    isWalking = false;
                    multipleStepRegister.clear();
                    broadcastChange();
                }
                new Handler().postDelayed(this, Definitions.TIME_FOR_STEP);
            }
        }, Definitions.TIME_FOR_STEP);
    }

    public void onResumeOperation(NavigationActivity n) {
        mSensorManager.registerListener(n, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(n, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPauseOperation(NavigationActivity n) {
        mSensorManager.unregisterListener(n, mAccelerometer);
        mSensorManager.unregisterListener(n, mMagnetometer);
    }

    public void onSensorChangedOperation(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            orientation = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
        }

        if (event.sensor == mAccelerometer
                && enoughTimeForStep) {

            final float alpha = 0.8f; // constant for our filter below

            float[] gravity = {0, 0, 0};

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            float x = event.values[0] - gravity[0];
            float y = event.values[1] - gravity[1];
            float z = event.values[2] - gravity[2];

            if (!mLastAccelerometerSet) {
                // sensor is used for the first time, initialize the last read values
                mLastX = x;
                mLastY = y;
                mLastZ = z;
            } else {
                // sensor is already initialized, and we have previously read values.
                // take difference of past and current values and decide which
                // axis acceleration was detected by comparing values

                float deltaX = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);
                mLastX = x;
                mLastY = y;
                mLastZ = z;

                if (deltaZ > relativeZThreshold && deltaY > relativeYThreshold) {
                    enoughTimeForStep = false;
                    stepCount = stepCount + 1;
                    multipleStepRegister.add(true);
                    if (multipleStepRegister.size() >= Definitions.MIN_STEP_AMOUNT_FOR_WALKING) {
                        isWalking = true;
                        broadcastChange();
                    }
                }
            }
        }
    }


    public void onAccuracyChangedOperation(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.d(TAG, "Magnetic Field status: unreliable");
        }
    }

    private void broadcastChange() {
        Intent intent = new Intent("walking boolean changed");
        intent.putExtra("isWalking", isWalking);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
