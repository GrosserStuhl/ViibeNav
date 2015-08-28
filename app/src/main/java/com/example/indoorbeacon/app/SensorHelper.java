package com.example.indoorbeacon.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.indoorbeacon.app.model.Statistics;
import com.example.indoorbeacon.app.model.Util;

import java.util.ArrayList;

/**
 * Created by Dima on 04/08/2015.
 */
public class SensorHelper {
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnetometer;
    float[] mLastAccelerometer = new float[3];
    float[] mLastMagnetometer = new float[3];
    boolean mLastAccelerometerSet = false;
    boolean mLastMagnetometerSet = false;
    float[] mR = new float[9];
    float[] mOrientation = new float[3];
    float mCurrentDegree = 0f;

    int meter = 0;
    int grad = 0;
    TextView instructionText;
    ImageView arrowImage;

    private boolean calcInProgress = false;
    private long startTime = 0;
    private long curTime = 0;
    private boolean timeToAddValue = false;
    private ArrayList<Float> degreeValuesForMedian = new ArrayList<>();

    /**
     * Ausrichtung des Smartphones zum Nordpol
     */
    private static int orientation = 0;

    public static int getOrientation() {
        return orientation;
    }

    public SensorHelper(Context c, ImageView arrowImage, TextView instructionText) {
        mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.arrowImage = arrowImage;
        this.instructionText = instructionText;
        meter = 2;
        grad = 45;
        String text = meter + " Meter \n" + grad + " Grad";
        this.instructionText.setText(text);
    }

    public void onResumeOperation(NavigationActivity n) {
        mSensorManager.registerListener(n, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(n, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
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
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            if (!calcInProgress) {
                degreeValuesForMedian.add(azimuthInDegrees);
                startTime = System.currentTimeMillis();
                calcInProgress = true;
                startTimerThread();
            }
            if (timeToAddValue) {
                degreeValuesForMedian.add(azimuthInDegrees);
                if (degreeValuesForMedian.size() < 10) {
                    startTime = System.currentTimeMillis();
                    timeToAddValue = false;
                } else {
                    timeToAddValue = false;
                    calcInProgress = false;

                    float median = Statistics.calcMedianFromFloat(degreeValuesForMedian);
                    degreeValuesForMedian.clear();
                    animateImage(median);
                }
            }
        }
    }

    private void animateImage(float degrees) {
        if (mCurrentDegree > 350 && mCurrentDegree < 359 && degrees > 0 && degrees < 10) {
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            arrowImage.startAnimation(ra);

            ra = new RotateAnimation(
                    0,
                    degrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            arrowImage.startAnimation(ra);
        } else if (mCurrentDegree > 0 && mCurrentDegree < 10 && degrees > 350 && degrees < 359) {
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    0,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            arrowImage.startAnimation(ra);

            ra = new RotateAnimation(
                    360,
                    degrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            arrowImage.startAnimation(ra);
        } else {
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    degrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);

            arrowImage.startAnimation(ra);
        }
        mCurrentDegree = degrees;


        grad = (int) mCurrentDegree;
        orientation = grad;
        String text = meter + " Meter \n" + grad + " Grad";
        instructionText.setText(text);
    }

    private void startTimerThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (calcInProgress) {
                    curTime = System.currentTimeMillis();
                    long dif = curTime - startTime;
                    if (dif >= 15) timeToAddValue = true;
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}
