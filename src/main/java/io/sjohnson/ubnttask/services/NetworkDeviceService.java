package io.sjohnson.ubnttask.services;

import io.sjohnson.ubnttask.constructs.NetworkDeviceDTO;
import io.sjohnson.ubnttask.entities.NetworkDevice;
import io.sjohnson.ubnttask.exceptions.DeviceCausesNetworkLoopException;
import io.sjohnson.ubnttask.exceptions.InvalidNetworkDeviceException;
import io.sjohnson.ubnttask.repositories.NetworkDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.sjohnson.ubnttask.constructs.NetworkDeviceType.*;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Service
public class NetworkDeviceService {

    @Autowired
    private NetworkDeviceRepository repository;

    private final NetworkDeviceDTOMapper mapper;

    public NetworkDeviceService(NetworkDeviceDTOMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @return returns a flat, ordered list of all registered network devices
     */
    public List<NetworkDeviceDTO> getAll() {
        List<NetworkDeviceDTO> list = repository.findAll().stream().map(mapper::toDto).collect(toList());

        List<String> definedOrder = Arrays.asList(GATEWAY.toString(), SWITCH.toString(), ACCESS_POINT.toString());
        list.sort(Comparator.comparingInt(d -> definedOrder.indexOf(d.getType())));

        return list;
    }

    /**
     * @param macAddress MAC address of the device
     * @return returns a flat network device
     */
    public NetworkDeviceDTO findByMacAddress(String macAddress) throws InvalidNetworkDeviceException {
        NetworkDevice networkDevice = repository.findByMacAddress(macAddress);

        if (!nonNull(networkDevice)) {
            throw new InvalidNetworkDeviceException(String.format("Device not found: %s", macAddress));
        }

        return mapper.toDto(repository.findByMacAddress(macAddress));
    }

    /**
     * @return returns the device topology tree. We assume that root devices are the ones with no uplink
     */
    public List<NetworkDevice> getTopology() {
        return repository.findRootDevices();
    }

    /**
     * @return same as getTopology(), except nodes are identified as MAC addresses, and it only shows MAC addresses
     */
    public Map<String, Map<?, ?>> getSimpleTopology() {
        List<NetworkDevice> topology = getTopology();

        return simplifyTopology(topology);
    }

    /**
     * @param networkDevices the nested Network Device topology
     * @return macAddress => downlinks<macAddress>[]
     */
    private Map<String, Map<?, ?>> simplifyTopology(List<NetworkDevice> networkDevices) {
        Map<String, Map<?,?>> devices = new HashMap<>();

        networkDevices.forEach((networkDevice) -> devices.put(networkDevice.getMacAddress(), simplifyTopology((List<NetworkDevice>) networkDevice.getDownlinks())));

        return devices;
    }

    /**
     * @param macAddress MAC address of the network device
     * @return nested topology tree starting with the device with MAC address provided
     */
    public NetworkDevice getTopologyFromDevice(String macAddress) throws InvalidNetworkDeviceException {
        NetworkDevice networkDevice = repository.findByMacAddress(macAddress);

        if (!nonNull(networkDevice)) {
            throw new InvalidNetworkDeviceException(String.format("Device not found: %s", macAddress));
        }

        return networkDevice;
    }

    /**
     * Saves or updates a Network Device based on the DTO provided
     *
     * @param newDeviceDto Network Device DTO
     * @return Network Device entity proper
     * @throws InvalidNetworkDeviceException uplink provided doesn't exist
     * @throws DeviceCausesNetworkLoopException registering the device would result in a network loop
     */
    public NetworkDevice save(NetworkDeviceDTO newDeviceDto) throws InvalidNetworkDeviceException, DeviceCausesNetworkLoopException {
        NetworkDevice networkDevice = mapper.toNetworkDevice(newDeviceDto);

        String uplinkMacAddress = networkDevice.getUplink();

        if (nonNull(uplinkMacAddress)) {
            NetworkDevice uplink = repository.findByMacAddress(uplinkMacAddress);

            if (!nonNull(uplink)) {
                throw new InvalidNetworkDeviceException(String.format("Invalid uplink: %s - device not found", uplinkMacAddress));
            }

            networkDevice.setUplink(uplink);
            validateNoNetworkLoop(networkDevice.getMacAddress(), networkDevice.getUplink());
        } else {
            networkDevice.setUplink(null);
        }

        return repository.save(networkDevice);
    }

    /**
     * Deletes a Network Device and orphans all its downlinks
     *
     * @param macAddress MAC address of the device to be deleted
     * @throws InvalidNetworkDeviceException device we're trying to delete doesn't exist
     */
    public void delete(String macAddress) throws InvalidNetworkDeviceException {
        NetworkDevice device = repository.findByMacAddress(macAddress);

        if (!nonNull(device)) {
            throw new InvalidNetworkDeviceException(String.format("Invalid device: %s - device not found", macAddress));
        }

        // orphan all downlinks, but not recursively
        device.getDownlinks().forEach(this::orphanDevice);

        // delete the device
        repository.delete(device);
    }

    /**
     * Orphans a Network Device by removing the uplink reference
     * @param networkDevice Network Device to be orphaned
     */
    private void orphanDevice(NetworkDevice networkDevice) {
        networkDevice.setUplink(null);
        repository.save(networkDevice);
    }

    /**
     * Validates that saving a Network Device won't cause a network loop
     *
     * @param macAddress MAC address of the device we're trying to save
     * @param uplinkMacAddress An uplink of it
     * @throws DeviceCausesNetworkLoopException thrown if a network loop was detected
     */
    private void validateNoNetworkLoop(String macAddress, String uplinkMacAddress) throws DeviceCausesNetworkLoopException, InvalidNetworkDeviceException {
        if (!nonNull(uplinkMacAddress)) {
            return;
        }

        if (Objects.equals(macAddress, uplinkMacAddress)) {
            throw new DeviceCausesNetworkLoopException(String.format("Invalid uplink device: %s - causes a network loop", uplinkMacAddress));
        }

        // we will eventually either run out of uplink devices, or find one with a MAC address that matches the one we want to register as a downlink of it
        validateNoNetworkLoop(macAddress, findByMacAddress(uplinkMacAddress).getUplink());
    }
}
