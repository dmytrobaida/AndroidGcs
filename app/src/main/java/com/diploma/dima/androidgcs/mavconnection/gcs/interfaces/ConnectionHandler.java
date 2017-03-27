package com.diploma.dima.androidgcs.mavconnection.gcs.interfaces;

import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;

public interface ConnectionHandler{
    void success(Vehicle vehicle);
    void failure(Vehicle vehicle);
}
