package com.diploma.dima.androidgcs.utils;

import android.location.Location;

import com.diploma.dima.androidgcs.models.MapWay;
import com.diploma.dima.androidgcs.models.WayPointType;
import com.diploma.dima.androidgcs.models.Waypoint;

public class LandPointGenerator {
    public static void generate(MapWay mapWay, float gradient) {
        Waypoint land = mapWay.getWaypoints().get(mapWay.getWaypoints().size() - 1);
        land.delete();
        Waypoint wp = mapWay.getWaypoints().get(mapWay.getWaypoints().size() - 1);
        float D = getDistance(wp, land);
        float hB = 2 * D * gradient / 3 + land.getHeight();
        getWaypoint(wp, land, hB, 0.5f, mapWay).save();
        float hA = D * gradient / 3 + land.getHeight();
        getWaypoint(wp, land, hA, 2f, mapWay).save();
        land.clone().save();
    }

    private static Waypoint getWaypoint(Waypoint A, Waypoint B, float height, float lambda, MapWay mapWay) {
        float xm = (A.getX() + lambda * B.getX()) / (1 + lambda);
        float ym = (A.getY() + lambda * B.getY()) / (1 + lambda);
        return new Waypoint(xm, ym, height, mapWay, WayPointType.WayPoint);
    }

    private static float getDistance(Waypoint startWP, Waypoint endWp) {
        float[] distances = new float[1];
        Location.distanceBetween(startWP.getX(), startWP.getY(), endWp.getX(), endWp.getY(), distances);
        return distances[0];
    }
}
