package com.diploma.dima.androidgcs.mavconnection.gcs.helpers;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;

import java.io.IOException;

public interface IMessageHandler {
    void handle(MAVLinkMessage message) throws IOException;
}
