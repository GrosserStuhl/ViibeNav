package com.example.indoorbeacon.app.model;

/**
 * Created by TomTheBomb on 27.07.2015.
 */
public class Setup {

    /**
     * The amount of RSSIs to be measured to calculate on the fly RSSI median from.
     */
    public static final int ON_THE_FLY_THRESHOLD = 6;

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
    public static final int SIGNAL_TOO_BAD_THRESHOLD = -98;





    public static final boolean EVALUATE_MEDIAN_QUALITY = false;

    /**
     * The signal quality of the measured beacons used to weigh and evaluate its
     * use for the measurement
     */
    public static final int STRONG_SIGNAL_QUALITY = -60;

    /**
     * The signal quality of the measured beacons used to weigh and evaluate its
     * use for the measurement
     */
    public static final int MEDIUM_SIGNAL_QUALITY = -73;

    /**
     * The signal quality of the measured beacons used to weigh and evaluate its
     * use for the measurement
     */
    public static final int LOW_SIGNAL_QUALITY = -80;

}
