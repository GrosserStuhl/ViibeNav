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
    private int relationToNextRange;
    private boolean hasEnvironmentalInfos = false;
    private ArrayList<String> environmentalInfos;
    private boolean isLastRange = false;

    public Range(ArrayList<Coordinate> coordinates, int relationToNextRange) {
        this.coordinates = coordinates;
        this.relationToNextRange = relationToNextRange;
        environmentalInfos = new ArrayList<>();
    }

    public Range(ArrayList<Coordinate> coordinates, int relationToNextRange,
                 ArrayList<String> environmentalInfos) {
        this.coordinates = coordinates;
        this.relationToNextRange = relationToNextRange;
        this.environmentalInfos = environmentalInfos;
        hasEnvironmentalInfos = true;
    }

    public ArrayList<Coordinate> getCoordList() {
        return coordinates;
    }

    public int getRelationToNextRange() {
        return relationToNextRange;
    }

    public void setRelationToNextRange(int relationToNextRange) {
        this.relationToNextRange = relationToNextRange;
    }

    public ArrayList<String> getEnvironmentalInfos() {
        return environmentalInfos;
    }

    public void setEnvironmentalInfos(ArrayList<String> environmentalInfos) {
        this.environmentalInfos = environmentalInfos;
        hasEnvironmentalInfos = true;
    }

    public boolean isLastRange() {
        return isLastRange;
    }

    public void markAsLastRange() {
        isLastRange = true;
    }

    @Override
    public String toString() {
        return Util.primitivelistToString(coordinates);
    }
}
