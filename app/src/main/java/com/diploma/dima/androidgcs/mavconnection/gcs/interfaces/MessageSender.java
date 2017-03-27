package com.diploma.dima.androidgcs.mavconnection.gcs.interfaces;


import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.Vehicle;

public interface MessageSender {
    void sendMessage(Vehicle vehicle, MAVLinkMessage message);
}
