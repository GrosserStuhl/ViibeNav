package com.example.indoorbeacon.app.model;

import android.app.Activity;

import com.example.indoorbeacon.app.SensorHelper;

/**
 * Created by TomTheBomb on 01.09.2015.
 */
public class UserOrientation {

    private Activity act;
    private SensorHelper sh;

    public UserOrientation(Activity act){
        this.act = act;
        this.sh = SensorHelper.getSensorHelper(act.getApplicationContext());
    }

    public static Orientation getOrientationFromDegree(float degree){
        if(degree >= 90 && degree < 270)
            return Orientation.back;
        else if ( (degree >= 0 && degree < 90) || (degree >= 270 && degree <= 360))
            return Orientation.front;
        else
            return Orientation.undetermined;
    }

//    public static Orientation getOrientationFromSensorHelper(float orientation){
//        if(orientation >= 90 && orientation < 270)
//            return Orientation.back;
//        else if ( (orientation >= 0 && orientation < 90) || (orientation >= 270 && orientation <= 360))
//            return Orientation.front;
//        else
//            return Orientation.undetermined;
//    }

}
