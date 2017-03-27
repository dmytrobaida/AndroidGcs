package com.diploma.dima.androidgcs.utils;


import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_item;
import com.diploma.dima.androidgcs.models.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class WaypointsConverter {

    public static List<msg_mission_item> convert(List<Waypoint> waypoints) {
        ArrayList<msg_mission_item> newArray = new ArrayList<>();

        for (Waypoint waypoint : waypoints) {

        }

        return newArray;
    }

    public static List<Waypoint> convertBack(List<msg_mission_item> waypoints) {
        ArrayList<Waypoint> newArray = new ArrayList<>();

        for (msg_mission_item waypoint : waypoints) {

        }

        return newArray;
    }
}