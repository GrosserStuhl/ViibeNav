package com.example.indoorbeacon.app.model.position.neighbor;

import java.util.Comparator;

/**
 * Created by TomTheBomb on 12.09.2015.
 */
public class DevToCoordComparator implements Comparator<DeviationToCoord>{

    /**
     * Negative return value: first parameter subordinate
     * 0 return value: both parameters same order
     * Positive return value: first parameter superior
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(DeviationToCoord o1, DeviationToCoord o2) {
        if(o1.getdeviation() == o2.getdeviation())
            return 0;
        if(o1.getdeviation() < o2.getdeviation())
            return -1;
        if(o2.getdeviation() > o2.getdeviation())
            return 1;

        return Double.compare(o1.getdeviation(),o2.getdeviation());
    }
}
