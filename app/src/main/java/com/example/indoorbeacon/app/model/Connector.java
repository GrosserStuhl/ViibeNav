package com.example.indoorbeacon.app.model;

import android.net.wifi.WifiManager;

/**
 * Created by TomTheBomb on 01.08.2015.
 */
public class Connector {

    private WifiManager wifi;

    private static Connector singleton;

    private Connector(WifiManager wifi) {
        this.wifi = wifi;
    }

    public static Connector createConnector(WifiManager wifi){
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized(Connector.class) {
            if (singleton == null) {
                singleton = new Connector(wifi);
            }
        }
        return singleton;
    }

    public static Connector getConnector(){
        return singleton;
    }

    public void enableWiFi(){
        wifi.setWifiEnabled(true);
    }

    public void disableWiFi(){
        wifi.setWifiEnabled(false);
    }

    public boolean WiFiEnabled(){
        return wifi.isWifiEnabled();
    }
}
