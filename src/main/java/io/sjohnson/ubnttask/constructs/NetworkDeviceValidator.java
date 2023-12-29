package io.sjohnson.ubnttask.constructs;

import io.sjohnson.ubnttask.entities.NetworkDevice;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NetworkDeviceValidator implements Validator {
    @Override
    public boolean supports(Class<?> className) {
        return NetworkDevice.class.equals(className);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NetworkDevice networkDevice = (NetworkDevice) target;

        boolean hasValidMac = networkDevice.getMacAddress().matches("(?:[0-9a-f]{2}(?=([:]))(?:\\1[0-9a-f]{2}){5})");
    }
}
