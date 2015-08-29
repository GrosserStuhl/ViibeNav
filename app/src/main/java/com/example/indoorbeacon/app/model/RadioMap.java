package com.example.indoorbeacon.app.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TomTheBomb on 25.06.2015.
 */
public class RadioMap {

    private static RadioMap singleton;

    private Coordinate coordinate;

    private static HashMap<BeaconsMedians,AnchorPoint> data;
    private AnchorPoint lastAnchor;

    private RadioMap() {
        this.data = new HashMap<BeaconsMedians,AnchorPoint>();
        this.coordinate = new Coordinate(0,0,0);
    }

    public static RadioMap createRadioMap(){
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized(RadioMap.class) {
            if (singleton == null) {
                singleton = new RadioMap();
            }
        }
        return singleton;
    }

    public static RadioMap getRadioMap(){
        return singleton;
    }

    public static void add(AnchorPoint a){
        data.put(a.getBeaconsMedians(),a);
    }

    public static void remove(Coordinate coordinate){
        Iterator it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<BeaconsMedians,AnchorPoint> pair = (Map.Entry) it.next();
            if(pair.getValue().getCoordinate().equals(coordinate))
                it.remove();
        }
    }

    public static int size(){
        return data.size();
    }

    public void setLastAnchor(AnchorPoint a){
        this.lastAnchor = a;
    }

    public AnchorPoint getLastAnchor() {
        return lastAnchor;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public double getPosition_x() {
        return coordinate.getX();
    }

    public double getPosition_y() {
        return coordinate.getY();
    }

    public void setPosition_x(int position_x) {
        coordinate.setX(position_x);
    }

    public void setPosition_y(int position_y) {
        coordinate.setX(position_y);
    }

    public void setY_up(){
        coordinate.setY_up();
    }

    public void setY_down(){
        coordinate.setY_down();
    }

    public void setX_up(){
        coordinate.setX_up();
    }

    public void setX_down(){
       coordinate.setX_down();
    }

    public double getFloor() {
        return coordinate.getFloor();
    }

    public static HashMap<BeaconsMedians, AnchorPoint> getData() {
        return data;
    }

    public void setFloor(int floor) {
        coordinate.setFloor(floor);
    }
}
