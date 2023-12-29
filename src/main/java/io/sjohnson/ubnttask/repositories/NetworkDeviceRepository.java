package io.sjohnson.ubnttask.repositories;

import io.sjohnson.ubnttask.entities.NetworkDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworkDeviceRepository extends CrudRepository<NetworkDevice, String> {
    NetworkDevice findByMacAddress(String macAddress);

    @Query(value = "SELECT * FROM network_device WHERE uplink_mac_address IS NULL", nativeQuery = true)
    List<NetworkDevice> findRootDevices();

    @Query(value = "SELECT * FROM network_device ORDER BY FIELD(type,'GATEWAY','SWITCH','ACCESS_POINT')", nativeQuery = true)
    List<NetworkDevice> findAllByOrderByType();
}
