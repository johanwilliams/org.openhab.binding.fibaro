package org.openhab.binding.fibaro.discovery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.handler.BridgeStatusListener;
import org.openhab.binding.fibaro.handler.FibaroGatewayBridgeHandler;
import org.openhab.binding.fibaro.internal.model.json.FibaroDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FibaroDeviceDiscoveryService} is used to discover devices that are connected to the Fibaro HC2.
 *
 * @author Martin Wennerberg
 *
 */
public class FibaroDeviceDiscoveryService extends AbstractDiscoveryService implements BridgeStatusListener {

    private Logger logger = LoggerFactory.getLogger(FibaroDeviceDiscoveryService.class);

    private static final int TIMEOUT = 5;

    FibaroGatewayBridgeHandler bridge;

    public FibaroDeviceDiscoveryService(FibaroGatewayBridgeHandler bridge) {
        super(FibaroBindingConstants.SUPPORTED_THING_TYPES_UIDS, TIMEOUT, true);
        logger.debug("HarmonyDeviceDiscoveryService {}", bridge);
        this.bridge = bridge;
        this.bridge.addBridgeStatusListener(this);
    }

    @Override
    protected void startScan() {
        try {
            discoverDevices();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        try {
            discoverDevices();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatus status) {
        if (status.equals(ThingStatus.ONLINE)) {
            try {
                discoverDevices();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    protected void deactivate() {
        super.deactivate();
        bridge.removeBridgeStatusListener(this);
    }

    /**
     * Discovers devices connected to the Fibaro Gateway
     * Only discovers devices that are enabled and visible.
     */
    private void discoverDevices() throws Exception {
        if (bridge.getFibaroServer() == null) {
            logger.debug("Fibaro server not connected, scanning postponed.");
            return;
        }
        logger.debug("getting devices on {}", bridge.getThing().getUID().getId());

        List<FibaroDevice> devices = Arrays.asList(bridge.callFibaroApi(HttpMethod.GET,
                "http://" + bridge.getIpAddress() + "/api/devices", "", FibaroDevice[].class));

        for (FibaroDevice device : devices) {

            logger.debug("Processing found Fibaro device {}", device.getId());

            if (!device.isEnabled() || !device.isVisible()) {
                logger.debug("Fibaro device {} is disabled or hidden. Ignoring.", device.getId());
                continue;
            }

            ThingTypeUID type = getDeviceThingType(device.getType());
            if (type == null) {
                logger.debug("Unknown device type {} found. Ignoring.", device.getType());
                continue;
            }

            ThingUID bridgeUID = bridge.getThing().getUID();
            int id = device.getId();

            ThingUID thingUID = new ThingUID(type, bridgeUID, String.valueOf(id));

            Map<String, Object> properties = new HashMap<>(2);
            properties.put("id", id);
            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                    .withBridge(bridgeUID).withLabel(device.getName()).build();
            thingDiscovered(discoveryResult);

        }
    }

    /*
     * TODO Move to some other processor or factory
     */
    private ThingTypeUID getDeviceThingType(String deviceType) {

        if ("com.fibaro.doorSensor".equals(deviceType)) {
            return FibaroBindingConstants.THING_TYPE_DOOR_SENSOR;
        } else if ("com.fibaro.FGMS001".equals(deviceType)) {
            return FibaroBindingConstants.THING_TYPE_MOTION_SENSOR;
        }
        if ("com.fibaro.temperatureSensor".equals(deviceType) || "com.fibaro.FGMS001v2".equals(deviceType)
                || "com.fibaro.lightSensor".equals(deviceType) || "com.fibaro.seismometer".equals(deviceType)
                || "com.fibaro.FGSS001".equals(deviceType) || "com.fibaro.heatDetector".equals(deviceType)
                || "com.fibaro.thermostatDanfoss".equals(deviceType) || "com.fibaro.multilevelSensor".equals(deviceType)
                || "com.fibaro.accelerometer".equals(deviceType) || "com.fibaro.FGFS101".equals(deviceType)) {
            return FibaroBindingConstants.THING_TYPE_SENSOR;

        } else if ("com.fibaro.binarySwitch".equals(deviceType) || "com.fibaro.FGD212".equals(deviceType)
                || "com.fibaro.setPoint".equals(deviceType) || "com.fibaro.operatingMode".equals(deviceType)
                || "com.fibaro.FGWP101".equals(deviceType) || "com.fibaro.FGRGBW441M".equals(deviceType)
                || "com.fibaro.multilevelSwitch".equals(deviceType) || "com.fibaro.doorLock".equals(deviceType)) {
            return FibaroBindingConstants.THING_TYPE_ACTOR;
        }
        return null;
    }

}
