package com.example.indoorbeacon.app.model;

/**
 * Created by TomTheBomb on 27.07.2015.
 */
public class Setup {

    /**
     * The amount of RSSIs to be measured to calculate on the fly RSSI median from.
     */
    public static  int ON_THE_FLY_AMT_THRESHOLD = 6;

    public static  int MEASUREMENT_AMT_THRESHOLD = 10;

    public static  float FSPL_ELEMENT = 2.5f;


    /**
     * The time difference threshold at which a Beacon should not be considered for Mesurement,
     * as measuring might be taking too long and decrease Measurement performance.
     * It sets the time threshold for the last signal to be recent enough and for the
     * beacon to send frequently enough.
     *
     */
    public static final long TIME_LAST_SIGNAL_THRESHOLD = 3000;

    /**
     * The Signal strength at which measuring makes no sense, as signal strength is
     * unreliably bad.
     */
    public static final int SIGNAL_TOO_BAD_THRESHOLD = -90;




}
