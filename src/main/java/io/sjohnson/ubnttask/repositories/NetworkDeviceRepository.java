package io.sjohnson.ubnttask.repositories;

import io.sjohnson.ubnttask.entities.NetworkDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NetworkDeviceRepository extends CrudRepository<NetworkDevice, String> {
    NetworkDevice findByMacAddress(String macAddress);

    @Query(value = "SELECT nd FROM NetworkDevice nd WHERE nd.uplink IS NULL")
    List<NetworkDevice> findRootDevices();

    @NonNull
    List<NetworkDevice> findAll();
}
