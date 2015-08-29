package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.util.Log;

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
    private Activity activity;

    private PositionAlgorithm algorithm;

    public Person(Activity activity) {
        this.activity = activity;
        coord = new Coordinate(-1,-1,-1);
        measurement = new Measurement();

        algorithm = new Ewknn();
    }

    public void getMostLikelyPosition(){

        ArrayList<OnyxBeacon> surrounding = OnyxBeacon.filterSurroundingBeacons();
        getOnTheFlyMedians(surrounding);

    }

    public void getOnTheFlyMedians(ArrayList<OnyxBeacon> surrounding){
        Log.d(TAG, "SURROUNDING " + surrounding.size());
        ArrayList<Integer> supposedAnchorPointIds = new ArrayList<>();

        measurement.setState(Measurement.State.isMeasuring);
        for(OnyxBeacon tmp : surrounding)
            if (!tmp.isMeasurementStarted())
                tmp.setMeasurementStarted(true);
        measurement.overallOnTheFlyCalcProcess(surrounding, this);
    }

    public void estimatePos(MacToMedian[] data){
        setCoord(getAlgorithm().estimatePos(data));

        /**TODO**
         *
         * AFTER getAlgorithm().estimatePos(data) is done
         * Persons coordinates have changed to an estimated value!
         * Therefore HERE NEEDS to be the Code to set estimated Location on UI
         *
         */
    }


    private Coordinate getCoordFromAnchorId(int id){
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
