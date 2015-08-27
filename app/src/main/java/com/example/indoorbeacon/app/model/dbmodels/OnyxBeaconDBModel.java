package com.example.indoorbeacon.app.model.dbmodels;

import java.util.ArrayList;

/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class OnyxBeaconDBModel {

    private int _id,major,minor;
    private String macAddress;

    private static ArrayList<OnyxBeaconDBModel> allBeacons;

    public OnyxBeaconDBModel(int _id, int major, int minor, String macAddress) {
        this._id = _id;
        this.major = major;
        this.minor = minor;
        this.macAddress = macAddress;
    }

    public static ArrayList<OnyxBeaconDBModel> getAllBeacons() {
        return allBeacons;
    }

    public static void setAllBeacons(ArrayList<OnyxBeaconDBModel> allBeacons) {
        OnyxBeaconDBModel.allBeacons = allBeacons;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
