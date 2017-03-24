package com.diploma.dima.androidgcs.models;

import java.io.Serializable;

public class Waypoint implements Serializable {
    private float x, y, height;
    private WayPointType wayPointType;

    public Waypoint(float x, float y, float height, WayPointType wayPointType) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.wayPointType = wayPointType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
