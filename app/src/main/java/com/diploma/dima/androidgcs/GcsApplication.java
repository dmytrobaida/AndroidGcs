package com.diploma.dima.androidgcs;

import com.diploma.dima.androidgcs.mavconnection.gcs.Gcs;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;
import com.orm.SugarApp;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GcsApplication extends SugarApp {
    private Gcs groundControlStation;
    private List<Vehicle> vehicles = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            groundControlStation = new Gcs(22813);
            groundControlStation.startListening();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public Gcs getGroundControlStation() {
        return groundControlStation;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
