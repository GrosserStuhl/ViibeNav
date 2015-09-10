package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.indoorbeacon.app.model.position.neighbor.MacToMedian;

import java.util.ArrayList;


/**
 * Created by TomTheBomb on 12.07.2015.
 */
public class Measurement {

    private Activity activity;
    public static final String TAG = "Measurement";
    public Measurement.State state;

    public Measurement(Activity activity){
        this.activity = activity;
        setState(State.notMeasuring);
    }


    public enum State{
        isMeasuring,notMeasuring;
    };

    public void setState(State state) {
        Log.d(TAG, "STATE: "+state);
        this.state = state;

        Intent intent = new Intent("measuring boolean changed");
        intent.putExtra("startedMeasuring", isMeasuring());
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public State getState() {
        return state;
    }

    public boolean isMeasuring(){
       if(getState().equals(State.isMeasuring))
           return true;
        return false;
    }

    /**
     * Only Call this function for on the Fly measurement to identify position - NOT for saving data in the RadioMap (For saving data to RadioMap use overallCalcProcess())
     * @param beacons
     * @param
     */
    public void onTheFlyCalcProcess(ArrayList<OnyxBeacon> beacons, Person person){

            MacToMedian[] data = MacToMedian.listToMacToMedianArr(beacons);
            person.estimatePos(data);
    }
}
