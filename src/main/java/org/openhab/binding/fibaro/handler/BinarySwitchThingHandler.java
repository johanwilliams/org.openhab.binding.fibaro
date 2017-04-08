/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.jetty.http.HttpMethod;
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

    // Reference to the bridge which we need for communication
    private FibaroBridgeHandler bridge = null;

    // Device data. TODO: Store data in cache for a few seconds to avoid calling the api for every channel
    private Device deviceData = null;

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
        // TODO: Call the fibaro API to verify that this id exists and the device is of correct type. This should
        // preferably be done in the refresh to simultaneously get the channel values

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
                updateDeviceData();
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
     * Updates the device data. If the data has expired from cache it will make a call to the Fibaro api to fetch new
     * data
     * 
     * @throws Exception
     */
    private void updateDeviceData() throws Exception {
        String baseUrl = "http://" + bridge.getIpAddress() + "/api/devices/";

        // TODO: Cache this data
        deviceData = bridge.callFibaroApi(HttpMethod.GET, baseUrl + getId(), "", Device.class);
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
     */
    public void updateChannel(String channelId) {
        boolean state = Boolean.parseBoolean(deviceData.getProperties().getValue());
        if (state) {
            updateState(channelId, OnOffType.ON);
        } else {
            updateState(channelId, OnOffType.OFF);
        }
    }

    @Override
    public void update(FibaroUpdate fibaroUpdate) {
        String property = fibaroUpdate.getProperty();

        if (property.equals("value")) {
            int value = Integer.parseInt(fibaroUpdate.getValue());
            if (value == 1) {
                updateState(FibaroBindingConstants.SWITCH, OnOffType.ON);
            } else {
                updateState(FibaroBindingConstants.SWITCH, OnOffType.OFF);
            }
            // TODO: Invalidate the cached data
        }
    }
}
