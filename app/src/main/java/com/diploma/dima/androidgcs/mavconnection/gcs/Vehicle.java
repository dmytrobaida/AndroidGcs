package com.diploma.dima.androidgcs.mavconnection.gcs;

import com.diploma.dima.androidgcs.mavconnection.gcs.helpers.IpPortAddress;

public class Vehicle {
    private IpPortAddress address;
    protected int sysid;
    protected int compid;
    protected boolean isConnected = false;

    public Vehicle(IpPortAddress address) {
        this.address = address;
    }

    public int getSysid() {
        return sysid;
    }

    public int getCompid() {
        return compid;
    }

    public IpPortAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("Vehicle: sysid = %d; address = %s", sysid, address.toString());
    }
}
