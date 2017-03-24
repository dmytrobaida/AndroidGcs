package com.diploma.dima.androidgcs.mavconnection.gcs.helpers;

import android.support.annotation.Nullable;

import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Parser;

import java.io.IOException;
import java.net.*;

public class UdpMessageRunnable extends Thread {
    private static final int READ_BUFFER_SIZE = 4096;
    private DatagramSocket datagramSocket;
    private IMessageHandler IMessageHandler;

    public UdpMessageRunnable(int port, IMessageHandler IMessageHandler) throws SocketException, UnknownHostException {
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setBroadcast(true);
        datagramSocket.setReuseAddress(true);
        this.IMessageHandler = IMessageHandler;
    }

    @Override
    public void run() {
        byte[] receiveData = new byte[READ_BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            while (!this.isInterrupted()) {
                datagramSocket.setSoTimeout(5000);
                datagramSocket.receive(receivePacket);
                MAVLinkPacket packet = parsePackage(receivePacket.getData());
                if (packet != null) {
                    IMessageHandler.handle(packet.unpack());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private MAVLinkPacket parsePackage(byte[] bytes) throws IOException {
        MAVLinkPacket pac;
        Parser parser = new Parser();

        for (byte b : bytes) {
            pac = parser.mavlink_parse_char(b & 0xFF);
            if (pac != null) {
                return pac;
            }
        }

        return null;
    }

    public void sendMessage(IpPortAddress address, MAVLinkMessage message) throws IOException {
        byte[] data = message.pack().encodePacket();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address.getAddress(), address.getPort());
        datagramSocket.send(datagramPacket);
    }
//
//    public final void closeConnection() throws IOException {
//        if (datagramSocket != null) {
//            datagramSocket.close();
//        }
//    }
}