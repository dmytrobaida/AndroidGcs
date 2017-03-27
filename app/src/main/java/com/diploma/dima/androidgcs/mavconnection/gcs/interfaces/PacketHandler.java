package com.diploma.dima.androidgcs.mavconnection.gcs.interfaces;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.IpPortAddress;

public interface PacketHandler {
    void handlePacket(MAVLinkPacket packet);
    IpPortAddress getVehicleAddress();
}
