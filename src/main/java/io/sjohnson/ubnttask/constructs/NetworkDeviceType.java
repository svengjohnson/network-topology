package io.sjohnson.ubnttask.constructs;

public enum NetworkDeviceType {
    GATEWAY,
    SWITCH,
    ACCESS_POINT;

    public static final String NOT_IN_ENUM_ERROR = "Device type must be GATEWAY|SWITCH|ACCESS_POINT";
}
