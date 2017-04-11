/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.openhab.binding.fibaro.internal.model.json.FibaroDevice;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Abstract thing handler which implements all common functions the other thing handlers may need.
 *
 * @author Johan Williams - Initial contribution
 */
public abstract class FibaroAbstractThingHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroAbstractThingHandler.class);

    protected Gson gson;

    protected int id;

    // Reference to the bridge which we need for communication
    protected FibaroControllerBridgeHandler bridge = null;

    public FibaroAbstractThingHandler(Thing thing) {
        super(thing);
    }

    /**
     * Init method that holds all common initialization stuff for Fibaro things
     *
     * @throws FibaroConfigurationException Thrown if a configuration error is encountered
     */
    protected void init() throws FibaroConfigurationException {
        gson = new Gson();

        if (getBridge() == null) {
            throw new FibaroConfigurationException(
                    "This thing is not connected to a Fibaro bridge. Please add a Fibaro bridge and connect it in Thing settings.");
        }
        bridge = (FibaroControllerBridgeHandler) getBridge().getHandler();
    }

    protected void setThingId(int id) {
        this.id = id;
        if (bridge != null) {
            bridge.addThing(id, this);
        }
    }

    /**
     * Updates a thing channel from device data
     *
     * @param channelId Id of channel to update
     * @param device The device carrying the update information
     * @throws Exception
     */
    protected void updateChannel(String channelId, FibaroDevice device) {
        if (device == null) {
            logger.debug("Can't update channel {} as the device information is null", channelId);
        } else {
            switch (channelId) {
                case FibaroBindingConstants.CHANNEL_ID_SWITCH:
                    updateChannel(channelId, stringToOnOff(device.getProperties().getValue()));
                    break;
                case FibaroBindingConstants.CHANNEL_ID_DIMMER:
                    PercentType dimmerPercent = stringToPercent(device.getProperties().getValue());
                    if (dimmerPercent.intValue() > 0) {
                        updateChannel(channelId, OnOffType.ON);
                    }
                    updateChannel(channelId, dimmerPercent);
                    break;
                case FibaroBindingConstants.CHANNEL_ID_DEAD:
                    updateChannel(channelId, stringToOnOff(device.getProperties().getDead()));
                    break;
                case FibaroBindingConstants.CHANNEL_ID_ENERGY:
                    updateState(channelId, new DecimalType(device.getProperties().getEnergy()));
                    break;
                case FibaroBindingConstants.CHANNEL_ID_POWER:
                    updateState(channelId, new DecimalType(device.getProperties().getPower()));
                    break;
                case FibaroBindingConstants.CHANNEL_ID_TEMPERATURE:
                    updateState(channelId, stringToDecimal(device.getProperties().getValue()));
                    break;
                default:
                    logger.debug("Unknown channel: {}", channelId);
                    break;
            }
        }
    }

    /**
     * Tries to cast a string to a {@link OnOffType}
     *
     * @param str String to cast
     * @return the OnOffType state or null if it could not be casted
     */
    protected OnOffType stringToOnOff(String str) {
        if (str.equals("1") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("on")) {
            return OnOffType.ON;
        }
        if (str.equals("0") || str.equalsIgnoreCase("false") || str.equalsIgnoreCase("off")) {
            return OnOffType.OFF;
        }
        return null;
    }

    /**
     * Tries to cast a string to a {@link PercentType}
     *
     * @param str String to cast
     * @return the PercentType state or null if it could not be casted or is not between 0 and 100
     */
    protected PercentType stringToPercent(String str) {
        try {
            int percent = Integer.valueOf(str).intValue();
            if (percent >= 0 && percent <= 100) {
                return new PercentType(percent);
            }
        } catch (NumberFormatException nfe) {
            // Not a integer
        }
        return null;
    }

    /**
     * Tries to cast a string to a {@link DecimalType}
     *
     * @param str String to cast
     * @return the DecimalType state or null if it could not be casted
     */
    protected DecimalType stringToDecimal(String str) {
        try {
            double decimal = Double.valueOf(str).doubleValue();
            return new DecimalType(decimal);
        } catch (NumberFormatException nfe) {
            // Not a double
        }
        return null;
    }

    protected void updateChannel(String channelId, State state) {
        if (state != null) {
            updateState(channelId, state);
        }
    }

    /**
     * Force implementing classes to add method for updates from Fibaro
     */
    protected abstract void update(FibaroUpdate fibaroUpdate);

}
