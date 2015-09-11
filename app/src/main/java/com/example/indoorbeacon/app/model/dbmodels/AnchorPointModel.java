package com.example.indoorbeacon.app.model.dbmodels;

import com.example.indoorbeacon.app.model.Coordinate;


/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class AnchorPointModel {

    private int _id;
    private Coordinate coord;
    private int info_id;

    public AnchorPointModel(int _id, Coordinate coord, int info_id) {
        this._id = _id;
        this.coord = coord;
        this.info_id = info_id;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public int get_id() {
        return _id;
    }



}
