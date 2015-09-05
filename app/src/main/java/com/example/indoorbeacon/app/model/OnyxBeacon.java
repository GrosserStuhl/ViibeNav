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
    private float distanceFSPL;
    private float distanceRegression;


    private float medianRSSI;

    public static HashMap<CharBuffer, OnyxBeacon> beaconMap;



    private long lastSignalMeasured;

    // ON THE FLY MEASUREMENT
    private ArrayList<Integer> measurementRSSIs;
    private boolean measurementStarted,measurementDone;

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
        measurementStarted = false;
        measurementDone = false;
        measurementRSSIs = new ArrayList<>();
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
        if(measurementStarted)
            if (onMeasurementRSSIsFilled()) {
                calculateMedian();
                Log.d(TAG, Util.intListToString(measurementRSSIs) + " " + macAddress);
                Log.d(TAG, "Calculated Median is: " + medianRSSI + " | mac: " + macAddress);
                measurementDone = true;
            }

    }

    public boolean isMeasurementDone(){
        return measurementDone;
    }

    /**
     * Returns true if the amount of measured RSSIs equals the predefined size of a set for later on median calculation.
     * @return
     */
    private boolean onMeasurementRSSIsFilled(){
        if(measurementRSSIs.size()<Definitions.MEASUREMENT_AMOUNT) {
            measurementRSSIs.add(rssi);
            return false;
        } else
            measurementStarted = false;
        return true;
    }

    private void calculateMedian(){
            medianRSSI = (float) Statistics.calcMedian(measurementRSSIs);
    }

    public void resetMedianMeasurement(){
        medianRSSI = 0;
        measurementRSSIs.clear();
        measurementStarted = false;
        measurementDone = false;
    }

    public static OnyxBeacon[] getBeaconMapAsArr(HashMap<CharBuffer,OnyxBeacon> tmpBeaconMap){
        ArrayList<OnyxBeacon> tmp = new ArrayList<OnyxBeacon>(tmpBeaconMap.values());
        return tmp.toArray(new OnyxBeacon[tmp.size()]);
    }

    public static ArrayList<OnyxBeacon> getBeaconMapAsList(){
        return new ArrayList<OnyxBeacon>(beaconMap.values());
    }

    public static ArrayList<OnyxBeacon> filterSurroundingBeacons(){
        ArrayList<OnyxBeacon> res = getBeaconMapAsList();
        Iterator it = res.iterator();
        while(it.hasNext()){
            OnyxBeacon tmp = (OnyxBeacon) it.next();
            if(!Util.hasSufficientSendingFreq(tmp.lastSignalMeasured)) {
                tmp.resetMedianMeasurement();
                it.remove();
            } else if(tmp.rssi <= Definitions.SIGNAL_TOO_BAD_THRESHOLD) {
                tmp.resetMedianMeasurement();
                it.remove();
            }
        }
//        Log.d(TAG, "Beacon RES SIZE: "+res.size());
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
    public void setMedianRSSI(float medianRSSI) {
        this.medianRSSI = medianRSSI;
    }
    public void setMeasurementStarted(boolean measurementStarted) {
        this.measurementStarted = measurementStarted;
    }
    public boolean isMeasurementStarted() {
        return measurementStarted;
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
