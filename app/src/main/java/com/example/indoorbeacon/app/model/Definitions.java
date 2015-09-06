package com.example.indoorbeacon.app.model;

/**
 * Created by TomTheBomb on 27.07.2015.
 */
public class Definitions {

    /**
     * The amount of RSSIs to be measured to calculate on the fly RSSI median from.
     */
    public static int MEASUREMENT_AMOUNT = 6;


    /**
     * This is the environmental constant for distance calculation
     * based on the Free Space Path Loss model
     */
    public static float FSPL_ELEMENT = 2.5f;


    /**
     * The time difference threshold at which a Beacon should not be considered for Mesurement,
     * as measuring might be taking too long and decrease Measurement performance.
     * It sets the time threshold for the last signal to be recent enough and for the
     * beacon to send frequently enough.
     */
    public static final long TIME_LAST_SIGNAL_THRESHOLD = 3000;

    /**
     * The Signal strength at which measuring makes no sense, as signal strength is
     * unreliably bad.
     */
    public static final int SIGNAL_TOO_BAD_THRESHOLD = -90;


    /**
     * The time it takes a user to make a step.
     * Used to limit stepCount to a realistic rate.
     */
    public static final int TIME_FOR_STEP = 1000;

    /**
     * The accelerator Y value threshold at which a step is recognized.
     * (Used in combination with the Z value threshold)
     */
    public static final float STEP_THRESHOLD_Y = 0.15f;

    /**
     * The accelerator Z value threshold at which a step is recognized.
     * (Used in combination with the Y value threshold)
     */
    public static final float STEP_THRESHOLD_Z = 0.4f;

    /**
     * The minimum amount of steps it takes for the System to tell if the user is walking.
     */
    public static final int MIN_STEP_AMOUNT_FOR_WALKING = 2;

    public static final float WALKED_WALKED_CENTIMETERS_PER_SECOND = 70f;

    public static final int ANCHORPOINT_DISTANCE_IN_CM = 100;
}
