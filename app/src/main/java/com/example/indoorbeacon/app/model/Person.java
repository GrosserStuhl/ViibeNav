package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.indoorbeacon.app.NavigationActivity;
import com.example.indoorbeacon.app.SensorHelper;
import com.example.indoorbeacon.app.model.dbmodels.DBHandler;
import com.example.indoorbeacon.app.model.position.neighbor.Ewknn;
import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;
import com.example.indoorbeacon.app.model.position.neighbor.PositionAlgorithm;

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

    public Person(NavigationActivity activity) {
        this.activity = activity;
        coord = new Coordinate(-1, -1, -1);
        measurement = new Measurement(activity);

        algorithm = new Ewknn();

        sensorHelper = SensorHelper.getSensorHelper(activity);

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean startedMeasuring = intent.getBooleanExtra("startedMeasuring", false);

                if (startedMeasuring) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (measurement.isMeasuring()) {
                                if (sensorHelper.isWalking())
                                    walkedDistance += Definitions.WALKED_METERS_PER_SECOND / 2;
                                new Handler().postDelayed(this, 500);
                            }
                        }
                    }, 0);
                }
            }
        };

        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("measuring boolean changed"));
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
        setCoord(getAlgorithm().estimatePos(data));

        /**TODO
         *
         * AFTER getAlgorithm().estimatePos(data) is done
         * Persons coordinates have changed to an estimated value!
         * Therefore HERE NEEDS to be the Code to set estimated Location on UI
         *
         */
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
