package com.diploma.dima.androidgcs.mavconnection.gcs;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.*;

import com.diploma.dima.androidgcs.mavconnection.gcs.helpers.IMessageHandler;
import com.diploma.dima.androidgcs.mavconnection.gcs.helpers.IpPortAddress;
import com.diploma.dima.androidgcs.mavconnection.gcs.helpers.UdpMessageRunnable;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Gcs{
    private Vehicle vehicle;
    private short sysid = 221;
    private short compid = 1;
    private UdpMessageRunnable udpMavRunnable;
    private ArrayList<msg_mission_item> pointToSend;

    public Gcs(int port) {
        try {
            this.udpMavRunnable = new UdpMessageRunnable(port, new MavMessageHandler());
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Vehicle connectToVehicle(String address, int port) throws IOException, InterruptedException {
        IpPortAddress ipPortAddress = new IpPortAddress(address, port);
        vehicle = new Vehicle(ipPortAddress);
        sendMessageToVehicle(new msg_heartbeat());

        while (!vehicle.isConnected){
            Thread.sleep(1000);
        }

        return vehicle;
    }

    public void disconnectFromVehicle(Vehicle vehicle) {
        //vehicles.remove(vehicle);
        ////
    }

    public void start() {
        udpMavRunnable.start();
    }

    public void stop() {
        udpMavRunnable.interrupt();
    }

    private class MavMessageHandler implements IMessageHandler{
        @Override
        public void handle(MAVLinkMessage message) throws IOException {
            //  System.out.println(message);
            switch (message.msgid) {
                case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
                    msg_heartbeat heartbeat = (msg_heartbeat) message;
                    if (vehicle != null && !vehicle.isConnected) {
                        vehicle.sysid = heartbeat.sysid;
                        vehicle.compid = heartbeat.compid;
                        vehicle.isConnected = true;
                    }
                    // System.out.println(heartbeat);
                    break;

                case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
                    msg_mission_request request = (msg_mission_request) message;
                    msg_mission_item item = pointToSend.get(request.seq);
                    sendMessageToVehicle(item);
                    break;

                case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
                    pointToSend = null;
                    break;

                case msg_mission_request_list.MAVLINK_MSG_ID_MISSION_REQUEST_LIST:
                    break;

                case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
                    break;

                case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
                    break;
            }
        }
    }

    private void sendMessageToVehicle(MAVLinkMessage message) {
        message.sysid = sysid;
        message.compid = compid;
        try {
            udpMavRunnable.sendMessage(vehicle.getAddress(), message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPoints(ArrayList<msg_mission_item> pointToSend){
        this.pointToSend = pointToSend;
        msg_mission_count pointsMsg = new msg_mission_count();
        pointsMsg.target_system = (short) vehicle.sysid;
        pointsMsg.target_component = (short) vehicle.compid;
        pointsMsg.count = pointToSend.size();
        sendMessageToVehicle(pointsMsg);
    }
}
