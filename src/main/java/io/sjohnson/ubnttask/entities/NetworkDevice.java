package io.sjohnson.ubnttask.entities;

import io.sjohnson.ubnttask.constructs.NetworkDeviceType;
import io.sjohnson.ubnttask.constructs.ValueOfEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Collection;

@Entity
public class NetworkDevice {
    @Id
    @Pattern(regexp = "(?:[0-9a-f]{2}(?=([:]))(?:\\1[0-9a-f]{2}){5})", message = "must be formatted as 12:34:56:78:90:ab")
    @NotNull(message = "MAC address must provided")
    @Column(length = 17)
    private String macAddress;

    @Nullable
    @Pattern(regexp = "(?:[0-9a-f]{2}(?=([:]))(?:\\1[0-9a-f]{2}){5})", message = "must be formatted as 12:34:56:78:90:ab")
    @Column(length = 17)
    private String uplinkMacAddress;

    @ValueOfEnum(enumClass = NetworkDeviceType.class, message = "must be GATEWAY|SWITCH|ACCESS_POINT")
    private String type;

    @Nullable
    @Size(min = 0, max = 60)
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
