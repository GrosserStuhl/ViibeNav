package com.example.indoorbeacon.app.model.beaconFilter;

import java.util.HashSet;

/**
 * Created by TomTheBomb on 29.07.2015.
 */
public class MinorFilter {

    private static HashSet<Integer> minorFilter;

    static{
        minorFilter = new HashSet<>();
        minorFilter.add(17176);
        minorFilter.add(25497);
        minorFilter.add(25522);
        minorFilter.add(44694);
        minorFilter.add(65080);
        minorFilter.add(16339);
        minorFilter.add(3);
        minorFilter.add(2);
        minorFilter.add(1);
    }

    public static boolean inFilter(int minor){
        if(minorFilter.contains(minor))
            return true;
        else
            return false;
    }


}
