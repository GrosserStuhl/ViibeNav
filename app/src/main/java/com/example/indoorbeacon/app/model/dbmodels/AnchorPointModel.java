package com.example.indoorbeacon.app.model.dbmodels;

import com.example.indoorbeacon.app.model.Coordinate;


/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class AnchorPointModel {

    private int _id;
    private Coordinate coord;
    private InfoModel info;

    public AnchorPointModel(int _id, Coordinate coord, InfoModel info) {
        this._id = _id;
        this.coord = coord;
        this.info = info;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public int get_id() {
        return _id;
    }

    public InfoModel getInfo() {
        return info;
    }
}
