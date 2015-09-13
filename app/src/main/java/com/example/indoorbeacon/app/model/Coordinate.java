package com.example.indoorbeacon.app.model;

import java.util.Objects;

/**
 * Created by TomTheBomb on 23.06.2015.
 */
public class Coordinate {

    private double floor;
    private double x;
    private double y;


    public Coordinate(double floor, double x, double y) {
        this.floor = floor;
        this.x = x;
        this.y = y;
    }

    public void setY_up() {
        y++;
    }

    public void setY_down() {
        y--;
    }

    public void setX_up() {
        x++;
    }

    public void setX_down() {
        x--;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isValid() {
        return x >= 0 && y >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Objects.equals(floor, that.floor) &&
                Objects.equals(x, that.x) &&
                Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, x, y);
    }

    @Override
    public String toString() {
        return "x: " + getX() + ", " + "y: " + getY() /* + " | " + "floor: " + getFloor() */;
    }
}
