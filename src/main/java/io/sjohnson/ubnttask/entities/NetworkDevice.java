package io.sjohnson.ubnttask.entities;

import io.sjohnson.ubnttask.constructs.NetworkDeviceTypeEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
public class NetworkDevice {
    @Id
    private String macAddress;

    @Nullable
    private String uplinkMacAddress;

    @Enumerated(EnumType.STRING)
    private NetworkDeviceTypeEnum type;

    @Nullable
    private String friendlyName;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Nullable
    public String getUplinkMacAddress() {
        return uplinkMacAddress;
    }

    public void setUplinkMacAddress(@Nullable String uplinkMacAddress) {
        this.uplinkMacAddress = uplinkMacAddress;
    }

    public NetworkDeviceTypeEnum getType() {
        return type;
    }

    public void setType(NetworkDeviceTypeEnum type) {
        this.type = type;
    }

    @Nullable
    public String getFriendlyName() {
        return this.friendlyName;
    }

    public void setFriendlyName(@Nullable String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
