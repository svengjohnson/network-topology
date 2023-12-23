package io.sjohnson.ubnttask.controllers;

import io.sjohnson.ubnttask.constructs.NetworkDeviceTypeEnum;
import io.sjohnson.ubnttask.entities.NetworkDevice;
import io.sjohnson.ubnttask.repositories.NetworkDeviceRepository;
import jakarta.persistence.OrderBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path="/device")
public class NetworkDeviceController {

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;

    @GetMapping("")
    public Iterable<NetworkDevice> all() {
        return networkDeviceRepository.findAllByOrderByType();
    }

    @GetMapping("/{macAddress}")
    public NetworkDevice getDevice(@PathVariable String macAddress) {
        return networkDeviceRepository.findByMacAddress(macAddress);
    }

    @PutMapping("/{macAddress}")
    public NetworkDevice createOrUpdateDevice(@RequestBody NetworkDevice newDevice, @PathVariable String macAddress) {
        NetworkDevice device = new NetworkDevice();
        device.setMacAddress(macAddress);
        device.setUplinkMacAddress(newDevice.getUplinkMacAddress());
        device.setType(newDevice.getType());
        device.setFriendlyName(newDevice.getFriendlyName());
        networkDeviceRepository.save(device);

        return device;
    }
}
