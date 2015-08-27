package com.example.indoorbeacon.app.model;

import android.util.Log;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class OnyxBeacon {

    private static final String TAG = "OnyxBeacon";

    private String name;
    private int signal;

    private CharBuffer macAddress;

    private String uuid;
    private int minor,major,rssi,txPower;
    private double distanceFSPL;
    private double distanceRegression;

    private ArrayList<Integer> tenRSSIsList;
    private double medianRSSI;

    public static HashMap<CharBuffer, OnyxBeacon> beaconMap;

    private boolean measurementStarted,calculationDone;

    private long lastSignalMeasured;

    // ON THE FLY MEASUREMENT
    private ArrayList<Integer> onTheFlyRSSIs;
    private boolean onTheFlyMeasurement,onTheFlyDone;
    private double onTheFlyMedian;

    static{
        beaconMap = new HashMap<>();
    }

    public OnyxBeacon(CharBuffer deviceAddress, String uuid, int major, int minor, int rssi, int txPower) {
        this.macAddress = deviceAddress;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.txPower = txPower;

        init();
        addBeaconToHashMap(this);
    }

    private void init(){
        tenRSSIsList = new ArrayList<Integer>(10);

        measurementStarted = false;
        calculationDone = false;

        // ON THE FLY MEASUREMENT
        onTheFlyMeasurement = false;
        onTheFlyMedian = 0;
        onTheFlyRSSIs = new ArrayList<>(Setup.ON_THE_FLY_THRESHOLD);
        onTheFlyDone = false;
    }

    private void addBeaconToHashMap(OnyxBeacon beacon){
        if(beacon == null)
            throw new NullPointerException("Can Not Add a NULL beacon to HashMap!");
        // puts the Beacon object in the HashMap
        beaconMap.put(macAddress, beacon);
    }

    /**
     * Checks if the give OnyxBeacon object is already mentioned in the beaconMap
     * Returns true if it is already listed and false otherwise.
     */
    public static boolean inBeaconMap(CharBuffer deviceAddress){
        return beaconMap.containsKey(deviceAddress);
    }

    public static OnyxBeacon getBeaconInMap(CharBuffer key){
        if(key == null)
            throw new NullPointerException("Passed Key is invalid or not set.");
        if(!beaconMap.containsKey(key))
            throw new IllegalArgumentException("Passed key can not be found in beaconMap.");
        else
            return beaconMap.get(key);
    }

    public static void updateBeaconRSSIinMap(CharBuffer key,int rssi, long timeSignalMeasured){
        if(key == null)
            throw new NullPointerException("Passed Key is invalid or not set.");
        if(!beaconMap.containsKey(key))
            throw new IllegalArgumentException("Passed key can not be found in beaconMap.");
        else{
            OnyxBeacon temp = getBeaconInMap(key);
            temp.setRssi(rssi);
            temp.setLastSignalMeasured(timeSignalMeasured);
            beaconMap.put(key, temp);
        }
    }

    public void checkState(){
        if(measurementStarted) {
            if (fillTenRSSIs()) {
                calculateMedian();
                Log.d(TAG, "Calculated Median is: " + medianRSSI + " | mac: " + macAddress);
                calculationDone = true;
            }
        } else if(onTheFlyMeasurement)
            if(fillOnTheFlyRSSIs(rssi)) {
                doOnTheFlyMedianMeasurement();
                Log.d(TAG, "OnTheFly Median is: " + onTheFlyMedian + " | mac: "+macAddress);
                onTheFlyDone = true;
            }


    }

    public boolean onTheFlyDone(){
        return onTheFlyDone;
    }

    private boolean fillTenRSSIs(){
        if(tenRSSIsList.size()<10) {
            tenRSSIsList.add(rssi);
            return false;
        } else {
            if (measurementStarted)
                Log.d(TAG, Util.stringListToString(tenRSSIsList) + " | mac: "+macAddress);
            measurementStarted = false;
            return true;
        }
    }

    private boolean fillOnTheFlyRSSIs(int rssi){
        if(onTheFlyRSSIs.size()<Setup.ON_THE_FLY_THRESHOLD) {
            onTheFlyRSSIs.add(rssi);
            return false;
        } else {
            if (onTheFlyMeasurement) {
                onTheFlyMeasurement = false;
            }
            return true;
        }
    }

    private void calculateMedian(){
            medianRSSI = Statistics.calcMedian(tenRSSIsList);
    }

    private void doOnTheFlyMedianMeasurement(){
            onTheFlyMedian = Statistics.calcMedian(onTheFlyRSSIs);
    }


    public void resetMedianMeasurement(){
        tenRSSIsList.clear();
        calculationDone = false;
        medianRSSI = 0;
    }

    public void resetOnTheFlyMeasurement(){
        onTheFlyMedian = 0;
        onTheFlyRSSIs.clear();
        onTheFlyMeasurement = false;
        onTheFlyDone = false;
    }



    public static ArrayList<OnyxBeacon> getBeaconMapAsList(){
        return new ArrayList<OnyxBeacon> (beaconMap.values());
    }

    public static ArrayList<OnyxBeacon> filterSurroundingBeacons(){
        ArrayList<OnyxBeacon> res = (ArrayList<OnyxBeacon>)getBeaconMapAsList();
        Iterator it = res.iterator();
        while(it.hasNext()){
            OnyxBeacon tmp = (OnyxBeacon) it.next();
            if(!Util.hasSufficientSendingFreq(tmp.lastSignalMeasured)) {
                tmp.resetOnTheFlyMeasurement();
                it.remove();
            } else if(tmp.rssi <= Setup.SIGNAL_TOO_BAD_THRESHOLD) {
                tmp.resetOnTheFlyMeasurement();
                it.remove();
            }
        }
        return res;
    }

    public static void clearMap(){
        beaconMap.clear();
    }

    @Override
    public boolean equals(Object o) {
        if(o != null)
            if(o instanceof OnyxBeacon){
                OnyxBeacon temp = (OnyxBeacon) o;
                if(temp.getMacAddress().equals(getMacAddress()))
                    return true;
            }
        return false;
    }

    @Override
    public int hashCode() {
        return 31* (31+ getMacAddress().hashCode());
    }
    public static HashMap<CharBuffer, OnyxBeacon> getbeaconMap() {
        return beaconMap;
    }
    public String getName() {
        return name;
    }
    public CharBuffer getMacAddress() {
        return macAddress;
    }
    public String getMacAddressStr(){return macAddress.toString();}
    public int getTxPower() {
        return txPower;
    }
    public int getRssi() {
        return rssi;
    }
    public long getLastSignalMeasured() {
        return lastSignalMeasured;
    }
    public void setLastSignalMeasured(long lastSignalMeasured) {
        this.lastSignalMeasured = lastSignalMeasured;
    }
    public int getMajor() {
        return major;
    }
    public int getMinor() {
        return minor;
    }
    public double getMedianRSSI() {
        return medianRSSI;
    }
    public void setMedianRSSI(double medianRSSI) {
        this.medianRSSI = medianRSSI;
    }
    public void setMeasurementStarted(boolean measurementStarted) {
        this.measurementStarted = measurementStarted;
    }
    public boolean isCalculationDone() {
        return calculationDone;
    }
    public boolean isMeasurementStarted() {
        return measurementStarted;
    }
    public boolean onTheFlyMeasurement() { return onTheFlyMeasurement; }
    public void setOnTheFlyMeasurement(boolean onTheFlyMeasurement) {
        this.onTheFlyMeasurement = onTheFlyMeasurement;
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public double getOnTheFlyMedian() {
        return onTheFlyMedian;
    }
}
