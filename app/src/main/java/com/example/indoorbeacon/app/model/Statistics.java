package com.example.indoorbeacon.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by TomTheBomb on 02.08.2015.
 */
public class Statistics {

    public static double calcStandDeviation(final double[] data){
        return Math.sqrt(calcVariance(data));
    }

    public static double calcStandDeviation(final ArrayList<Double> data){
        return Math.sqrt(calcVariance(data));
    }

    public static double calcVariance(final int[] data){
        double mean = calcMean(data);
        double sum = 0;
        for(double tmp : data)
            sum += Math.pow(tmp-mean,2);

        return sum / (data.length);
    }

    public static double calcVariance(final double[] data){
        double mean = calcMean(data);
        double sum = 0;
        for(double tmp : data)
            sum += Math.pow(tmp-mean,2);

        return sum / (data.length);
    }

    public static double calcVariance(final ArrayList<Double> data){
        double mean = calcMean(data);
        double sum = 0;
        for(double tmp : data)
            sum += Math.pow(tmp-mean,2);

        return sum / (data.size());
    }

    public static double calcMean(final int[] data){
        double sum = 0;
        for(double tmp : data)
            sum += tmp;
        return sum / data.length;
    }

    public static double calcMean(final double[] data){
        double sum = 0;
        for(double tmp : data)
            sum += tmp;
        return sum / data.length;
    }

    public static double calcMean(final ArrayList<Double> data){
        double sum = 0;
        for(double tmp : data)
            sum += tmp;
        return sum / data.size();
    }

    public static double calcMedian(final double[] data){
        Arrays.sort(data);

    /*-1 bec. it's an array!*/
    /* array starts from 0 !*/
        if (data.length % 2 == 0)
            return (double) (data[(data.length / 2) - 1] + data[(data.length / 2) + 1 - 1]) / 2;
        else
            return (double) data[(data.length + 1 - 1) / 2];
    }

    public static double calcMedian(final int[] data){
        Arrays.sort(data);

    /*-1 bec. it's an array!*/
    /* array starts from 0 !*/
        if (data.length % 2 == 0)
            return (double) (data[(data.length / 2) - 1] + data[(data.length / 2) + 1 - 1]) / 2;
        else
            return (double) data[(data.length + 1 - 1) / 2];
    }

    public static double calcMedian(final ArrayList<Integer> data){
        Collections.sort(data);

    /*-1 bec. it's an array!*/
    /* array starts from 0 !*/
        if (data.size() % 2 == 0)
            return (double) (data.get((data.size() / 2) - 1) + data.get((data.size() / 2) + 1 - 1)) / 2;
        else
            return (double) data.get((data.size() + 1 - 1) / 2);
    }

    public static float calcMedianFromFloat(final ArrayList<Float> data){
        Collections.sort(data);

    /*-1 bec. it's an array!*/
    /* array starts from 0 !*/
        if (data.size() % 2 == 0)
            return (data.get((data.size() / 2) - 1) + data.get((data.size() / 2) + 1 - 1)) / 2;
        else
            return data.get((data.size() + 1 - 1) / 2);
    }

    /**
     *  Ein Korrelationskoeffizient kann zwischen
     *  -1 = perfekter negativer (linearer) Zusammenhang und
     *  +1 = perfekter positiver (linearer) ZUsammenhang variieren
     *  0  = Die Variablen sind (linear) unabhaengig. Kein ZUsammenhang
     *  	 Man kann aus x nichts über y vorhersagen
     * @param covariance
     * @param std_old
     * @param std_new
     * @return
     * @throws Exception
     */
    public static double calcPMCorrelation(final double covariance, final double std_old, final double std_new) throws Exception{
        return  covariance / (std_old * std_new);
    }


    /**
     * 5 samples from slave device => (x1’, x2’, x3’, x4’, x5’)
        Learned values:
        Each meter contains 4 tuples
        Each tuple contains 5 learned RSSI values => (x1, x2, x3, x4, x5)
        For each tuple
        Distance = √((x1-x1’)2 + (x2-x2’)2 + (x3-x3’)2 + (x4-x4’)2 + (x5-x5’)2)
        Return the average distance

     *
     *
     *
     * For more on euclidean distance:
     * APPLICATION, COMPARISON, AND IMPROVEMENT OF KNOWN
     RECEIVED SIGNAL STRENGTH INDICATION (RSSI) BASED INDOOR
     LOCALIZATION AND TRACKING METHODS USING ACTIVE RFID
     DEVICES - Bora Ozkaya
     page 14
     * @return
     */
    public static double calcEuclidDist(double[] distsArray){
        double euclidDist = 0;

        // quadrierte Abweichung
        for(int i=0;i<distsArray.length;i++) {
            distsArray[i] = Math.pow(distsArray[i], 2);
            // diese aufsummieren
            euclidDist += distsArray[i];
        }
        // schliesslich durch anzahl der Reader teilen
        euclidDist /= distsArray.length;

        return euclidDist;
    }


//    public static double calcEuclidWeightingFact()




}
