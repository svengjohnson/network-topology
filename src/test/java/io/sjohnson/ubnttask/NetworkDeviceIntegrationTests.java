package io.sjohnson.ubnttask;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sjohnson.ubnttask.constructs.NetworkDeviceDTO;
import io.sjohnson.ubnttask.entities.NetworkDevice;
import io.sjohnson.ubnttask.exceptions.DeviceCausesNetworkLoopException;
import io.sjohnson.ubnttask.exceptions.InvalidNetworkDeviceException;
import io.sjohnson.ubnttask.repositories.NetworkDeviceRepository;
import io.sjohnson.ubnttask.services.NetworkDeviceDTOMapper;
import io.sjohnson.ubnttask.services.NetworkDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static io.sjohnson.ubnttask.constructs.NetworkDeviceType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("ConstantValue")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = UbntTaskApplication.class)
@AutoConfigureMockMvc
public class NetworkDeviceIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private NetworkDeviceRepository repository;

    @Autowired
    private NetworkDeviceService service;

    @Autowired
    private NetworkDeviceDTOMapper mapper;

    @Test
    public void testGetNetworkDevice_ExpectSuccess() throws Exception {
        String macAddress = "ff:00:00:00:00:00";
        String uplink = null;
        String type = GATEWAY.toString();
        String friendlyName = "UDM SE";

        NetworkDevice device = createAndSaveNetworkDevice(macAddress, uplink, type, friendlyName);
        String deviceJson = asJsonString(mapper.toDto(device));

        mvc.perform(get("/device/"+macAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(deviceJson));
    }

    @Test
    public void testGetNetworkDevice_MalformedMac_ExpectFailure() throws Exception {
        mvc.perform(get("/device/an:00:00:00:00:00")).andExpect(status().is(400));
    }

    @Test
    public void testGetNetworkDevice_DoesntExist_ExpectFailure() throws Exception {
        mvc.perform(get("/device/aa:00:00:00:00:00")).andExpect(status().is(404));
    }

    @Test
    public void testDeleteNetworkDevice_ExpectSuccess() throws Exception {
        String macAddress = "ff:00:00:00:00:00";

        createAndSaveNetworkDevice("ff:00:00:00:00:00", null, GATEWAY.toString(), "to be deleted");

        mvc.perform(delete("/device/"+macAddress)).andExpect(status().isOk());

        assertNetworkDeviceDoesntExist(macAddress);
    }

    @Test
    public void testDeleteNetworkDevice_MalformedMac_ExpectFailure() throws Exception {
        mvc.perform(delete("/device/an:00:00:00:00:00")).andExpect(status().is(400));
    }

    @Test
    public void testDeleteNetworkDevice_DoesntExist_ExpectFailure() throws Exception {
        mvc.perform(delete("/device/aa:00:00:00:00:00")).andExpect(status().is(404));
    }

    @Test
    public void testCreateNetworkDevice_WithNoUplink_ExpectSuccess() throws Exception {
        String macAddress = "ff:00:00:00:00:00";
        String uplink = null;
        String type = GATEWAY.toString();
        String friendlyName = "UDM SE";

        NetworkDeviceDTO dto = new NetworkDeviceDTO(macAddress, uplink, type, friendlyName);
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json))
        ;

        assertNetworkDeviceExists(macAddress, uplink, type, friendlyName);
    }

    @Test
    public void testCreateNetworkDevice_WithNoUplink_NoMac_ExpectFailure() throws Exception {
        NetworkDeviceDTO dto = new NetworkDeviceDTO(null, null, GATEWAY.toString(), "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
        ;
    }

    @Test
    public void testCreateNetworkDevice_WithNoUplink_InvalidType_ExpectFailure() throws Exception {
        NetworkDeviceDTO dto = new NetworkDeviceDTO("ff:00:00:00:00:00", null, "CAMERA", "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
        ;
    }
    @Test
    public void testCreateNetworkDevice_WithNoUplink_MalformedMac_ExpectFailure() throws Exception {
        NetworkDeviceDTO dto = new NetworkDeviceDTO("fx:00:00:00:00:00", null, GATEWAY.toString(), "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
        ;
    }

    @Test
    public void testCreateNetworkDevice_WithNonExistentUplink_ExpectFailure() throws Exception {
        NetworkDeviceDTO dto = new NetworkDeviceDTO("f0:00:00:00:00:00", "f0:00:aa:00:ff:00", GATEWAY.toString(), "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
        ;
    }

    @Test
    public void testUpdateNetworkDevice_WithSelfUplink_NetworkLoop_ExpectFailure() throws Exception {
        createAndSaveNetworkDevice("ff:00:00:00:00:00", null, GATEWAY.toString(), "test");

        NetworkDeviceDTO dto = new NetworkDeviceDTO("ff:00:00:00:00:00", "ff:00:00:00:00:00", GATEWAY.toString(), "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
        ;
    }

    @Test
    public void testUpdateNetworkDevice_WithUplink3LayersUp_NetworkLoop_ExpectFailure() throws Exception {
        createAndSaveNetworkDevice("ff:00:00:00:00:00", null, GATEWAY.toString(), "test");
        createAndSaveNetworkDevice("ff:00:00:00:00:01", "ff:00:00:00:00:00", SWITCH.toString(), "test");
        createAndSaveNetworkDevice("ff:00:00:00:00:02", "ff:00:00:00:00:01", SWITCH.toString(), "test");

        NetworkDeviceDTO dto = new NetworkDeviceDTO("ff:00:00:00:00:00", "ff:00:00:00:00:02", GATEWAY.toString(), "test");
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
        ;
    }

    @Test
    public void testCreateNetworkDevice_WithUplink_ExpectSuccess() throws Exception {
        createAndSaveNetworkDevice("ff:00:00:00:00:01", null, GATEWAY.toString(), "UDM SE");

        String macAddress = "ff:00:00:00:00:02";
        String uplink = "ff:00:00:00:00:01";
        String type = SWITCH.toString();
        String friendlyName = "Switch 48 Enterprise";

        NetworkDeviceDTO dto = new NetworkDeviceDTO(macAddress, uplink, type, friendlyName);
        String json = asJsonString(dto);

        mvc.perform(put("/device")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(json))
        ;

        assertNetworkDeviceExists(macAddress, uplink, type, friendlyName);
    }

    @Test
    public void getAllNetworkDevices_ExpectSuccess() throws Exception {
        createTopology();

        String expectedJson = asJsonString(service.getAll());

        mvc.perform(get("/device"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void getTopology_ExpectSuccess() throws Exception {
        createTopology();

        mvc.perform(get("/device/topology"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].macAddress").value("ff:00:00:00:00:00"))
                .andExpect(jsonPath("[0].uplink").isEmpty())
                .andExpect(jsonPath("[0].type").value(GATEWAY.toString()))
                .andExpect(jsonPath("[0].friendlyName").value("UDM SE"))
                .andExpect(jsonPath("[1].macAddress").value("ff:00:00:00:00:08"))
                .andExpect(jsonPath("[1].uplink").isEmpty())
                .andExpect(jsonPath("[1].type").value(GATEWAY.toString()))
                .andExpect(jsonPath("[1].friendlyName").value("USG Enterprise"))
                .andExpect(jsonPath("[1].downlinks").isEmpty())
                .andExpect(jsonPath("[2].macAddress").value("ff:00:00:00:00:09"))
                .andExpect(jsonPath("[2].uplink").isEmpty())
                .andExpect(jsonPath("[2].type").value(GATEWAY.toString()))
                .andExpect(jsonPath("[2].friendlyName").value("USG Enterprise"))
                .andExpect(jsonPath("[2].downlinks[0].downlinks[0].downlinks[0].macAddress").value("ff:00:00:00:00:0c"))
                .andExpect(jsonPath("[2].downlinks[0].downlinks[0].downlinks[0].uplink").value("ff:00:00:00:00:0b"))
                .andExpect(jsonPath("[2].downlinks[0].downlinks[0].downlinks[0].type").value("ACCESS_POINT"))
                .andExpect(jsonPath("[2].downlinks[0].downlinks[0].downlinks[0].friendlyName").value("AP 6 Lite"))
                .andExpect(jsonPath("[2].downlinks[0].downlinks[0].downlinks[0].downlinks").isEmpty())
                .andExpect(jsonPath("[3]").doesNotExist());
    }

    @Test
    public void getSimpleTopology_ExpectSuccess() throws Exception {
        createTopology();

        mvc.perform(get("/device/topology/simple"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[\"ff:00:00:00:00:00\"][\"ff:00:00:00:00:01\"][\"ff:00:00:00:00:05\"][\"ff:00:00:00:00:06\"]").isMap())
                .andExpect(jsonPath("[\"ff:00:00:00:00:00\"][\"ff:00:00:00:00:01\"][\"ff:00:00:00:00:05\"][\"ff:00:00:00:00:06\"][\"ff:00:00:00:00:07\"]").isEmpty())
                .andExpect(jsonPath("[\"ff:00:00:00:00:08\"]").isEmpty())
                .andExpect(jsonPath("[\"ff:00:00:00:00:09\"]").isMap());
    }

    @Test
    public void getTopologyFromDevice_ExpectSuccess() throws Exception {
        createTopology();

        mvc.perform(get("/device/ff:00:00:00:00:0a/topology"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("macAddress").value("ff:00:00:00:00:0a"))
                .andExpect(jsonPath("uplink").value("ff:00:00:00:00:09"))
                .andExpect(jsonPath("type").value(SWITCH.toString()))
                .andExpect(jsonPath("friendlyName").value("Switch 48 Enterprise PoE"))
                .andExpect(jsonPath("downlinks").isArray())
                .andExpect(jsonPath("downlinks[0].downlinks[1].macAddress").value("ff:00:00:00:00:0d"))
                .andExpect(jsonPath("downlinks[0].downlinks[1].uplink").value("ff:00:00:00:00:0b"))
                .andExpect(jsonPath("downlinks[0].downlinks[1].type").value(ACCESS_POINT.toString()))
                .andExpect(jsonPath("downlinks[0].downlinks[1].friendlyName").value("AP 6 Lite"))
                .andExpect(jsonPath("downlinks[0].downlinks[1].downlinks").isEmpty());
    }

    @Test
    public void getTopologyFromDevice_MalformedMac_ExpectFailure() throws Exception {
        mvc.perform(get("/device/aa00aa00aa00/topology"))
                .andExpect(status().is(400));
    }

    @Test
    public void getTopologyFromDevice_NonexistentDevice_ExpectFailure() throws Exception {
        mvc.perform(get("/device/aa:00:aa:00:aa:00/topology"))
                .andExpect(status().is(404));
    }

    public NetworkDevice createAndSaveNetworkDevice(String macAddress, String uplink, String type, String friendlyName) throws DeviceCausesNetworkLoopException, InvalidNetworkDeviceException {
        NetworkDeviceDTO dto = new NetworkDeviceDTO(macAddress, uplink, type, friendlyName);
        service.save(dto);

        return repository.findByMacAddress(macAddress);
    }

    public void createTopology() throws DeviceCausesNetworkLoopException, InvalidNetworkDeviceException {
        createAndSaveNetworkDevice("ff:00:00:00:00:00", null, GATEWAY.toString(), "UDM SE");
        createAndSaveNetworkDevice("ff:00:00:00:00:01", "ff:00:00:00:00:00", SWITCH.toString(), "Switch 48 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:02", "ff:00:00:00:00:00", ACCESS_POINT.toString(), "AP 6 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:03", "ff:00:00:00:00:00", ACCESS_POINT.toString(), "AP 6 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:04", "ff:00:00:00:00:00", ACCESS_POINT.toString(), "AP 6 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:05", "ff:00:00:00:00:01", SWITCH.toString(), "Switch 48 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:06", "ff:00:00:00:00:05", SWITCH.toString(), "Switch 48 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:07", "ff:00:00:00:00:06", ACCESS_POINT.toString(), "AP 6 Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:08", null, GATEWAY.toString(), "USG Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:09", null, GATEWAY.toString(), "USG Enterprise");
        createAndSaveNetworkDevice("ff:00:00:00:00:0a", "ff:00:00:00:00:09", SWITCH.toString(), "Switch 48 Enterprise PoE");
        createAndSaveNetworkDevice("ff:00:00:00:00:0b", "ff:00:00:00:00:0a", SWITCH.toString(), "Switch 48 Enterprise PoE");
        createAndSaveNetworkDevice("ff:00:00:00:00:0c", "ff:00:00:00:00:0b", ACCESS_POINT.toString(), "AP 6 Lite");
        createAndSaveNetworkDevice("ff:00:00:00:00:0d", "ff:00:00:00:00:0b", ACCESS_POINT.toString(), "AP 6 Lite");
    }

    public void assertNetworkDeviceExists(String macAddress, String uplink, String type, String friendlyName) {
        NetworkDevice device = repository.findByMacAddress(macAddress);

        assertThat(device.getMacAddress()).isEqualTo(macAddress);
        assertThat(device.getUplink()).isEqualTo(uplink);
        assertThat(device.getType()).isEqualTo(type);
        assertThat(device.getFriendlyName()).isEqualTo(friendlyName);
    }

    public void assertNetworkDeviceDoesntExist(String macAddress) {
        NetworkDevice device = repository.findByMacAddress(macAddress);

        assertThat(device).isEqualTo(null);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
