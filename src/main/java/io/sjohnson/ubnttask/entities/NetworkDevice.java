package io.sjohnson.ubnttask.entities;

import io.sjohnson.ubnttask.constructs.NetworkDeviceType;
import io.sjohnson.ubnttask.validators.ValueOfEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Collection;

@Entity
public class NetworkDevice implements Serializable {
    public static final String MAC_ADDRESS_REGEXP = "(?:[0-9a-f]{2}(?=([:]))(?:\\1[0-9a-f]{2}){5})";
    public static final String MAC_INVALID_MESSAGE = "MAC address must be formatted as 12:34:56:78:90:ab";
    public static final String MAC_NOT_PROVIDED_MESSAGE = "MAC address must be provided and formatted as 12:34:56:78:90:ab";

    @Id
    @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE)
    @NotNull(message = MAC_NOT_PROVIDED_MESSAGE)
    @Column(length = 17)
    private String macAddress;

    @Nullable
    @ManyToOne(fetch = FetchType.EAGER)
    private NetworkDevice uplink;

    @ValueOfEnum(enumClass = NetworkDeviceType.class, message = NetworkDeviceType.NOT_IN_ENUM_ERROR)
    private String type;

    @Nullable
    @Size(max = 60)
    private String friendlyName;

    @OneToMany(mappedBy = "uplink", fetch = FetchType.LAZY)
    private Collection<NetworkDevice> downlinks;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Nullable
    public String getUplink() {
        return uplink != null ? uplink.macAddress : null;
    }

    public void setUplink(@Nullable NetworkDevice uplinkMacAddress) {
        this.uplink = uplinkMacAddress;
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

    public Collection<NetworkDevice> getDownlinks() {
        return this.downlinks;
    }
}
