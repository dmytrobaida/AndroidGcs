package com.diploma.dima.androidgcs.utils;

import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.models.WayPointType;
import com.diploma.dima.androidgcs.models.Waypoint;

import java.util.List;

public class LandPointGenerator {
    public static void generate(List<Waypoint> waypoints, MapWay mapWay, float gradient) {
        Waypoint land = waypoints.get(waypoints.size() - 1);
        land.delete();
        Waypoint wp = waypoints.get(waypoints.size() - 2);
        float D = (float) Math.sqrt(Math.pow(land.getX() - wp.getX(), 2) + Math.pow(land.getY() - wp.getY(), 2));
        float hB = 2 * D * gradient / 3 + land.getHeight();
        getWaypoint(wp, land, hB, 0.5f, mapWay).save();
        float hA = D * gradient / 3 + land.getHeight();
        getWaypoint(wp, land, hA, 2f, mapWay).save();
        land.save();
    }

    private static Waypoint getWaypoint(Waypoint A, Waypoint B, float height, float lambda, MapWay mapWay) {
        float xm = (A.getX() + lambda * B.getX()) / (1 + lambda);
        float ym = (A.getY() + lambda * B.getY()) / (1 + lambda);
        return new Waypoint(xm, ym, height, mapWay, WayPointType.WayPoint);
    }
}
