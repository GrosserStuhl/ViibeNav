package com.example.indoorbeacon.app.model.position.neighbor;

import android.util.Log;

import com.example.indoorbeacon.app.model.Coordinate;
import com.example.indoorbeacon.app.model.Definitions;
import com.example.indoorbeacon.app.model.Util;
import com.example.indoorbeacon.app.model.dbmodels.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * Created by TomTheBomb on 15.08.2015.
 *
 * This class is the enhanced k-nearest neighbor approach
 *
 * See
 * Overview on RSSI-based Positioning
 * Algorithms for WPSs
 * Luca De Nardis and Giuseppe Caso
 *
 * for further understanding and reference
 */
public class Ewknn implements PositionAlgorithm {


    @Override
    public Coordinate estimatePos(MacToMedian[] map){
        Coordinate estimatedPos = compareRSSIs(map);

        Log.d(TAG,"COORD: "+estimatedPos );
        return estimatedPos ;
    }

    private Coordinate compareRSSIs(MacToMedian[] map){
        // maps the differences between measured RSSIs and
        // RSSIs in the look-up table to their pointing coordinate
        // see slide 10 on ppxt mentioned above
        // ||||||||||||||||||||||||||||||||||||
        // Step 1/3 done -> I RPs filtering
        ArrayList<DeviationToCoord> data = Database.getDB().getAllDistancesFromMedians(map);

        if(data == null || data.isEmpty()) {
            Log.e(TAG, "INVALID COORDINATE FILTER - DATA == NULL || data empty");
            return new Coordinate(-1, -1, -1);
        }

        // Step 2/3 done -> II RPs filtering
        // Remove minDeviation from list and calculate next threshold with it
        // after that the filtered list with minDeviation added is returned
        if(data.size() == 1) {
            Log.e(TAG, "INVALID COORDINATE DATA SIZE = 1");
            return new Coordinate(-1, -1, -1);
        } else {
            DeviationToCoord minDeviation = getMinDevToCoord(data);
            data = filterRPsII(minDeviation, data);
        }

        if(data == null || data.isEmpty()) {
            Log.e(TAG, "INVALID COORDINATE FILTER 2");
            return new Coordinate(-1, -1, -1);
        }
        // Step 3/3 done -> Position estimate
        return estimatePosFromData(data);
    }

    /**
     * PROBLEM : SOMETIMES THIS FUNCTION RETURNS NaN
     * which could derive from a 0 denominator maybe?! Better write 1/0.000000001 than 1/0 (which is not possible and may cause this error
     *
     * This error is not bad!
     * It just tells you that this algorithm has found minimum 1 or more medians with 0 deviation to the database
     * therefore means is extremely reliable measurement
     *
     * @param data
     * @return
     */
    private Coordinate estimatePosFromData(ArrayList<DeviationToCoord> data){
        double numerator_FLOOR = 0;
        double denominator_FLOOR = 0;

        double numerator_X = 0;
        double denominator_X = 0;

        double numerator_Y = 0;
        double denominator_Y = 0;

        for(DeviationToCoord tmp : data){
            // to prevent NaN and Infinity error by dividing through 0
            // we choose a very small deviation like: 0,0000001
            double deviation = tmp.getdeviation()!=0 ? tmp.getdeviation() : 0.0000001;

            numerator_FLOOR += (1/deviation*tmp.getCoordinate().getFloor());
            numerator_X += (1/deviation*tmp.getCoordinate().getX());
            numerator_Y += (1/deviation*tmp.getCoordinate().getY());

            denominator_FLOOR += (1/deviation);
            denominator_X += (1/deviation);
            denominator_Y += (1/deviation);
        }

//        Log.d(TAG, "CHECK BUG: -NaN and -Infinity: "+numerator_Y+" + "+denominator_Y);

        final double estimate_FLOOR = numerator_FLOOR/denominator_FLOOR;
        final double estimate_X = numerator_X/denominator_X;
        final double estimate_Y = numerator_Y/denominator_Y;

        return new Coordinate (Util.twoDecimals(estimate_FLOOR), Util.twoDecimals(estimate_X),Util.twoDecimals(estimate_Y));
    }

    private DeviationToCoord getMinDevToCoord(ArrayList<DeviationToCoord> data){
        Collections.sort(data, new DevToCoordComparator());
//        ArrayList<Float> deviations = new ArrayList<>();
//        for (DeviationToCoord tmp : data) {
//            deviations.add(tmp.getdeviation());
//        }
//        Log.d(TAG, "DEVIATION LIST: "+deviations);

        // remove from list!
        data.remove(0);
        return data.get(0);
    }

    private ArrayList<DeviationToCoord> filterRPsII(DeviationToCoord min, ArrayList<DeviationToCoord> data){

        float E_s = 0;

        for(DeviationToCoord tmp : data)
            E_s += Math.abs(min.getdeviation() - tmp.getdeviation());

        // now E_s is actually only the nominator so now
        // we have to divide with denominator
        // see equation 2 on slide 10
        E_s  /=  (data.size()-1);

        Iterator it = data.iterator();
        while(it.hasNext()){
            DeviationToCoord tmp = (DeviationToCoord) it.next();
            if(tmp.getdeviation()>E_s)
                it.remove();
        }

        // Additional Filter for max K of 5
        if(Definitions.POSITIONING_K_FILTER_ENABLED)
        for (int i = data.size() - 1; i >= 0; i--)
             if(data.size()> Definitions.POSITIONING_MAX_K-1)
                 data.remove(i);


        // IMPORTANT put min back in the list it's the best GUESS!!!
        data.add(min);
        return data;
    }

}
