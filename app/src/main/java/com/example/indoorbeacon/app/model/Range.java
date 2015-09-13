package com.example.indoorbeacon.app.model;

import java.util.ArrayList;

/**
 * Created by Dima on 13/09/2015.
 */
public class Range {
    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    private ArrayList<Coordinate> coordinates;
    private int relationFromLastRange;
    private int relationToNextRange;
    private boolean hasEnvironmentalInfos;
    private ArrayList<String> environmentalInfos;

    public Range(ArrayList<Coordinate> coordinates, int relationFromLastRange, int relationToNextRange) {
        this.coordinates = coordinates;
        this.relationFromLastRange = relationFromLastRange;
        this.relationToNextRange = relationToNextRange;
        environmentalInfos = new ArrayList<>();
        hasEnvironmentalInfos = false;
    }

    public Range(ArrayList<Coordinate> coordinates, int relationFromLastRange, int relationToNextRange,
                 ArrayList<String> environmentalInfos) {
        this.coordinates = coordinates;
        this.relationFromLastRange = relationFromLastRange;
        this.relationToNextRange = relationToNextRange;
        this.environmentalInfos = environmentalInfos;
        hasEnvironmentalInfos = true;
    }

    public ArrayList<Coordinate> getCoordList() {
        return coordinates;
    }

    public int getRelationFromLastRange() {
        return relationFromLastRange;
    }

    public int getRelationToNextRange() {
        return relationToNextRange;
    }

    public ArrayList<String> getEnvironmentalInfos() {
        return environmentalInfos;
    }

    public void setEnvironmentalInfos(ArrayList<String> environmentalInfos) {
        this.environmentalInfos = environmentalInfos;
        hasEnvironmentalInfos = true;
    }

    @Override
    public String toString() {
    }
}
