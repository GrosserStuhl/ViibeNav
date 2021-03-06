package com.example.indoorbeacon.app.model;

import android.content.*;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.indoorbeacon.app.controller.SettingsActivity;
import com.example.indoorbeacon.app.model.dbmodels.Database;
import com.example.indoorbeacon.app.model.dbmodels.InfoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Dima on 05/09/2015.
 */
public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    private Context context;
    private Coordinate target;
    private LinkedList<Coordinate> path;
    private HashMap<Coordinate, InfoModel> infoTextsForAnchors;
    private LinkedList<Range> ranges;
    private Range currentRange;
    private ArrayList<Coordinate> lastUserPositions;
    private boolean firstInstruction = true;
    private ArrayList<String> instructionList;

    private TTS tts;
    private Person person;
    private ArrayList<String> instructionTexts;
    private float previousOrientation = -1;
    private float firstDirection = 0;
    private float previousDirection = 0;
    private String directionUnit;
    private String distanceUnit;
    private int directionDifference = 0;
    private boolean useEnvInfos;
    private BroadcastReceiver mMessageReceiver;

    public NavigationHelper(Context context, Person person, String ziel) {
        this.context = context;
        this.person = person;
        tts = TTS.getTTS(context);
        setUpBrReceiver();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        directionUnit = preferences.getString(SettingsActivity.KEY_PREF_ORI, "Grad");
        distanceUnit = preferences.getString(SettingsActivity.KEY_PREF_DIS, "Meter");
        useEnvInfos = preferences.getBoolean(SettingsActivity.KEY_PREF_ENV, true);
        initNavigation(ziel);
    }

    private void initNavigation(String ziel) {
        target = Database.getDB().getTarget(ziel);
        path = Database.getDB().getAllAnchors();
        infoTextsForAnchors = Database.getDB().getCoordinateToInfoModelMap();
        ranges = new LinkedList<>();
        lastUserPositions = new ArrayList<>();
        dividePathIntoRanges();
        fillInstructionList();
//        onPositionChangedAction();
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

        if (path.size() < 3) {
            ArrayList<Coordinate> rangeCoords = new ArrayList<>();
            for (int j = newRangeStart; j <= counter; j++) {
                rangeCoords.add(path.get(j));
            }
            ranges.add(new Range(rangeCoords, Range.NONE));
        } else {

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
                        ranges.add(new Range(rangeCoords, Range.NONE));
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
                        ranges.add(new Range(rangeCoords, Range.NONE));
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
                    ranges.add(new Range(rangeCoords, Range.NONE));
                }
            }
        }

        ranges.get(0).setRelationToNextRange(Range.LEFT);
//        ranges.get(1).setRelationToNextRange(Range.RIGHT);
        currentRange = ranges.getFirst();
        ranges.getLast().setRelationToNextRange(Range.LAST);

        adjustImageToNewRange();

        for (Range range : ranges) {
            for (Coordinate coord : range.getCoordList()) {
                if (infoTextsForAnchors.containsKey(coord) && !infoTextsForAnchors.get(coord).getEnvironment().trim().isEmpty()) {
                    range.addEnvironmentalInfos(infoTextsForAnchors.get(coord).getEnvironment());
                }
            }
        }

