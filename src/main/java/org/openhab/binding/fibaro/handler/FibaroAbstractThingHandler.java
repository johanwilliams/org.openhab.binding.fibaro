/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.fibaro.FibaroChannel;
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
    SimpleDateFormat formatter;

    protected int id;
    protected List<FibaroChannel> linkedChannels;

    // Reference to the bridge which we need for communication
    protected FibaroGatewayBridgeHandler bridge = null;

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
        linkedChannels = new ArrayList<FibaroChannel>();
        formatter = new SimpleDateFormat(DateTimeType.DATE_PATTERN_WITH_TZ_AND_MS);

        if (getBridge() == null) {
            throw new FibaroConfigurationException(
                    "This thing is not connected to a Fibaro bridge. Please add a Fibaro bridge and connect it in Thing settings.");
        }
        bridge = (FibaroGatewayBridgeHandler) getBridge().getHandler();
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
            FibaroChannel channel = FibaroChannel.fromId(channelId);
            switch (channel) {
                case ALARM:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case BATTERY:
                    updateChannel(channel, new DecimalType(device.getProperties().getBatteryLevel()));
                    break;
                case DEAD:
                    updateChannel(channel, stringToOnOff(device.getProperties().getDead()));
                    break;
                case DIMMER:
                    PercentType dimmerPercent = stringToPercent(device.getProperties().getValue());
                    if (dimmerPercent.intValue() > 0) {
                        updateChannel(channel, OnOffType.ON);
                    }
                    updateChannel(channel, dimmerPercent);
                    break;
                case DOOR:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case ELECTRIC_CURRENT:
                    updateChannel(channel, stringToDecimal(device.getProperties().getValue()));
                    break;
                case ENERGY:
                    updateChannel(channel, new DecimalType(device.getProperties().getEnergy()));
                    break;
                case HEAT:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case ILLUMINANCE:
                    updateChannel(channel, stringToDecimal(device.getProperties().getValue()));
                    break;
                case MOTION:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case POWER:
                    updateChannel(channel, new DecimalType(device.getProperties().getPower()));
                    break;
                case POWER_OUTLET:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case SWITCH:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case TEMPERATURE:
                    updateChannel(channel, stringToDecimal(device.getProperties().getValue()));
                    break;
                case SMOKE:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case WINDOW:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
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

    /**
     * Tries to cast a string to a {@link DateTimeType}
     *
     * @param str String to cast
     * @return the DateTimeType state or null if it could not be casted
     */
    protected DateTimeType stringToDateTime(String timeInMs) {
        try {
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(Long.valueOf(timeInMs).longValue());
            return new DateTimeType(formatter.format(time.getTime()));
        } catch (NumberFormatException nfe) {
            // Not a double
        }
        return null;

    }

    /**
     * Tries to update the specified channel with the specified state. Will however check that the state is not null and
     * that the channel is linked (in use)
     *
     * @param channel Channel to update
     * @param state State to update the channel with
     */
    protected void updateChannel(FibaroChannel channel, State state) {
        if (state != null && linkedChannels.contains(channel)) {
            updateState(channel.getId(), state);
        }
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        FibaroChannel channel = FibaroChannel.fromId(channelUID.getId());
        linkedChannels.add(channel);
        logger.debug("Channel " + channel.toString() + " was linked");
        super.channelLinked(channelUID);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        FibaroChannel channel = FibaroChannel.fromId(channelUID.getId());
        linkedChannels.remove(channel);
        logger.debug("Channel " + channel.toString() + " was unlinked");
        super.channelUnlinked(channelUID);
    }

    /**
     * Force implementing classes to add method for updates from Fibaro
     */
    protected abstract void update(FibaroUpdate fibaroUpdate);

}
