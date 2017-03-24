package com.diploma.dima.androidgcs.mavconnection.gcs.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpPortAddress {
    private InetAddress address;
    private int port;

    public IpPortAddress(String address, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", address.toString(), port);
    }
}
