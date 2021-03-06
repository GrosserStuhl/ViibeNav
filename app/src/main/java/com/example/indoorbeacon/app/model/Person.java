package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.indoorbeacon.app.controller.NavigationActivity;
import com.example.indoorbeacon.app.model.dbmodels.Database;
import com.example.indoorbeacon.app.model.position.neighbor.Ewknn;
import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;
import com.example.indoorbeacon.app.model.position.neighbor.PositionAlgorithm;

import java.util.ArrayList;


/**
 * Created by TomTheBomb on 23.07.2015.
 */
public class Person {

    private static final String TAG = "Person";

    private Coordinate currentPos;
    private Coordinate currentPosAlgorithm;

    private Measurement measurement;
    private NavigationActivity activity;

    private PositionAlgorithm algorithm;

    private SensorHelper sensorHelper;
    private int walkedDistance;
    private Handler walkDestectionHandler;

    private ArrayList<Coordinate> tmpCoordinates;

    public Person(NavigationActivity activity) {
        this.activity = activity;
        tmpCoordinates = new ArrayList<>();
        currentPos = new Coordinate(-1, 0, 0);
        currentPosAlgorithm = new Coordinate(-1, -1, -1);
        measurement = new Measurement(activity);

        algorithm = new Ewknn();

        sensorHelper = SensorHelper.getSensorHelper(activity);

        walkDestectionHandler = new Handler();
        walkDestectionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sensorHelper.isWalking()) {
                    walkedDistance += Definitions.WALKED_WALKED_CENTIMETERS_PER_SECOND / 2;
                    Log.d(TAG, "walkedDistance " + walkedDistance);
                }
                new Handler().postDelayed(this, 500);
            }
        }, 0);
    }

    public void getMostLikelyPosition() {
        measurement.setState(Measurement.State.isMeasuring);
        ArrayList<OnyxBeacon> surrounding = OnyxBeacon.filterSurroundingBeacons();
        getOnTheFlyMedians(surrounding);
    }

    private void getOnTheFlyMedians(ArrayList<OnyxBeacon> surrounding) {
        Log.d(TAG, "SURROUNDING " + surrounding.size());
//        for (OnyxBeacon tmp : surrounding)
//            if (!tmp.isMeasurementStarted())
//                tmp.setMeasurementStarted(true);
        measurement.onTheFlyCalcProcess(surrounding, this);
    }

    public void estimatePos(MacToMedian[] data) {

        // need to round the values for adjescent points
        Coordinate estimatedPos = getAlgorithm().estimatePos(data);
        estimatedPos.setX((int) Math.round(estimatedPos.getX()));
        estimatedPos.setY((int) Math.round(estimatedPos.getY()));

        setCurrentPosAlgorithm(estimatedPos);

        ArrayList<Coordinate> neighbours;

        if (walkedDistance < Definitions.ANCHORPOINT_DISTANCE_IN_CM || !estimatedPos.isValid()) {
//            if (!estimatedPos.isValid())
//                Toast.makeText(activity.getApplicationContext(), "INVALID COORDINATE!", Toast.LENGTH_SHORT).show();
        } else if (walkedDistance >= Definitions.ANCHORPOINT_DISTANCE_IN_CM
                && walkedDistance < Definitions.ANCHORPOINT_DISTANCE_IN_CM * 2) {
            //Die Matrix mit den nächsten Nachbarn zu estimatedPos
            // x x x
            // x o x
            // x x x  (3x3 Matrix)

            neighbours = Database.getDB().getDirectNeighborFPs(currentPos);
            Coordinate newEstimatedPos = findNextBestPos(neighbours, estimatedPos);
            if (newEstimatedPos.isValid()) {
                Log.d(TAG, "newCurPos: " + newEstimatedPos);
                setCurrentPos(newEstimatedPos);
            }
            walkedDistance = 0;

            Log.d(TAG, "SIZE of neighbors nächster: " + neighbours.size());
        } else if (walkedDistance >= Definitions.ANCHORPOINT_DISTANCE_IN_CM * 2) {
            //Die Matrix mit den ÜBERnächsten Nachbarn zu estimatedPos bekommen
            //Auf jeder Seite werden 4 ersten Punkte genommen, der letzte wird ausgelassen
            //Dieser ist dann wiederum der Anfangspunkt für die nächste Seite, somit 4x4 Matrix
            //
            // * * * * |*| (Beispiel linke Seite)
            // * x x x |*|
            // * x o x |*|
            // * x x x |*|
            // * * * * *

            neighbours = Database.getDB().getOuterNeighborFPs(currentPos);
            Coordinate newEstimatedPos = findNextBestPos(neighbours, estimatedPos);
            if (newEstimatedPos.isValid()) {
                Log.d(TAG, "newCurPos: " + newEstimatedPos);
                setCurrentPos(newEstimatedPos);
            }
            walkedDistance = 0;

            Log.d(TAG, "SIZE of neighbors übernächster: " + neighbours.size());
        }
        measurement.setState(Measurement.State.notMeasuring);
    }

    private Coordinate findNextBestPos(ArrayList<Coordinate> neighbours, Coordinate estimatedPos) {
        Coordinate newEstimatedPos = new Coordinate(-1, -1, -1);
        double smallestDistance = 15;

        for (Coordinate tempPos : neighbours) {
            if (tempPos.equals(estimatedPos)) {
                break;
            } else {
                double t_X = tempPos.getX();
                double t_Y = tempPos.getY();
                double e_X = estimatedPos.getX();
                double e_Y = estimatedPos.getY();

                double euclideanDistance = Math.pow(e_X - t_X, 2) + Math.pow(e_Y - t_Y, 2);
                if (euclideanDistance < smallestDistance && tempPos.isValid()) {
                    smallestDistance = euclideanDistance;
                    newEstimatedPos = tempPos;
                }
            }
        }
        if (newEstimatedPos.equals(new Coordinate(-1, -1, -1)))
            Log.e(TAG, "no nearest neighbour found!");
        return newEstimatedPos;
    }

    private Coordinate getCoordFromAnchorId(int id) {
        return Database.getDB().getCoordFromAnchorId(id);
    }

    public void setCurrentPos(Coordinate currentPos) {
        this.currentPos = currentPos;

        // Notify all listeners that persons position has changed
        Intent intent = new Intent("person position changed");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public Coordinate getCurrentPos() {
        return currentPos;
    }

    public Activity getActivity() {
        return activity;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public PositionAlgorithm getAlgorithm() {
        return algorithm;
    }

    public Coordinate getCurrentPosAlgorithm() {
        return currentPosAlgorithm;
    }

    public void setCurrentPosAlgorithm(Coordinate currentPosAlgorithm) {
        this.currentPosAlgorithm = currentPosAlgorithm;
    }

    public void killAllHandlers() {
        walkDestectionHandler.removeCallbacksAndMessages(null);
    }
}
