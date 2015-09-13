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

import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
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

    private Coordinate target;
    private Coordinate nextSubTarget;
    private Coordinate previousPos;
    private LinkedList<Coordinate> path;
    private HashMap<Coordinate, InfoModel> infoTextsForAnchors;
    private LinkedList<Range> ranges;
    private Range currentRange;
    private ArrayList<Coordinate> lastUserPositions;
    private boolean firstInstruction = true;

    private TTS tts;
    private Person person;
    private ArrayList<String> instructionTexts;
    private float previousOrientation = -1;
    private float firstDirection = 0;
    private float previousDirection = 0;
    private String directionUnit = " °";
    private int directionDifference = 0;

    public NavigationHelper(Context context, Person person, String ziel) {
        this.person = person;
        tts = TTS.getTTS(context);
        initNavigation(ziel);
        setUpBrReceiver(context);
    }

    private void initNavigation(String ziel) {
        target = DBHandler.getDB().getTarget(ziel);
        path = DBHandler.getDB().getAllAnchors();
//        path = new LinkedList<>();
//        for (int i = 0; i < 4; i++) {
//            path.add(new Coordinate(-1, 0, i));
//        }
//        for (int i = 1; i < 4; i++) {
//            path.add(new Coordinate(-1, i, 3));
//        }
//        for (int i = 2; i > -1; i--) {
//            path.add(new Coordinate(-1, 3, i));
//        }
        infoTextsForAnchors = DBHandler.getDB().getCoordinateToInfoModelMap();
        ranges = new LinkedList<>();
        lastUserPositions = new ArrayList<>();
        dividePathIntoRanges();

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

        for (int i = 0; i < ranges.size(); i++) {
            Log.d(TAG, "Range #" + i + ":");
            String dir = "NONE";
            if (ranges.get(i).getRelationToNextRange() == Range.LEFT)
                dir = "links";
            else if (ranges.get(i).getRelationToNextRange() == Range.RIGHT)
                dir = "rechts";
            if (!(ranges.get(i).getRelationToNextRange() == Range.LAST))
                Log.d(TAG, "Nächste Range in Richtung: " + dir);
            else
                Log.d(TAG, "Das ist die letzte Range");
            Log.d(TAG, ranges.get(i).toString());
        }

//        lastUserPositions.add(ranges.get(0).getCoordList().get(0));
//        lastUserPositions.add(ranges.get(1).getCoordList().get(0));
//        lastUserPositions.add(ranges.get(1).getCoordList().get(1));
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
                        Log.d(TAG, "pos at " + i + " is in same range too");
                    } else {
                        allInSameRange = false;
                        Log.d(TAG, "pos at " + i + " is NOT in same range");
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
                    } else Log.d(TAG, "stil in same range");
                    lastUserPositions.clear();
                } else {
                    //size - (counterForSameRange + 1), da man für letztes Element eh schon size - 1 machen würde
                    //Und hier soll ja mit dem vorletzen Element angefangen werden
                    int counter = 0;
                    int deletionIndex = lastUserPositions.size() - (counterForSameRange + 1);
                    Iterator<Coordinate> it = lastUserPositions.iterator();
                    Log.d(TAG, "userPosi: " + lastUserPositions);
                    Log.d(TAG, "have to delete all pos up to index: " + deletionIndex);
                    while (it.hasNext()) {
                        Coordinate c = it.next();
                        if (counter <= deletionIndex) {
                            it.remove();
                            Log.d(TAG, "removing userPos: " + c);
                        }
                        counter++;
                    }
                    Log.d(TAG, "size of userPosList after removal: " + lastUserPositions.size());

                }
            }
        }
    }

    private void onNewRangeEntered() {
        adjustImageToNewRange();
        ArrayList<String> environmentalInfo = currentRange.getEnvironmentalInfos();
        instructionTexts = new ArrayList<>();
        instructionTexts.add("Geradeaus");

        String vorbeiAn = "Vorbei an ";
        for (String e : environmentalInfo)
            instructionTexts.add(vorbeiAn + e);

//        strings.add("Vorbei an Glastür");strings.add("Vorbei an Teppich");strings.add("Vorbei an Ming-Vase");
        instructionTexts.add(currentRange.getRelationToNextRangeAsString());
        tts.speakList(instructionTexts, 0);
    }

    private Range getRangeByCoord(Coordinate coord) {
        Range resultRange = null;
        for (Range range : ranges) {
            Log.d(TAG, "checking range " + range);
            Log.d(TAG, "for pos: " + coord);
            if (range.getCoordList().contains(coord)) {
                Log.d(TAG, "this was the wanted range");
                resultRange = range;
                break;
            } else Log.d(TAG, "this was not the wanted range");
        }
        if (resultRange == null) {
            Log.d(TAG, "resultRange still null");
        }
        return resultRange;
    }

    public void updateTextViews(TextView directionTextView) {
        directionTextView.setText(directionDifference + directionUnit);
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

            directionDifference = (int) (direction - firstDirection);
            if (directionDifference <= 10) {
                if (previousDirection < 360 && previousDirection > 355 && direction > 0 && direction < 5)
                    direction = 360;
                else if (previousDirection > 0 && previousDirection < 5 && direction < 360 && direction > 355)
                    direction = 0;
            }

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
        else if (currentRange.getRelationToNextRange() == Range.NONE)
            firstDirection = 0;
    }

    public void nextInstruction() {
//        tts.speak("Nächste Anweisung");

        int index = ranges.indexOf(currentRange);

        Range nextRange;
        if (index != ranges.size() - 1) {
            nextRange = ranges.get(index + 1);
            ArrayList<String> environmentalInfo = nextRange.getEnvironmentalInfos();
            instructionTexts = new ArrayList<>();
            instructionTexts.add("Geradeaus");

            String vorbeiAn = "Vorbei an ";
            for (String e : environmentalInfo)
                instructionTexts.add(vorbeiAn + e);

            instructionTexts.add(nextRange.getRelationToNextRangeAsString());
            tts.speakList(instructionTexts, 0);
        } else {
            ArrayList<String> alternative = new ArrayList<>();
            alternative.add("Danach haben Sie ihr Ziel erreicht. Es gibt keine nächste Anweisung.");
            tts.speakList(alternative, 0);
        }
//        ArrayList<String> environmentalInfo = currentRange.getEnvironmentalInfos();
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("Geradeaus");
//
//        String vorbeiAn = "Vorbei an ";
//        for (String e : environmentalInfo)
//            strings.add(vorbeiAn+e);
//
////        strings.add("Vorbei an Glastür");strings.add("Vorbei an Teppich");strings.add("Vorbei an Ming-Vase");
//        strings.add(currentRange.getRelationToNextRangeAsString());
//        tts.speakList(strings, 0);
    }

    public void previousInstruction() {
//        tts.speak("Vorherige Anweisung");
        int index = ranges.indexOf(currentRange);

        Range previousRange;
        if (index != 0) {
            previousRange = ranges.get(index - 1);
            ArrayList<String> environmentalInfo = previousRange.getEnvironmentalInfos();
            instructionTexts = new ArrayList<>();
            instructionTexts.add("Geradeaus");

            String vorbeiAn = "Vorbei an ";
            for (String e : environmentalInfo)
                instructionTexts.add(vorbeiAn + e);

            instructionTexts.add(previousRange.getRelationToNextRangeAsString());
            tts.speakList(instructionTexts, 0);
        } else {
            ArrayList<String> alternative = new ArrayList<>();
            alternative.add("Die Navigation hat hier begonnen. Es gibt noch keine vorherige Anweisung.");
            tts.speakList(alternative, 0);
        }
    }

    public void repeatInstruction() {
        tts.speakList(instructionTexts, 0);
    }
}
