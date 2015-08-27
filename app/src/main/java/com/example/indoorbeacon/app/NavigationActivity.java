package com.example.indoorbeacon.app;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dima on 28/07/2015.
 */
public class NavigationActivity extends Activity implements SensorEventListener {

    private SensorHelpClass sensorHelpClass;

    private GestureDetector mDetector;

    private MediaPlayer mp;
    private int[] anweisungen = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDetector = new GestureDetector(this, new MyGestureListener());

        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_navigation);

        Intent intent = getIntent();
        String ziel = intent.getStringExtra("Ziel");

        ImageView arrowImage = (ImageView) findViewById(R.id.arrowImageView);
        TextView instructionText = (TextView) findViewById(R.id.instructionTextView);
        sensorHelpClass = new SensorHelpClass(this, arrowImage, instructionText);

        anweisungen[0] = R.raw.anweisung1;
        anweisungen[1] = R.raw.anweisung2;
        anweisungen[2] = R.raw.anweisung3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    protected void onResume() {
        super.onResume();
        sensorHelpClass.onResumeOperation(this);
    }

    protected void onPause() {
        super.onPause();
        sensorHelpClass.onPauseOperation(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorHelpClass.onSensorChangedOperation(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private long time = 0L;
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

                    if (pointer > 0) {
                        if (time == 0L) {
                            time = System.currentTimeMillis();
                        } else {
                            long curTime = System.currentTimeMillis();
                            if (curTime - time < 3000L) {
                                pointer--;
                                time = System.currentTimeMillis();
                            } else time = 0L;
                        }
                    }

                } else {
                    Log.d("SWIPE GESTURES", "Swipe Left");
                    Toast.makeText(NavigationActivity.this, "Swipe Left", Toast.LENGTH_SHORT).show();
                    if (pointer < 2) {
                        pointer++;
                        time = 0L;
                    }
                }

                if (mp != null) {
                    mp.release();
                    mp = null;
                }
                mp = MediaPlayer.create(NavigationActivity.this, anweisungen[pointer]);
                mp.start();

                return true;
            }
            return false;
        }
    }
}
