package io.sjohnson.ubnttask.repositories;

import io.sjohnson.ubnttask.entities.NetworkDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkDeviceRepository extends CrudRepository<NetworkDevice, Integer> {
    NetworkDevice findByMacAddress(String macAddress);

    // TODO: sort during runtime. While it would be cool to offload it to the database, this is mySQL-specific and a hack and code duplication
    @Query(value = "SELECT * FROM network_device ORDER BY FIELD(type,'GATEWAY','SWITCH','ACCESS_POINT')", nativeQuery = true)
    Iterable<NetworkDevice> findAllByOrderByType();
}
