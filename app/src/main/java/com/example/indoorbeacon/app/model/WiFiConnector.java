package com.example.indoorbeacon.app.model;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by TomTheBomb on 01.08.2015.
 */
public class WiFiConnector {

    private WifiManager wifi;

    private static WiFiConnector singleton;

    private WiFiConnector(WifiManager wifi) {
        this.wifi = wifi;
    }

    public static WiFiConnector getConnector(Activity act){
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized(WiFiConnector.class) {
            if (singleton == null) {
                WifiManager wifi = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
                singleton = new WiFiConnector(wifi);
            }
        }
        return singleton;
    }

    public static WiFiConnector getConnector(){
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
