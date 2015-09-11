package com.example.indoorbeacon.app;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.Person;
import com.example.indoorbeacon.app.model.TTS;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Dima on 05/09/2015.
 */
public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    private Coordinate goal;
    private Coordinate nextSubGoal;
    private Coordinate previousPos;
    private LinkedList<Coordinate> path;
    private HashMap<Coordinate, String> infoTextsForAnchors;
    private TTS tts;
    private Person person;
    private String instructionText;
    private int distance;
    private float previousOrientation;
    private float firstDirection;
    private float previousDirection;
    private String distanceUnit;
    private String directionUnit;

    public NavigationHelper(Context context, Person person, String ziel) {
        this.person = person;
        tts = TTS.createTTS(context);
        initNavigation(ziel);
    }

    private void initNavigation(String ziel) {
        distance = 0;
        distanceUnit = " m";
        previousOrientation = -1;
        firstDirection = 90;
        previousDirection = 0;
        directionUnit = " °";

        //Hier Datenbankabfrage, in info-table nach dem ziel suchen und ID der info bekommen
        // dann in anchortabelle nach dem anchor suchen der die infoID hat
        goal = new Coordinate(-1, -1, -1);
        path = new LinkedList<>(); //Hier DB-Abfrage um alle Anchorpoints in Reihenfolgezu bekommen
        infoTextsForAnchors = new HashMap<>(); //Alle Koordinaten reinspeichern, die einen Info text haben
        calculateNewSubGoal();
    }

    private void calculateNewSubGoal() {

    }

    public void updateTextViews(TextView distanceTextView, TextView directionTextView) {
        distanceTextView.setText(distance + distanceUnit);
//        directionTextView.setText(orientationDifference + directionUnit);
    }

    public void updateImage(ImageView arrowImage, float newOrientation) {
        if (previousOrientation == -1) {
            setupImage(arrowImage, newOrientation);
//            Log.d(TAG, "setupImage: newOr: " + newOrientation);
        } else {
//            Log.d(TAG, "prevOr: " + previousOrientation + ", newOr: " + newOrientation);
            float orientationDifference = newOrientation - previousOrientation;
            float direction = previousDirection - orientationDifference;
            if (direction < 0) {
                direction = 360 + direction;
            } else if (direction > 360) {
                direction = direction - 360;
            }

//            Log.d(TAG, "prevDir: " + previousDirection + ", newDir: " + direction);

            RotateAnimation ra = new RotateAnimation(
                    previousDirection,
                    direction,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);
            arrowImage.startAnimation(ra);

            previousOrientation = newOrientation;
            previousDirection = direction;
        }
    }

    public void setupImage(ImageView arrowImage, float initialOrientation) {
        RotateAnimation ra = new RotateAnimation(
                0,
                firstDirection,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(250);
        ra.setFillAfter(true);
        arrowImage.startAnimation(ra);

        previousDirection = firstDirection;
        previousOrientation = initialOrientation;
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
