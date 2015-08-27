package com.example.indoorbeacon.app.model.position.neighbor;

import java.nio.CharBuffer;

/**
 *
 * Maps a macAddress of an OnyxBeacon to a specific median
 *
 * Created by TomTheBomb on 15.08.2015.
 */
public class MacToMedian {

    private CharBuffer macAddress;
    private double median;

    public MacToMedian(CharBuffer macAddress, double median) {
        this.macAddress = macAddress;
        this.median = median;
    }

    public CharBuffer getMacAddress() {
        return macAddress;
    }

    public String getMacAddressStr() {return macAddress.toString(); }

    public void setMacAddress(CharBuffer macAddress) {
        this.macAddress = macAddress;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }
}
