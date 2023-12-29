package io.sjohnson.ubnttask.controllers;

import io.sjohnson.ubnttask.constructs.NetworkDeviceDTO;
import io.sjohnson.ubnttask.entities.NetworkDevice;
import io.sjohnson.ubnttask.exceptions.InvalidNetworkDeviceException;
import io.sjohnson.ubnttask.services.NetworkDeviceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.sjohnson.ubnttask.entities.NetworkDevice.MAC_ADDRESS_REGEXP;
import static io.sjohnson.ubnttask.entities.NetworkDevice.MAC_INVALID_MESSAGE;


@RestController
@RequestMapping(path="/device")
public class NetworkDeviceController {

    @Autowired
    private final NetworkDeviceService service;

    public NetworkDeviceController(NetworkDeviceService service) {
        this.service = service;
    }

    @GetMapping("")
    public List<NetworkDeviceDTO> all() {
        return service.getAll();
    }

    @PutMapping("")
    public NetworkDevice createOrUpdateDevice(@Valid @RequestBody NetworkDeviceDTO newDeviceDto) throws InvalidNetworkDeviceException {
        return service.save(newDeviceDto);
    }

    @GetMapping("/topology")
    public List<NetworkDevice> getTopology() {
        return service.getTopology();
    }

    // Task specifically states that the topology output should be a tree structure where node is represented as macAddress, this is just that and absolutely nothing more
    @GetMapping("/topology/simple")
    public Map<String, Map<?, ?>> getSimpleTopology() {
        return service.getSimpleTopology();
    }

    @GetMapping("/{macAddress}")
    public NetworkDeviceDTO getDevice(@PathVariable @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE) String macAddress) {
        return service.findByMacAddress(macAddress);
    }

    @GetMapping("/{macAddress}/topology")
    public NetworkDevice getTopologyStartingFromADevice(@PathVariable @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE) String macAddress) {
        return service.getTopologyFromDevice(macAddress);
    }

    @DeleteMapping("/{macAddress}")
    public void deleteDevice(@PathVariable @Pattern(regexp = MAC_ADDRESS_REGEXP, message = MAC_INVALID_MESSAGE) String macAddress) throws InvalidNetworkDeviceException {
        service.delete(macAddress);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidNetworkDeviceException.class)
    public List<String> handleDeviceNotFoundException(InvalidNetworkDeviceException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public List<String> handleMethodValidationException(HandlerMethodValidationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getAllErrors().forEach((error) -> errors.add(error.getDefaultMessage()));
        return errors;
    }
}
