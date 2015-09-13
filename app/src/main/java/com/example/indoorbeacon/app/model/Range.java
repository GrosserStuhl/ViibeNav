package com.example.indoorbeacon.app.model;

import java.util.ArrayList;

/**
 * Created by Dima on 13/09/2015.
 */
public class Range {
    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int LAST = 3;

    private ArrayList<Coordinate> coordinates;
    private int relationToNextRange;
    private boolean hasEnvironmentalInfos = false;
    private ArrayList<String> environmentalInfos;

    public Range(ArrayList<Coordinate> coordinates, int relationToNextRange) {
        this.coordinates = coordinates;
        this.relationToNextRange = relationToNextRange;
        environmentalInfos = new ArrayList<>();
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

    public boolean isHasEnvironmentalInfos() {
        return hasEnvironmentalInfos;
    }

    public void setEnvironmentalInfos(ArrayList<String> environmentalInfos) {
        this.environmentalInfos = environmentalInfos;
        hasEnvironmentalInfos = true;
    }

    public void addEnvironmentalInfos(String info) {
        if (environmentalInfos.isEmpty())
            hasEnvironmentalInfos = true;
        environmentalInfos.add(info);
    }

    public String getRelationToNextRangeAsString(){
        if (relationToNextRange == Range.LAST)
            return " und dann haben Sie ihr Ziel erreicht.";
        else if(relationToNextRange == Range.LEFT)
            return " und dann links abbiegen.";
        else if (relationToNextRange == Range.RIGHT)
            return " und dann rechts abbiegen.";
        return " Achtung! Es konnte keine anschließende Richtung bestimmt werden.";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (relationToNextRange != range.relationToNextRange) return false;
        if (hasEnvironmentalInfos != range.hasEnvironmentalInfos) return false;
        if (coordinates != null ? !coordinates.equals(range.coordinates) : range.coordinates != null) return false;
        return !(environmentalInfos != null ? !environmentalInfos.equals(range.environmentalInfos) : range.environmentalInfos != null);
    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + relationToNextRange;
        result = 31 * result + (hasEnvironmentalInfos ? 1 : 0);
        result = 31 * result + (environmentalInfos != null ? environmentalInfos.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Util.primitiveListToString(coordinates);
    }
}
