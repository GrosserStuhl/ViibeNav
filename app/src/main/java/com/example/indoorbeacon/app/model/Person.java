package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.example.indoorbeacon.app.NavigationActivity;
import com.example.indoorbeacon.app.SensorHelper;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.position.neighbor.Ewknn;
import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;
import com.example.indoorbeacon.app.model.position.neighbor.PositionAlgorithm;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by TomTheBomb on 23.07.2015.
 */
public class Person {

    private static final String TAG = "Person";

    private Coordinate coord;
    private Measurement measurement;
    private NavigationActivity activity;

    private PositionAlgorithm algorithm;

    private SensorHelper sensorHelper;
    private int walkedDistance;
    private boolean trackingActivated = false;

    public Person(NavigationActivity activity) {
        this.activity = activity;
        coord = new Coordinate(-1, -1, -1);
        measurement = new Measurement(activity);

        algorithm = new Ewknn();

        sensorHelper = SensorHelper.getSensorHelper(activity);

//        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                boolean startedMeasuring = intent.getBooleanExtra("startedMeasuring", false);
//
//                if (startedMeasuring) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (measurement.isMeasuring()) {
//                                if (sensorHelper.isWalking())
//                                    walkedDistance += Definitions.WALKED_METERS_PER_SECOND / 2;
//                                new Handler().postDelayed(this, 500);
//                            }
//                        }
//                    }, 0);
//                }
//            }
//        };
//
//        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
//                new IntentFilter("measuring boolean changed"));
    }

    public void getMostLikelyPosition() {
        ArrayList<OnyxBeacon> surrounding = OnyxBeacon.filterSurroundingBeacons();
        getOnTheFlyMedians(surrounding);
    }

    private void getOnTheFlyMedians(ArrayList<OnyxBeacon> surrounding) {
        Log.d(TAG, "SURROUNDING " + surrounding.size());
        ArrayList<Integer> supposedAnchorPointIds = new ArrayList<>();

        measurement.setState(Measurement.State.isMeasuring);
        for (OnyxBeacon tmp : surrounding)
            if (!tmp.isMeasurementStarted())
                tmp.setMeasurementStarted(true);
        measurement.overallOnTheFlyCalcProcess(surrounding, this);
    }

    public void estimatePos(MacToMedian[] data) {
        trackingActivated = false;

        Coordinate estimatedPos = getAlgorithm().estimatePos(data);

        if (walkedDistance < Definitions.ANCHORPOINT_DISTANCE_IN_M) {
            setCoord(estimatedPos);
        } else if (walkedDistance >= Definitions.ANCHORPOINT_DISTANCE_IN_M
                && walkedDistance < Definitions.ANCHORPOINT_DISTANCE_IN_M * 2) {
            //TODO
            //Die Matrix mit den nächsten Nachbarn zu estimatedPos
            // x x x
            // x o x
            // x x x  (3x3 Matrix)

            ArrayList<Coordinate> neighbours = new ArrayList<>();
            Coordinate newEstimatedPos = findNextBestPos(neighbours, estimatedPos);
            setCoord(newEstimatedPos);
        } else if (walkedDistance >= Definitions.ANCHORPOINT_DISTANCE_IN_M * 2) {
            //TODO
            //Die Matrix mit den ÜBERnächsten Nachbarn zu estimatedPos bekommen
            //Auf jeder Seite werden 4 ersten Punkte genommen, der letzte wird ausgelassen
            //Dieser ist dann wiederum der Anfangspunkt für die nächste Seite, somit 4x4 Matrix
            //
            // * * * * |*| (Beispiel linke Seite)
            // * x x x |*|
            // * x o x |*|
            // * x x x |*|
            // * * * * *

            ArrayList<Coordinate> neighbours = new ArrayList<>();
            Coordinate newEstimatedPos = findNextBestPos(neighbours, estimatedPos);
            setCoord(newEstimatedPos);
        }

        walkedDistance = 0;
        trackingActivated = true;
        startTrackingDistance();
    }

    private Coordinate findNextBestPos(ArrayList<Coordinate> neighbours, Coordinate estimatedPos) {
        Coordinate newEstimatedPos = estimatedPos;
        double smallestDistance = 0;

        for (Coordinate tempPos : neighbours) {
            if (tempPos.equals(estimatedPos))
                break;
            else {
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
        return newEstimatedPos;
    }

    private void startTrackingDistance() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (trackingActivated) {
                    if (sensorHelper.isWalking())
                        walkedDistance += Definitions.WALKED_METERS_PER_SECOND / 2;
                    new Handler().postDelayed(this, 500);
                }
            }
        }, 0);
    }


    private Coordinate getCoordFromAnchorId(int id) {
        return DBHandler.getDB().getCoordFromAnchorId(id);
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoord() {
        return coord;
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
}
