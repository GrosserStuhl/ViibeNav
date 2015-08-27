package com.example.indoorbeacon.app.model;

import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TomTheBomb on 15.07.2015.
 */
public class BeaconsMedians implements Serializable {

    private int median1,median2,median3,median4,median5,median6;


    public BeaconsMedians(HashMap<CharBuffer,OnyxBeacon> beacons) {
        ArrayList<Integer> temp = new ArrayList<>();
        Iterator it = beacons.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<CharSequence,OnyxBeacon> pair = (Map.Entry)it.next();
            temp.add((int) pair.getValue().getMedianRSSI());
        }

        median1 = temp.get(0);
        median2 = temp.get(1);
        median3 = temp.get(2);
        median4 = temp.get(3);

        if(beacons.size()>=5)
            median5 = temp.get(4);
        if(beacons.size()==6)
            median6 = temp.get(5);

    }

    public void setMedian5(int median5) {
        this.median5 = median5;
    }

    public void setMedian6(int median6) {
        this.median6 = median6;
    }

    public int getMedian5() {
        return median5;
    }

    public int getMedian6() {
        return median6;
    }

    public void setMedian2(int median2) {
        this.median2 = median2;
    }

    public void setMedian3(int median3) {
        this.median3 = median3;
    }

    public void setMedian4(int median4) {
        this.median4 = median4;
    }

    public int getMedian1() {
        return median1;
    }

    public int getMedian2() {
        return median2;
    }

    public int getMedian3() {
        return median3;
    }

    public int getMedian4() {
        return median4;
    }

    public void setMedian1(int median1) {
        this.median1 = median1;
    }


}
