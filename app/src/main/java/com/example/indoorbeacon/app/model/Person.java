package com.example.indoorbeacon.app.model;

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
    private TestAreaActivity test;

    private PositionAlgorithm algorithm;

    public Person(TestAreaActivity test) {
        this.test = test;
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

        // START MEASURING
        measurement.setState(Measurement.State.isMeasuring);
        for(OnyxBeacon tmp : surrounding) {
            if (!tmp.onTheFlyMeasurement())
                tmp.setOnTheFlyMeasurement(true);
        }
        measurement.overallOnTheFlyCalcProcess(surrounding, this);

    }

    public void estimatePos(MacToMedian[] data){
        setCoord(getAlgorithm().estimatePos(data));
        getTest().updateLikelyCoordsView();
    }

    public void checkLoop(){
        if(isLoop())
            getMostLikelyPosition();
    }

    private boolean isLoop(){
        return test.isLoopTest();
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

    public TestAreaActivity getTest() {
        return test;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public PositionAlgorithm getAlgorithm() {
        return algorithm;
    }
}
