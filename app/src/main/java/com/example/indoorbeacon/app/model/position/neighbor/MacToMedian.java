package com.example.indoorbeacon.app.model.position.neighbor;

import com.example.indoorbeacon.app.model.OnyxBeacon;
import com.example.indoorbeacon.app.model.Orientation;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Maps a macAddress of an OnyxBeacon to a specific median
 *
 * Created by TomTheBomb on 15.08.2015.
 */
public class MacToMedian {

    private CharBuffer macAddress;
    private double median;
    private Orientation orientation;
    private String macAddressStr;

    public MacToMedian(CharBuffer macAddress, double median, Orientation orientation) {
        this.macAddress = macAddress;
        this.median = median;
        this.orientation = orientation;
        this.macAddressStr = macAddress.toString();
    }

    public static MacToMedian[] mapToMacToMedianArr(HashMap<CharBuffer,OnyxBeacon> input){
        MacToMedian[] res = new MacToMedian[input.size()];
        final ArrayList<OnyxBeacon> convert = new ArrayList<>(input.values());

        for(int i=0;i<input.values().size();i++)
            res[i] = new MacToMedian(convert.get(i).getMacAddress(),convert.get(i).getMedianRSSI(),convert.get(i).getOrientation());

        return res;
    }

    public static MacToMedian[] listToMacToMedianArr(final ArrayList<OnyxBeacon> input){
        MacToMedian[] res = new MacToMedian[input.size()];
        for(int i=0;i<input.size();i++)
            res[i] = new MacToMedian(input.get(i).getMacAddress(), input.get(i).getMedianRSSI(),input.get(i).getOrientation());

        return res;
    }

    public CharBuffer getMacAddress() {
        return macAddress;
    }

    public double getMedian() {
        return median;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public String getMacAddressStr() {
        return macAddressStr;
    }
}
