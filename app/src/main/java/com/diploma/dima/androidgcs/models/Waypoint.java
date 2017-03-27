package com.diploma.dima.androidgcs.models;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_item;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.enums.MAV_CMD;
import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.io.Serializable;

public class Waypoint extends SugarRecord {
    float x;
    float y;
    float height;
    WayPointType wayPointType;

    MapWay mapWay;

    public Waypoint() {
        x = 0;
        y = 0;
        height = 0;
        wayPointType = WayPointType.WayPoint;
    }

    public Waypoint(float x, float y, float height, MapWay mapWay, WayPointType wayPointType) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.wayPointType = wayPointType;
        this.mapWay = mapWay;
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

    public LatLng getlatLng() {
        return new LatLng(x, y);
    }

    public void setWayPointType(WayPointType type){
        wayPointType = type;
    }

    public WayPointType getWayPointType(){
        return wayPointType;
    }

    public msg_mission_item getMavLinkItem(){
        msg_mission_item item = new msg_mission_item();
        item.x = x;
        item.y = y;
        item.z = height;

        switch (wayPointType){
            case TakeOff:
                item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
                break;
            case Land:
                item.command = MAV_CMD.MAV_CMD_NAV_LAND;
                break;
            case WayPoint:
                item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
                break;
        }

        return item;
    }
}
