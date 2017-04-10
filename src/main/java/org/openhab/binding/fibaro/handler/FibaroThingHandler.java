/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.openhab.binding.fibaro.internal.model.json.Device;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Abstract thing handler which implements all common functions the onterh thing handler need.
 *
 * @author Johan Williams - Initial contribution
 */
public abstract class FibaroThingHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroThingHandler.class);

    protected Gson gson;

    protected int id;

    // Reference to the bridge which we need for communication
    protected FibaroBridgeHandler bridge = null;

    public FibaroThingHandler(Thing thing) {
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
        bridge = (FibaroBridgeHandler) getBridge().getHandler();
    }

    protected void setThingId(int id) {
        this.id = id;
        if (bridge != null) {
            bridge.addThing(id, this);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            try {
                updateChannel(channelUID.getId(), bridge.getDeviceData(id));
            } catch (Exception e) {
                logger.debug("Could not refresh data for device id {}", e.getMessage());
            }
        }
    }

    /**
     * Updates a thing channel from device data
     *
     * @param channelId Id of channel to update
     * @param device The device carrying the update information
     * @throws Exception
     */
    protected void updateChannel(String channelId, Device device) throws Exception {
        if (device == null) {
            logger.debug("Can't update channel {} as the device information is null", channelId);
        } else {
            switch (channelId) {
                case FibaroBindingConstants.CHANNEL_ID_SWITCH:
                    updateState(channelId,
                            device.getProperties().getValue().equals("true") ? OnOffType.ON : OnOffType.OFF);
                    break;
                case FibaroBindingConstants.CHANNEL_ID_DEAD:
                    updateState(channelId,
                            device.getProperties().getDead().equals("true") ? OnOffType.ON : OnOffType.OFF);
                    break;
                case FibaroBindingConstants.CHANNEL_ID_ENERGY:
                    updateState(channelId, new DecimalType(device.getProperties().getEnergy()));
                    break;
                case FibaroBindingConstants.CHANNEL_ID_POWER:
                    updateState(channelId, new DecimalType(device.getProperties().getPower()));
                    break;
                default:
                    logger.debug("Unknown channel: {}", channelId);
                    break;
            }
        }
    }

    /**
     * Updates the Dead channel state with the specified value
     *
     * @param value State value
     */
    protected void updateDeadState(String value) {
        if (value.equals("true")) {
            updateState(FibaroBindingConstants.CHANNEL_ID_DEAD, OnOffType.ON);
        } else if (value.equals("false")) {
            updateState(FibaroBindingConstants.CHANNEL_ID_DEAD, OnOffType.OFF);
        }
    }

    /**
     * Updates the Switch state with the specified value
     *
     * @param value State value
     */
    protected void updateSwitchState(String value) {
        if (value.equals("1")) {
            updateState(FibaroBindingConstants.CHANNEL_ID_SWITCH, OnOffType.ON);
        } else if (value.equals("0")) {
            updateState(FibaroBindingConstants.CHANNEL_ID_SWITCH, OnOffType.OFF);
        }
    }

    /**
     * Updates the Energy state with the specified value
     *
     * @param value State value
     */
    protected void updateEnergyState(String value) {
        if (StringUtils.isNumeric(value)) {
            updateState(FibaroBindingConstants.CHANNEL_ID_ENERGY, new DecimalType(value));
        }
    }

    /**
     * Updates the Power state with the specified value
     *
     * @param value State value
     */
    protected void updatePowerState(String value) {
        if (StringUtils.isNumeric(value)) {
            updateState(FibaroBindingConstants.CHANNEL_ID_POWER, new DecimalType(value));
        }
    }

    /**
     * Force implementing classes to add method for updates from Fibaro
     */
    protected abstract void update(FibaroUpdate fibaroUpdate);

}
