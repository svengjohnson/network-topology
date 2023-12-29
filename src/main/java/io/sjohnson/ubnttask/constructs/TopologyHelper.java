package io.sjohnson.ubnttask.constructs;

import io.sjohnson.ubnttask.entities.NetworkDevice;

public class TopologyHelper {
    public Iterable<NetworkDevice> getTopology(Iterable<NetworkDevice> allDevices, NetworkDevice networkDevice) {

        return allDevices;
    }

    public Iterable<NetworkDevice> getTopology(Iterable<NetworkDevice> allDevices) {

        return null;
    }

    private Iterable<NetworkDevice> findChildren(Iterable<NetworkDevice> allDevices, NetworkDevice networkDevice) {
        String parentDeviceMacAddress = networkDevice.getMacAddress();

        return null;
    }
}
