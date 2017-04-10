/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.config.DimmerConfiguration;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.openhab.binding.fibaro.internal.model.json.ApiResponse;
import org.openhab.binding.fibaro.internal.model.json.Arguments;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DimmerThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johan Williams - Initial contribution
 */
public class DimmerThingHandler extends FibaroThingHandler {

    private Logger logger = LoggerFactory.getLogger(DimmerThingHandler.class);

    public static final String PROPERTY_SWITCH = "value";
    public static final String PROPERTY_DEAD = "dead";
    public static final String PROPERTY_ENERGY = "energy";
    public static final String PROPERTY_POWER = "power";

    public static final String ACTION_SET_VALUE = "setValue";
    public static final String ACTION_ON = "turnOn";
    public static final String ACTION_OFF = "turnOff";
    public static final String ACTION_INCREASE = "startLevelIncrease";
    public static final String ACTION_DECREASE = "startLevelDecrease";

    public DimmerThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            init();
            updateStatus(ThingStatus.ONLINE);
        } catch (FibaroConfigurationException e) {
            // TODO Auto-generated catch block
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void init() throws FibaroConfigurationException {
        super.init();

        DimmerConfiguration config = getConfigAs(DimmerConfiguration.class);
        logger.debug("Initializing the dimmer handler with id {}", config.id);

        if (config.id < 1) {
            throw new FibaroConfigurationException(DimmerConfiguration.ID + "' must be larget than 0");
        }

        try {
            bridge.getDeviceData(config.id);
        } catch (Exception e) {
            throw new FibaroConfigurationException(
                    "Could not get device data from the Fibaro api for id " + config.id + ". Does this id exist?", e);
        }

        setThingId(config.id);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        super.handleCommand(channelUID, command);
        try {
            String url = "http://" + bridge.getIpAddress() + "/api/devices/" + getId() + "/action/";
            if (command instanceof OnOffType) {
                url += command.equals(OnOffType.ON) ? ACTION_ON : ACTION_OFF;
                ApiResponse apiResponse = bridge.callFibaroApi(HttpMethod.POST, url, "", ApiResponse.class);
                logger.debug(apiResponse.toString());
                // TODO: Check ApiResponse for error codes
            } else if (command instanceof IncreaseDecreaseType) {
                url += command.equals(IncreaseDecreaseType.INCREASE) ? ACTION_INCREASE : ACTION_DECREASE;
                ApiResponse apiResponse = bridge.callFibaroApi(HttpMethod.POST, url, "", ApiResponse.class);
                logger.debug(apiResponse.toString());
                // TODO: Check ApiResponse for error codes
            } else if (command instanceof PercentType) {
                url += ACTION_SET_VALUE;
                int dimmerValue = ((PercentType) command).intValue();
                Arguments arguments = new Arguments();
                arguments.addArgs(dimmerValue);
                String temp = gson.toJson(arguments);
                ApiResponse apiResponse = bridge.callFibaroApi(HttpMethod.POST, url, temp, ApiResponse.class);
                logger.debug(apiResponse.toString());
                // TODO: Check ApiResponse for error codes
            } else {
                logger.debug("The dimmer handler can't handle command: " + command.toString());
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
        return getConfigAs(DimmerConfiguration.class).id;
    }

    @Override
    public void update(FibaroUpdate fibaroUpdate) {
        switch (fibaroUpdate.getProperty()) {
            case PROPERTY_SWITCH:
                updateSwitchState(fibaroUpdate.getValue());
                break;
            case PROPERTY_DEAD:
                updateDeadState(fibaroUpdate.getValue());
                break;
            case PROPERTY_ENERGY:
                updateEnergyState(fibaroUpdate.getValue());
                break;
            case PROPERTY_POWER:
                updatePowerState(fibaroUpdate.getValue());
                break;
            default:
                logger.debug("Update received for an unknown property: {}", fibaroUpdate.getProperty());
                break;
        }

        // Remove this device from the cache as it has been updated
        bridge.removeFromCache(fibaroUpdate.getId());
    }

}
