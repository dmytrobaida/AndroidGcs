package com.diploma.dima.androidgcs.mavconnection.gcs.network;


import android.support.annotation.Nullable;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Parser;

import java.io.IOException;

public class PacketParser {
    private static Parser parser = new Parser();

    @Nullable
    public static MAVLinkPacket parse(byte[] bytes) throws IOException {
        MAVLinkPacket pac;

        for (byte b : bytes) {
            pac = parser.mavlink_parse_char(b & 0xFF);
            if (pac != null) {
                return pac;
            }
        }

        return null;
    }
}
