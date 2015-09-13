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

    public boolean isLastRange() {
        return isLastRange;
    }

    public void markAsLastRange() {
        isLastRange = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (relationToNextRange != range.relationToNextRange) return false;
        if (hasEnvironmentalInfos != range.hasEnvironmentalInfos) return false;
        if (isLastRange != range.isLastRange) return false;
        if (coordinates != null ? !coordinates.equals(range.coordinates) : range.coordinates != null) return false;
        return !(environmentalInfos != null ? !environmentalInfos.equals(range.environmentalInfos) : range.environmentalInfos != null);
    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + relationToNextRange;
        result = 31 * result + (hasEnvironmentalInfos ? 1 : 0);
        result = 31 * result + (environmentalInfos != null ? environmentalInfos.hashCode() : 0);
        result = 31 * result + (isLastRange ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return Util.primitivelistToString(coordinates);
    }
}
