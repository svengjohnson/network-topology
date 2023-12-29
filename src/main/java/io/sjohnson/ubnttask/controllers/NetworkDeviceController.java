package io.sjohnson.ubnttask.controllers;

import io.sjohnson.ubnttask.entities.NetworkDevice;
import io.sjohnson.ubnttask.repositories.NetworkDeviceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(path="/device")
public class NetworkDeviceController {

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;

    @GetMapping("")
    public Iterable<NetworkDevice> all() {
        return networkDeviceRepository.findAllByOrderByType();
    }

    @PutMapping("")
    public NetworkDevice createOrUpdateDevice(@Valid @RequestBody NetworkDevice newDevice) {
        return networkDeviceRepository.save(newDevice);
    }

    @GetMapping("/topology")
    public Iterable<NetworkDevice> getTopology() {
        return networkDeviceRepository.findAllByOrderByType();
    }

    @GetMapping("/{macAddress}")
    public NetworkDevice getDevice(@PathVariable String macAddress) {
        return networkDeviceRepository.findByMacAddress(macAddress);
    }

    @GetMapping("/{macAddress}/topology")
    public Iterable<NetworkDevice> getTopologyStartingFromADevice(@PathVariable String macAddress) {
        return networkDeviceRepository.findAllByOrderByType();
    }

    @DeleteMapping("/{macAddress}")
    public void deleteDevice(@PathVariable String macAddress) {
        NetworkDevice device = networkDeviceRepository.findByMacAddress(macAddress);
        networkDeviceRepository.delete(device);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @GetMapping("/test/{macAddress}")
    public boolean asdf(@PathVariable String macAddress) {
        return macAddress.matches("(?:[0-9a-f]{2}(?=([:]))(?:\\1[0-9a-f]{2}){5})");
    }
}
