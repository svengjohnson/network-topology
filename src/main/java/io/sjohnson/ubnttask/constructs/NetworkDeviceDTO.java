package io.sjohnson.ubnttask.constructs;

import io.sjohnson.ubnttask.validators.ValueOfEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static io.sjohnson.ubnttask.entities.NetworkDevice.*;

public class NetworkDeviceDTO {
    @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE)
    @NotNull(message = MAC_NOT_PROVIDED_MESSAGE)
    private String macAddress;
    @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE)
    private String uplink;
    @ValueOfEnum(enumClass = NetworkDeviceType.class, message = NetworkDeviceType.NOT_IN_ENUM_ERROR)
    private String type;

    @Size(max = 60)
    private String friendlyName;

    public NetworkDeviceDTO(String macAddress, String uplink, String type, String friendlyName) {
        this.setMacAddress(macAddress);
        this.setUplink(uplink);
        this.setType(type);
        this.setFriendlyName(friendlyName);
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getUplink() {
        return uplink;
    }

    public void setUplink(String uplink) {
        this.uplink = uplink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
