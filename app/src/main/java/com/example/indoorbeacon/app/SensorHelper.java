package com.example.indoorbeacon.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.indoorbeacon.app.model.Statistics;

import java.util.ArrayList;

/**
 * Created by Dima on 04/08/2015.
 */
public class SensorHelper {

    private static final String TAG = "SensorHelper";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;

    private int meter = 0;
    private int grad = 0;
    private TextView instructionText;
    private ImageView arrowImage;

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

        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (mStepCounterSensor == null)
            Log.d(TAG, "StepCounter is null!");
        if (mStepDetectorSensor == null)
            Log.d(TAG, "StepDetector is null!");
        if (!c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))
            Log.d(TAG, "StepDetector not supported!");
        if (!c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER))
            Log.d(TAG, "StepCounter not supported!");

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

        mSensorManager.registerListener(n, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(n, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPauseOperation(NavigationActivity n) {
        mSensorManager.unregisterListener(n, mAccelerometer);
        mSensorManager.unregisterListener(n, mMagnetometer);

        mSensorManager.unregisterListener(n, mStepCounterSensor);
        mSensorManager.unregisterListener(n, mStepDetectorSensor);
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
            animateImage(azimuthInDegrees);
        }

        if (event.sensor == mStepCounterSensor) {
            float[] values = event.values;
            int value = -1;
            if (values.length > 0) value = (int) values[0];
            instructionText.setText("Step Counter Detected : " + value);
        } else if (event.sensor == mStepDetectorSensor) {
            float[] values = event.values;
            int value = -1;
            if (values.length > 0) value = (int) values[0];
            // For test only. Only allowed value is 1.0 i.e. for step taken
            instructionText.setText("Step Detector Detected : " + value);
        }
    }

    private void animateImage(float degrees) {
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -degrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(250);
        ra.setFillAfter(true);

        arrowImage.startAnimation(ra);

        mCurrentDegree = -degrees;
        grad = (int) -mCurrentDegree;
        orientation = grad;
        String text = meter + " Meter" + System.lineSeparator() + grad + " Grad";
//        instructionText.setText(text);
    }
}
