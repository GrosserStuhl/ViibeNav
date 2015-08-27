package com.example.indoorbeacon.app.model.dbmodels;

import com.example.indoorbeacon.app.model.Coordinate;

import java.util.ArrayList;

/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class AnchorPointDBModel {

    private int _id;
    private Coordinate coord;
    private ArrayList<Integer> beaconIds;


    private static ArrayList<AnchorPointDBModel> allAnchors;

    public AnchorPointDBModel(int _id, Coordinate coord, final ArrayList<Integer> beaconIds) {
        this._id = _id;
        this.coord = coord;
        this.beaconIds = beaconIds;
    }


    public static void setAllAnchors(ArrayList<AnchorPointDBModel> allAnchors) {
        AnchorPointDBModel.allAnchors = allAnchors;
    }

    public ArrayList<Integer> getBeaconIds() {
        return beaconIds;
    }

    public static ArrayList<AnchorPointDBModel> getAllAnchors() {
        return allAnchors;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
