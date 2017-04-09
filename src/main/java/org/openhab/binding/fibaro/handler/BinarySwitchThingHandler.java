/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.config.BinarySwitchConfiguration;
import org.openhab.binding.fibaro.internal.model.json.ApiResponse;
import org.openhab.binding.fibaro.internal.model.json.Device;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BinarySwitchThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johan Williams - Initial contribution
 */
public class BinarySwitchThingHandler extends BaseThingHandler implements FibaroUpdateHandler {

    private Logger logger = LoggerFactory.getLogger(BinarySwitchThingHandler.class);

    public static final String PROPERTY_SWITCH = "value";
    public static final String PROPERTY_DEAD = "dead";
    public static final String PROPERTY_ENERGY = "energy";
    public static final String PROPERTY_POWER = "power";

    // Reference to the bridge which we need for communication
    private FibaroBridgeHandler bridge = null;

    public BinarySwitchThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the binary switch handler");
        super.initialize();

        BinarySwitchConfiguration config = getConfigAs(BinarySwitchConfiguration.class);
        logger.debug("config id = {}", config.id);

        boolean validConfig = true;
        String errorMsg = null;

        if (config.id < 1) {
            errorMsg = BinarySwitchConfiguration.ID + "' must be larget than 0";
            validConfig = false;
        }
        if (getBridge() == null) {
            errorMsg = "This thing is not connected to a Fibaro bridge. Please add a Fibaro bridge and connect it in Thing settings.";
            validConfig = false;
        } else {
            bridge = (FibaroBridgeHandler) getBridge().getHandler();

        }

        try {
            if (bridge != null) {
                bridge.getDeviceData(config.id);
            }
        } catch (Exception e) {
            errorMsg = "Could not get device data from the Fibaro api for id " + config.id + ". Does this id exist?";
            validConfig = false;
        }

        if (validConfig) {
            // TODO: startAutomaticRefresh();
            updateStatus(ThingStatus.ONLINE);
            ((FibaroBridgeHandler) getBridge().getHandler()).addThing(config.id, this);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMsg);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            String baseUrl = "http://" + bridge.getIpAddress() + "/api/devices/";

            if (command instanceof RefreshType) {
                updateChannel(channelUID.getId());
            } else if (command instanceof OnOffType) {
                if (command.equals(OnOffType.ON)) {
                    ApiResponse apiResponse = bridge.callFibaroApi(HttpMethod.POST,
                            baseUrl + getId() + "/action/turnOn", "", ApiResponse.class);
                    logger.debug(apiResponse.toString());
                } else if (command.equals(OnOffType.OFF)) {
                    ApiResponse apiResponse = bridge.callFibaroApi(HttpMethod.POST,
                            baseUrl + getId() + "/action/turnOff", "", ApiResponse.class);
                    logger.debug(apiResponse.toString());

                }
                // TODO: Check ApiResponse for error codes
            } else {
                logger.debug("The binary switch handler can't handle command: " + command.toString());
            }

        } catch (Exception e) {
            logger.debug("Failed to handle command " + command.toString() + " : " + e.getMessage());
        }
    }

    /**
     * Returns the configured id
     *
     * @return Thing id
     */
    public int getId() {
        return getConfigAs(BinarySwitchConfiguration.class).id;
    }

    /**
     * Updates a thing channel from device data
     *
     * @param channelId Id of channel to update
     * @throws Exception
     */
    public void updateChannel(String channelId) throws Exception {
        Device device = bridge.getDeviceData(getId());
        switch (channelId) {
            case FibaroBindingConstants.SWITCH:
                updateState(channelId, device.getProperties().getValue().equals("true") ? OnOffType.ON : OnOffType.OFF);
                break;
            case FibaroBindingConstants.DEAD:
                updateState(channelId, device.getProperties().getDead().equals("true") ? OnOffType.ON : OnOffType.OFF);
                break;
            case FibaroBindingConstants.ENERGY:
                updateState(channelId, new DecimalType(device.getProperties().getEnergy()));
                break;
            case FibaroBindingConstants.POWER:
                updateState(channelId, new DecimalType(device.getProperties().getPower()));
                break;
            default:
                logger.debug("Unknown channel: {}", channelId);
                break;
        }
    }

    @Override
    public void update(FibaroUpdate fibaroUpdate) {

        switch (fibaroUpdate.getProperty()) {
            case PROPERTY_SWITCH:
                updateSwitch(fibaroUpdate.getValue());
                break;
            case PROPERTY_DEAD:
                updateDead(fibaroUpdate.getValue());
                break;
            case PROPERTY_ENERGY:
                updateEnergy(fibaroUpdate.getValue());
                break;
            case PROPERTY_POWER:
                updatePower(fibaroUpdate.getValue());
                break;
            default:
                logger.debug("Update received for an unknown property: {}", fibaroUpdate.getProperty());
                break;
        }
    }

    private void updateSwitch(String value) {
        if (value.equals("1")) {
            updateState(FibaroBindingConstants.SWITCH, OnOffType.ON);
        } else {
            updateState(FibaroBindingConstants.SWITCH, OnOffType.OFF);
        }
    }

    private void updateDead(String value) {
        if (value.equals("true")) {
            updateState(FibaroBindingConstants.DEAD, OnOffType.ON);
        } else {
            updateState(FibaroBindingConstants.DEAD, OnOffType.OFF);
        }
    }

    private void updateEnergy(String value) {
        updateState(FibaroBindingConstants.ENERGY, new DecimalType(value));
    }

    private void updatePower(String value) {
        updateState(FibaroBindingConstants.POWER, new DecimalType(value));
    }

}
