package com.example.indoorbeacon.app.model;

import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class AnchorPoint implements Serializable {

    private int _id;
    private HashMap<CharBuffer,OnyxBeacon> anchorBeacons;
    private BeaconsMedians beaconsMedians;
    private Coordinate coordinate;

    public AnchorPoint(Coordinate coordinate, HashMap<CharBuffer,OnyxBeacon> anchorBeacons) {
        this.anchorBeacons = anchorBeacons;
        this.coordinate = coordinate;
        this.beaconsMedians = new BeaconsMedians(anchorBeacons);
    }

    public AnchorPoint(Coordinate coordinate, ArrayList<OnyxBeacon> anchorBeaconsList) {
        this.anchorBeacons = new HashMap<>();
        Iterator it = anchorBeaconsList.iterator();
        while(it.hasNext()){
            OnyxBeacon temp = (OnyxBeacon) it.next();
            anchorBeacons.put(temp.getMacAddress(),temp);
        }
        this.coordinate = coordinate;
        this.beaconsMedians = new BeaconsMedians(anchorBeacons);
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setAnchorBeacons(HashMap<CharBuffer, OnyxBeacon> anchorBeacons) {
        this.anchorBeacons = anchorBeacons;
    }

    public HashMap<CharBuffer, OnyxBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

    public BeaconsMedians getBeaconsMedians() {
        return beaconsMedians;
    }

    public void setBeaconsMedians(BeaconsMedians beaconsMedians) {
        this.beaconsMedians = beaconsMedians;
    }
}
