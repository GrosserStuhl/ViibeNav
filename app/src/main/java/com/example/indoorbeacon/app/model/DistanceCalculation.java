package com.example.indoorbeacon.app.model;

/**
 * Created by TomTheBomb on 15.07.2015.
 */
public class DistanceCalculation {

    /**
     * Calculates Distance from FSPL
     * @param rssi
     * @param txPower
     * @return
     */
    public static double calculateDistanceFromFreeSpacePathLossModel(double rssi, int txPower){
         /*
             * RSSI = TxPower - 10 * n * lg(d)
             * n = 2 (in free space)
             *
             * d = 10 ^ ((TxPower - RSSI) / (10 * n))
             */

        double distance =  Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
        return distance;
    }

    /**
     * Calculates distance from BestFit Regression
     * @param rssi
     * @param txPower
     * @return
     */
    public static double calculateDistanceFromBestFitRegression(double rssi, int txPower){

        if (rssi == 0)
            return -1.0; // if we cannot determine distance, return -1.


        double ratio = rssi*1.0/txPower;
        double distance;

        if (ratio < 1.0)
            distance = Math.pow(ratio,10);
        else
            distance = (0.42093)*Math.pow(ratio,6.9476) + 0.54992;

        return distance;
    }

}