//        for (int i = 0; i < ranges.size(); i++) {
//            Log.d(TAG, "Range #" + i + ":");
//            String dir = "NONE";
//            if (ranges.get(i).getRelationToNextRange() == Range.LEFT)
//                dir = "links";
//            else if (ranges.get(i).getRelationToNextRange() == Range.RIGHT)
//                dir = "rechts";
//            if (!(ranges.get(i).getRelationToNextRange() == Range.LAST))
//                Log.d(TAG, "Nächste Range in Richtung: " + dir);
//            else
//                Log.d(TAG, "Das ist die letzte Range");
//            Log.d(TAG, ranges.get(i).toString());
//        }
    }

    private void setUpBrReceiver() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onPositionChangedAction();
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("person position changed"));
    }

    private void onPositionChangedAction() {
        if (firstInstruction) {
            firstInstruction = false;
            onNewRangeEntered();
        } else {
            Coordinate curPos = person.getCurrentPos();
            if (lastUserPositions.size() < Definitions.NUMBER_OF_NEEDED_POINTS_FOR_NEW_RANGE) {
                if (curPos.isValid()) {
                    lastUserPositions.add(curPos);
                    Log.d(TAG, "not enough positions, adding latest one to the list");
                } else Log.d(TAG, "not enough positions, BUT didn't add because this one was invalid");
            }
            if (lastUserPositions.size() == Definitions.NUMBER_OF_NEEDED_POINTS_FOR_NEW_RANGE) {
                boolean allInSameRange = true;
                Range tempRange = getRangeByCoord(lastUserPositions.get(lastUserPositions.size() - 1));
                Log.d(TAG, "index of range of last pos: " + ranges.indexOf(tempRange));
                int counterForSameRange = 1;
                for (int i = lastUserPositions.size() - 2; i >= 0; i--) {
                    if (tempRange.equals(getRangeByCoord(lastUserPositions.get(i)))) {
                        counterForSameRange++;
                    } else {
                        allInSameRange = false;
                        break;
                    }
                }

                if (allInSameRange) {
                    if (!tempRange.equals(currentRange)) {
                        for (Range range : ranges) {
                            if (range.equals(tempRange)) {
                                currentRange = range;
                                onNewRangeEntered();
                                Log.d(TAG, "new range set");
                                break;
                            }
                        }
                    }
                    lastUserPositions.clear();
                } else {
                    //size - (counterForSameRange + 1), da man für letztes Element eh schon size - 1 machen würde
                    //Und hier soll ja mit dem vorletzen Element angefangen werden
                    int counter = 0;
                    int deletionIndex = lastUserPositions.size() - (counterForSameRange + 1);
                    Iterator<Coordinate> it = lastUserPositions.iterator();
                    while (it.hasNext()) {
                        it.next();
                        if (counter <= deletionIndex) {
                            it.remove();
                        }
                        counter++;
                    }
                }
            }
        }
    }

    private void onNewRangeEntered() {
        adjustImageToNewRange();
        int index = ranges.indexOf(currentRange);
        tts.speak(instructionList.get(index));
    }

    private Range getRangeByCoord(Coordinate coord) {
        Range resultRange = null;
        for (Range range : ranges) {
            if (range.getCoordList().contains(coord)) {
                resultRange = range;
                break;
            }
        }
        if (resultRange == null) {
            Log.e(TAG, "resultRange still null");
        }
        return resultRange;
    }

    public void updateTextViews(TextView directionTextView) {
        if (directionUnit.equals("Grad"))
            directionTextView.setText(directionDifference + " " + directionUnit);
        else if (directionUnit.equals("Uhrzeit")) {
            directionDifference = Util.convertDegreesToTime(directionDifference);
            directionTextView.setText(directionDifference + " Uhr");
        }
    }

    public void updateImage(ImageView arrowImage, float newOrientation) {
        if (previousOrientation == -1) {
            setupImage(arrowImage, newOrientation);
        } else {
            float orientationDifference = newOrientation - previousOrientation;
            float direction = previousDirection - orientationDifference;
            if (direction < 0) {
                direction = 360 + direction;
            } else if (direction > 360) {
                direction = direction - 360;
            }
//
            directionDifference = (int) direction;
//            if (directionDifference <= 10) {
//                if (previousDirection < 360 && previousDirection > 355 && direction > 0 && direction < 5)
//                    direction = 360;
//                else if (previousDirection > 0 && previousDirection < 5 && direction < 360 && direction > 355)
//                    direction = 0;
//            }

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

    private void setupImage(ImageView arrowImage, float initialOrientation) {
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

    private void adjustImageToNewRange() {
        previousOrientation = -1;

        if (currentRange.getRelationToNextRange() == Range.LEFT)
            firstDirection = 270;
        else if (currentRange.getRelationToNextRange() == Range.RIGHT)
            firstDirection = 90;
        else if (currentRange.getRelationToNextRange() == Range.NONE || currentRange.getRelationToNextRange() == Range.LAST)
            firstDirection = 0;
    }

    private void fillInstructionList() {
        instructionList = new ArrayList<>();

        for (Range range : ranges) {
            StringBuilder fullInstruction = new StringBuilder();
            int distance = 0;
            if (distanceUnit.equals("Meter"))
                distance = range.getApproximateDistanceInMeters();
            else if (distanceUnit.equals("Schritte"))
                distance = range.getApproximateDistanceInSteps();

            fullInstruction.append("Geradeaus circa ").append(distance);
            fullInstruction.append(" ").append(distanceUnit);

            if (useEnvInfos) {
                if (range.hasEnvironmentalInfos())
                    fullInstruction.append(", ");

                ArrayList<String> environmentalInfo = range.getEnvironmentalInfos();
                for (String anEnvironmentalInfo : environmentalInfo) {
                    String e = anEnvironmentalInfo.trim();
                    fullInstruction.append(e).append(",");
                }
            }
            fullInstruction.append(range.getRelationToNextRangeAsString());
            instructionList.add(fullInstruction.toString());
        }
    }

    public void nextInstruction() {
        int index = ranges.indexOf(currentRange);

//        Range nextRange;
        if (index != ranges.size() - 1) {
//            nextRange = ranges.get(index + 1);
//            ArrayList<String> environmentalInfo = nextRange.getEnvironmentalInfos();
//            instructionTexts = new ArrayList<>();
//            instructionTexts.add("Geradeaus");
//
//            for (String e : environmentalInfo)
//                instructionTexts.add(e);
//
//            instructionTexts.add(nextRange.getRelationToNextRangeAsString());
//            tts.speakList(instructionTexts, 0);
            tts.speak(instructionList.get(index + 1));
        } else {
            ArrayList<String> alternative = new ArrayList<>();
            alternative.add("Danach haben Sie ihr Ziel erreicht. Es gibt keine nächste Anweisung.");
            tts.speakList(alternative, 0);
        }
    }

    public void previousInstruction() {
        int index = ranges.indexOf(currentRange);

//        Range previousRange;
        if (index != 0) {
//            previousRange = ranges.get(index - 1);
//            ArrayList<String> environmentalInfo = previousRange.getEnvironmentalInfos();
//            instructionTexts = new ArrayList<>();
//            instructionTexts.add("Geradeaus");
//
//            for (String e : environmentalInfo)
//                instructionTexts.add(e);
//
//            instructionTexts.add(previousRange.getRelationToNextRangeAsString());
//            tts.speakList(instructionTexts, 0);
            tts.speak(instructionList.get(index - 1));
        } else {
            ArrayList<String> alternative = new ArrayList<>();
            alternative.add("Die Navigation hat hier begonnen. Es gibt noch keine vorherige Anweisung.");
            tts.speakList(alternative, 0);
        }
    }

    public void repeatInstruction() {
//        ArrayList<String> environmentalInfo = currentRange.getEnvironmentalInfos();
//        instructionTexts = new ArrayList<>();
//        instructionTexts.add("Geradeaus");
//
//        for (String e : environmentalInfo)
//            instructionTexts.add(e);
//
//        instructionTexts.add(currentRange.getRelationToNextRangeAsString());
//        tts.speakList(instructionTexts, 0);
        int index = ranges.indexOf(currentRange);
        tts.speak(instructionList.get(index));
    }

    public ArrayList<String> getInstructionList() {
        return instructionList;
    }

    public void killAllHandlers() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }
}
