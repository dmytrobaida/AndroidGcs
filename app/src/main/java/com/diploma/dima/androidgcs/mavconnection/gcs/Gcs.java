package com.diploma.dima.androidgcs.mavconnection.gcs;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.exceptions.GcsNotListeningException;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ConnectionHandler;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.MessageSender;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.IpPortAddress;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.UdpMavWorker;

import java.net.SocketException;

public class Gcs {
    private UdpMavWorker udpMavWorker;
    private final int sysid = 213;
    private final int compid = 1;
    private boolean listening = false;

    public Gcs(int port) throws SocketException {
        udpMavWorker = new UdpMavWorker(port);
    }

    public void startListening() {
        udpMavWorker.start();
        listening = true;
    }

    public void stopListening() {
        udpMavWorker.stopWorker();
        listening = false;
    }

    public void connectToVehicle(IpPortAddress vehicleAddress, ConnectionHandler connectionHandler) throws GcsNotListeningException {
        if (listening) {
            Vehicle vehicle = new Vehicle(vehicleAddress, connectionHandler, new MessageSender() {
                @Override
                public void sendMessage(Vehicle vehicle, MAVLinkMessage message) {
                    message.sysid = sysid;
                    message.compid = compid;
                    if (udpMavWorker != null) {
                        udpMavWorker.sendPacket(vehicle.getVehicleAddress(), message.pack());
                    }
                }
            });
            udpMavWorker.addPacketHandler(vehicle);
            vehicle.connect();
        } else {
            throw new GcsNotListeningException();
        }
    }

    public void disconnectVehicle(Vehicle vehicle) {
        udpMavWorker.deletePacketHandler(vehicle);
    }
}
