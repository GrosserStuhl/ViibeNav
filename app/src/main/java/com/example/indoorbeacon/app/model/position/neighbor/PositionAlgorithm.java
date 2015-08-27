package com.example.indoorbeacon.app.model.position.neighbor;

import com.example.indoorbeacon.app.model.Coordinate;

/**
 * Created by TomTheBomb on 16.08.2015.
 */
public interface PositionAlgorithm {

    public static final String TAG = "PositionAlgorithm";

    public Coordinate estimatePos(MacToMedian[] map);
}
