package com.diploma.dima.androidgcs.models;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.io.Serializable;

public class Waypoint extends SugarRecord {
    double x;
    double y;
    double height;
    WayPointType wayPointType;

    MapWay mapWay;

    public Waypoint() {
        x = 0;
        y = 0;
        height = 0;
        wayPointType = WayPointType.WayPoint;
    }

    public Waypoint(double x, double y, float height, MapWay mapWay, WayPointType wayPointType) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.wayPointType = wayPointType;
        this.mapWay = mapWay;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public LatLng getlatLng() {
        return new LatLng(x, y);
    }

    public void setWayPointType(WayPointType type){
        wayPointType = type;
    }

    public WayPointType getWayPointType(){
        return wayPointType;
    }
}
