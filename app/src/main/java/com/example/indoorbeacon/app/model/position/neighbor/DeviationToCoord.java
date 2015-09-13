package com.example.indoorbeacon.app.model.position.neighbor;

import com.example.indoorbeacon.app.model.Coordinate;

/**
 * Created by TomTheBomb on 15.08.2015.
 */
public class DeviationToCoord {

    private float deviation;
    private Coordinate coordinate;

    public DeviationToCoord(float deviation, Coordinate coordinate) {
        this.deviation = deviation;
        this.coordinate = coordinate;
    }

    public float getdeviation() {
        return deviation;
    }

    public void setdeviation(float deviation) {
        this.deviation = deviation;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
}
