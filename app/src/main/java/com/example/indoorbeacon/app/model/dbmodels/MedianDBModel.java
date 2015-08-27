package com.example.indoorbeacon.app.model.dbmodels;

import java.util.ArrayList;

/**
 * Created by TomTheBomb on 25.07.2015.
 */
public class MedianDBModel {

    private int _id;
    private double median;
    private String macAddress;

    private static ArrayList<MedianDBModel> allMedians;

    public MedianDBModel(int _id, double median, String macAddress) {
        this._id = _id;
        this.median = median;
        this.macAddress = macAddress;
    }

    public static void setAllMedians(ArrayList<MedianDBModel> allMedians) {
        MedianDBModel.allMedians = allMedians;
    }

    public static ArrayList<MedianDBModel> getAllMedians() {
        return allMedians;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
