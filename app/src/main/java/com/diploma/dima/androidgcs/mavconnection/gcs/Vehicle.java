package com.diploma.dima.androidgcs.mavconnection.gcs;

import android.support.annotation.NonNull;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_attitude;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_global_position_int;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_heartbeat;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_ack;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_clear_all;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_count;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_current;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_item;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_request;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_mission_request_list;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common.msg_sys_status;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.enums.MAV_CMD;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.enums.MAV_MISSION_RESULT;
import com.diploma.dima.androidgcs.mavconnection.gcs.exceptions.VehicleBusyException;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.Action;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ActionWithMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.ConnectionHandler;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.MessageSender;
import com.diploma.dima.androidgcs.mavconnection.gcs.interfaces.PacketHandler;
import com.diploma.dima.androidgcs.mavconnection.gcs.network.IpPortAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Vehicle implements PacketHandler {
    //Vehicle params
    private IpPortAddress vehicleAddress;
    private boolean connected = false;
    private short sysid;
    private short compid;
    private List<msg_mission_item> pointsBuffer = new ArrayList<>();
    private VehicleParameters vehicleParameters = new VehicleParameters();

    //Util vars
    private final long connectionTime = 5000;
    private Timer timer = new Timer();
    private MessageSender messageSender;
    private int receiveCount;
    private boolean receiving = false;
    private boolean sending = false;

    //Events
    private Action onHeartbeat;
    private ActionWithMessage<String> onPointsSent;
    private ActionWithMessage<List<msg_mission_item>> onPointsReceived;
    private ConnectionHandler connectionHandler;

    Vehicle(IpPortAddress vehicleAddress, ConnectionHandler connectionHandler, MessageSender messageSender) {
        this.vehicleAddress = vehicleAddress;
        this.messageSender = messageSender;
        this.connectionHandler = connectionHandler;
    }

    void connect() {
        final Vehicle vehicle = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectionHandler.failure(vehicle);
                timer.cancel();
                timer.purge();
            }
        }, connectionTime);
        sendMessage(new msg_heartbeat());
    }

    @Override
    public IpPortAddress getVehicleAddress() {
        return vehicleAddress;
    }

    @Override
    public void handlePacket(MAVLinkPacket packet) {
        MAVLinkMessage message = packet.unpack();

        switch (message.msgid) {
            case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
                msg_heartbeat heartbeat = (msg_heartbeat) message;
                sysid = (short) heartbeat.sysid;
                compid = (short) heartbeat.compid;
                vehicleParameters.setMode(heartbeat.base_mode);
                if (!connected) {
                    timer.cancel();
                    connected = true;
                    connectionHandler.success(this);
                }
                if (onHeartbeat != null) {
                    onHeartbeat.handle();
                }
                break;

            case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
                msg_mission_request sendRequest = (msg_mission_request) message;
                if (sendRequest.sysid == sysid && sending) {
                    msg_mission_item itemToSend = pointsBuffer.get(sendRequest.seq);
                    itemToSend.target_component = compid;
                    itemToSend.target_system = sysid;
                    itemToSend.seq = sendRequest.seq;
                    sendMessage(itemToSend);
                }
                break;

            case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
                msg_mission_ack ack = (msg_mission_ack) message;
                if (ack.sysid == sysid && sending && onPointsSent != null) {
                    sending = false;
                    if (ack.type == MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED) {
                        onPointsSent.handle("Mission Accepted");
                    } else {
                        onPointsSent.handle(String.format("Error code: %d", ack.type));
                    }
                }
                break;

            case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
                msg_mission_count msgMissionCount = (msg_mission_count) message;
                if (msgMissionCount.sysid == sysid && receiving && msgMissionCount.count > 0) {
                    if (pointsBuffer.size() > 0) {
                        pointsBuffer.clear();
                    }
                    receiveCount = msgMissionCount.count;
                    msg_mission_request receiveRequest = new msg_mission_request();
                    receiveRequest.target_component = compid;
                    receiveRequest.target_system = sysid;
                    receiveRequest.seq = 0;
                    sendMessage(receiveRequest);
                } else {
                    if (receiving) {
                        receiving = false;
                        missionAckMessage((short) MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED);
                        onPointsReceived.handle(new ArrayList<msg_mission_item>());
                    } else {
                        missionAckMessage((short) MAV_MISSION_RESULT.MAV_MISSION_ERROR);
                    }
                }
                break;

            case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
                msg_mission_item receivedItem = (msg_mission_item) message;
                if (receivedItem.sysid == sysid && receiving && onPointsReceived != null) {
                    pointsBuffer.add(receivedItem);
                    if (pointsBuffer.size() < receiveCount) {
                        msg_mission_request newRequest = new msg_mission_request();
                        newRequest.target_component = compid;
                        newRequest.target_system = sysid;
                        newRequest.seq = pointsBuffer.size();
                        sendMessage(newRequest);
                    } else {
                        receiving = false;
                        missionAckMessage((short) MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED);
                        List<msg_mission_item> returnArray = new ArrayList<>();
                        returnArray.addAll(pointsBuffer);
                        pointsBuffer.clear();
                        onPointsReceived.handle(returnArray);
                    }
                } else {
                    missionAckMessage((short) MAV_MISSION_RESULT.MAV_MISSION_ERROR);
                }
                break;

            case msg_sys_status.MAVLINK_MSG_ID_SYS_STATUS:
                vehicleParameters.setSysStatus((msg_sys_status) message);
                break;

            case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                vehicleParameters.setAttitude((msg_attitude) message);
                break;

            case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT:
                vehicleParameters.setGlobalPosition((msg_global_position_int) message);
                break;

            case msg_mission_current.MAVLINK_MSG_ID_MISSION_CURRENT:
                vehicleParameters.setMissionCurrent((msg_mission_current) message);
                break;
        }
    }

    @NonNull
    private void missionAckMessage(short type) {
        msg_mission_ack receiveAck = new msg_mission_ack();
        receiveAck.type = type;
        receiveAck.target_component = compid;
        receiveAck.target_system = sysid;
        sendMessage(receiveAck);
    }

    public void sendMessage(MAVLinkMessage message) {
        messageSender.sendMessage(this, message);
    }

    public void sendPoints(List<msg_mission_item> points, ActionWithMessage<String> onPointsSent) throws VehicleBusyException {
        if (!sending && !receiving) {
            if (points.size() > 0) {
                this.onPointsSent = onPointsSent;
                if (pointsBuffer.size() > 0) {
                    pointsBuffer.clear();
                }
                //   pointsBuffer.add(getVehiclePosition());
                pointsBuffer.addAll(points);
                msg_mission_count missionCount = new msg_mission_count();
                missionCount.target_component = compid;
                missionCount.target_system = sysid;
                missionCount.count = pointsBuffer.size();
                sending = true;
                sendMessage(missionCount);
            }
        } else {
            if (sending) throw new VehicleBusyException(this, "Sending now");
            else throw new VehicleBusyException(this, "Receiving now");
        }
    }

    @NonNull
    public msg_mission_item getVehiclePosition() {
        msg_mission_item vehiclePos = new msg_mission_item();
        vehiclePos.x = vehicleParameters.getLat();
        vehiclePos.y = vehicleParameters.getLng();
        vehiclePos.z = vehicleParameters.getAlt();
        vehiclePos.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
        return vehiclePos;
    }

    public void receivePoints(ActionWithMessage<List<msg_mission_item>> onPointsReceived) throws VehicleBusyException {
        if (!sending && !receiving) {
            this.onPointsReceived = onPointsReceived;
            if (pointsBuffer.size() > 0) {
                pointsBuffer.clear();
            }
            msg_mission_request_list msgMissionRequestList = new msg_mission_request_list();
            msgMissionRequestList.target_component = compid;
            msgMissionRequestList.target_system = sysid;
            receiving = true;
            sendMessage(msgMissionRequestList);
        } else {
            if (receiving) throw new VehicleBusyException(this, "Receiving now");
            else throw new VehicleBusyException(this, "Sending now");
        }
    }

    public void clearPoints() throws VehicleBusyException {
        if (!sending && !receiving) {
            msg_mission_clear_all clearAll = new msg_mission_clear_all();
            clearAll.target_component = compid;
            clearAll.target_system = sysid;
            sendMessage(clearAll);
        } else {
            if (receiving) throw new VehicleBusyException(this, "Receiving now");
            else throw new VehicleBusyException(this, "Sending now");
        }
    }

    public void setOnHeartbeatHandler(Action heartbeatHandler) {
        this.onHeartbeat = heartbeatHandler;
    }

    public int getSysid() {
        return sysid;
    }

    public int getCompid() {
        return compid;
    }

    @Override
    public String toString() {
        return String.format("Address = %s", vehicleAddress.toString());
    }

    public VehicleParameters getVehicleParameters() {
        return vehicleParameters;
    }
}
