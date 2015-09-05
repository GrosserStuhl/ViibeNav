package com.example.indoorbeacon.app;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.TTS;

import java.util.LinkedList;

/**
 * Created by Dima on 05/09/2015.
 */
public class NavigationHelper {
    private Coordinate goal;
    private LinkedList<Coordinate> path;
    private Coordinate nextPartGoal;
    private TTS tts;
    private String instructionText;
    private int distance;
    private float previousOrientation;
    private float orientationDifference;
    private String distanceUnit;
    private String directionUnit;

    public NavigationHelper(Context context) {
        distance = 0;
        distanceUnit = " m";
        previousOrientation = 0;
        orientationDifference = 0;
        directionUnit = " °";
        tts = TTS.createTTS(context);
    }

    public void updateTextViews(TextView distanceTextView, TextView directionTextView) {
        distanceTextView.setText(distance + distanceUnit);
//        directionTextView.setText(orientationDifference + directionUnit);
    }

    public void updateImage(ImageView arrowImage, float newOrientation) {
        orientationDifference = previousOrientation - newOrientation;
        float targetOrientation = previousOrientation + orientationDifference;

        RotateAnimation ra = new RotateAnimation(
                previousOrientation,
                targetOrientation,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(250);
        ra.setFillAfter(true);
        arrowImage.startAnimation(ra);

        previousOrientation = newOrientation;
    }

    public void nextInstruction() {
        tts.speak("Nächste Anweisung");
    }

    public void previousInstruction() {
        tts.speak("Vorherige Anweisung");
    }

    public void repeatInstruction() {
        tts.speak("Wiederhole Anweisung");
    }
}
