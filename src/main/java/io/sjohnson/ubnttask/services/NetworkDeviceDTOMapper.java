package io.sjohnson.ubnttask.services;

import io.sjohnson.ubnttask.constructs.NetworkDeviceDTO;
import io.sjohnson.ubnttask.entities.NetworkDevice;
import org.springframework.stereotype.Component;

@Component
public class NetworkDeviceDTOMapper {
    /**
     * Creates a DTO from Network Device entity
     * @param networkDevice Network Device entity
     * @return DTO of the entity
     */
    public NetworkDeviceDTO toDto(NetworkDevice networkDevice) {
        return new NetworkDeviceDTO(networkDevice.getMacAddress(), networkDevice.getUplink(), networkDevice.getType(), networkDevice.getFriendlyName());
    }

    /**
     * Converts a Network Device DTO to a pending Network Device entity
     *
     * @param deviceDTO DTO of the entity
     * @return pending Network Device entity
     */
    public NetworkDevice toNetworkDevice(NetworkDeviceDTO deviceDTO) {
        NetworkDevice uplink = new NetworkDevice();
        uplink.setMacAddress(deviceDTO.getUplink());

        NetworkDevice networkDevice = new NetworkDevice();
        networkDevice.setUplink(uplink);
        networkDevice.setMacAddress(deviceDTO.getMacAddress());
        networkDevice.setFriendlyName(deviceDTO.getFriendlyName());
        networkDevice.setType(deviceDTO.getType());

        return networkDevice;
    }
}
