package com.example.indoorbeacon.app.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indoorbeacon.app.R;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.dbmodels.InfoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Dima on 05/09/2015.
 */
public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    private Coordinate target;
    private Coordinate nextSubTarget;
    private Coordinate previousPos;
    private LinkedList<Coordinate> path;
    private HashMap<Coordinate, InfoModel> infoTextsForAnchors;
    private LinkedList<Range> ranges;
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
        setUpBrReceiver(context);
    }

    private void initNavigation(String ziel) {
        distance = 0;
        distanceUnit = " m";
        previousOrientation = -1;
        firstDirection = 90;
        previousDirection = 0;
        directionUnit = " °";

        target = DBHandler.getDB().getTarget(ziel);
//        path = DBHandler.getDB().getAllAnchors();
        path = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            path.add(new Coordinate(-1, 0, i));
        }
        for (int i = 1; i < 4; i++) {
            path.add(new Coordinate(-1, i, 3));
        }
        for (int i = 2; i > -1; i--) {
            path.add(new Coordinate(-1, 3, i));
        }
        infoTextsForAnchors = DBHandler.getDB().getCoordinateToInfoModelMap();
        ranges = new LinkedList<>();
        dividePathIntoRanges();
    }

    private void dividePathIntoRanges() {
        int counter = 0;
        int newRangeStart = 0;
        double lastX = 0;
        double lastY = 0;
        boolean nextNavIsAlong_X_Axis = false;
        boolean nextNavIsAlong_Y_Axis = false;
        Coordinate startPos = path.getFirst();
        Coordinate secondPos = path.get(1);
        if (secondPos.getX() > startPos.getX()) {
            nextNavIsAlong_X_Axis = true;
            lastX = secondPos.getX();
            counter++;
        } else if (secondPos.getY() > startPos.getY()) {
            nextNavIsAlong_Y_Axis = true;
            lastY = secondPos.getY();
            counter++;
        }

        for (int i = 2; i < path.size(); i++) {
            if (nextNavIsAlong_X_Axis) {
                if (path.get(i).getX() > lastX || path.get(i).getX() < lastX) {
                    lastX = path.get(i).getX();
                    Log.d(TAG, "x-if");
                } else {
                    Log.d(TAG, "x-else");
                    ArrayList<Coordinate> rangeCoords = new ArrayList<>();
                    for (int j = newRangeStart; j <= counter; j++) {
                        rangeCoords.add(path.get(j));
                    }
                    ranges.add(new Range(rangeCoords, Range.NONE, Range.NONE));
                    //Counter + 1, da counter erst am ende der Schleife hochgezählt wird
                    newRangeStart = counter + 1;
                    nextNavIsAlong_X_Axis = false;
                    nextNavIsAlong_Y_Axis = true;
                }
            } else if (nextNavIsAlong_Y_Axis) {
                if (path.get(i).getY() > lastY || path.get(i).getY() < lastY) {
                    lastY = path.get(i).getY();
                    Log.d(TAG, "y-if");
                } else {
                    Log.d(TAG, "y-else");
                    ArrayList<Coordinate> rangeCoords = new ArrayList<>();
                    for (int j = newRangeStart; j <= counter; j++) {
                        rangeCoords.add(path.get(j));
                    }
                    ranges.add(new Range(rangeCoords, Range.NONE, Range.NONE));
                    //Counter + 1, da counter erst am ende der Schleife hochgezählt wird
                    newRangeStart = counter + 1;
                    nextNavIsAlong_Y_Axis = false;
                    nextNavIsAlong_X_Axis = true;
                }
            }
            counter++;
            if (i == path.size() - 1) {
                ArrayList<Coordinate> rangeCoords = new ArrayList<>();
                for (int j = newRangeStart; j <= counter; j++) {
                    rangeCoords.add(path.get(j));
                }
                ranges.add(new Range(rangeCoords, Range.NONE, Range.NONE));
            }
        }

        for (int i = 0; i < ranges.size(); i++) {
            Log.d(TAG, "Range #" + i + ":");
            for (Coordinate c : ranges.get(i)) {
                Log.d(TAG, c.toString());
            }
        }
    }

    private void setUpBrReceiver(Context context) {
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onPositionChangedAction();
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("person position changed"));
    }

    private void onPositionChangedAction() {
        Coordinate curPos = person.getCurrentPos();
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
