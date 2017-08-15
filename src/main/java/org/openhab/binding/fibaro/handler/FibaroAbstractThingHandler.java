/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.FibaroChannel;
import org.openhab.binding.fibaro.config.FibaroThingConfiguration;
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

    protected int id;

    protected Gson gson;
    SimpleDateFormat formatter;

    protected List<FibaroChannel> linkedChannels;

    // Reference to the bridge which we need for communication
    protected FibaroGatewayBridgeHandler bridge = null;

    public FibaroAbstractThingHandler(@NonNull Thing thing) {
        super(thing);
    }

    /**
     * Init method that holds all common initialization stuff for Fibaro things
     *
     * @throws FibaroConfigurationException Thrown if a configuration error is encountered
     */
    protected void init() throws FibaroConfigurationException {
        loadConfiguration();
        gson = new Gson();
        linkedChannels = new ArrayList<FibaroChannel>();
        formatter = new SimpleDateFormat(DateTimeType.DATE_PATTERN_WITH_TZ_AND_MS);

        if (getBridge() == null) {
            throw new FibaroConfigurationException("This thing needs to be associated with a bridge of type: "
                    + FibaroBindingConstants.BRIDGE_ID_GATEWAY);
        }
        bridge = (FibaroGatewayBridgeHandler) getBridge().getHandler();
    }

    private void loadConfiguration() {
        FibaroThingConfiguration config = getConfigAs(FibaroThingConfiguration.class);
        id = config.id;

        logger.debug("config id = {}", id);
    }

    /**
     * Calls the bridge so it can add this thing (with our id). This so the bridge later on can find the corresponding
     * thing to update when it receives an update from the Fibaro controller for a device.
     *
     * @param id Our device id
     */
    protected void reportThingIdToBridge(int id) {
        if (bridge != null) {
            bridge.addThing(id, this);
        }
    }

    /**
     * Updates a thing channel from device data
     *
     * @param channelId Id of channel to update
     * @param device The device carrying the update information
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
                    updateChannel(channel, stringToPercent(device.getProperties().getValue()));
                    break;
                case DOOR:
                    updateChannel(channel, stringToOpenClosed(device.getProperties().getValue()));
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
                case THERMOSTAT:
                    updateChannel(channel, stringToDecimal(device.getProperties().getValue()));
                    break;
                case SMOKE:
                    updateChannel(channel, stringToOnOff(device.getProperties().getValue()));
                    break;
                case VOLTAGE:
                    updateChannel(channel, stringToDecimal(device.getProperties().getValue()));
                    break;
                case WINDOW:
                    updateChannel(channel, stringToOpenClosed(device.getProperties().getValue()));
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
    protected State stringToOnOff(String str) {
        if (str.equals("1") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("on")) {
            return OnOffType.ON;
        }
        if (str.equals("0") || str.equalsIgnoreCase("false") || str.equalsIgnoreCase("off")) {
            return OnOffType.OFF;
        }
        return UnDefType.UNDEF;
    }

    /**
     * Tries to cast a string to a {@link OpenClosedType}
     *
     * @param str String to cast
     * @return the OpenClosedType state or null if it could not be casted
     */
    protected State stringToOpenClosed(String str) {
        if (str.equals("1") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("on")) {
            return OpenClosedType.OPEN;
        }
        if (str.equals("0") || str.equalsIgnoreCase("false") || str.equalsIgnoreCase("off")) {
            return OpenClosedType.CLOSED;
        }
        return UnDefType.UNDEF;
    }

    /**
     * Tries to cast a string to a {@link PercentType}
     *
     * @param str String to cast
     * @return the PercentType state or null if it could not be casted or is not between 0 and 100
     */
    protected State stringToPercent(String str) {
        try {
            int percent = Integer.valueOf(str).intValue();
            if (percent >= 0 && percent <= 100) {
                return new PercentType(percent);
            }
        } catch (NumberFormatException nfe) {
            // Not a integer
        }
        return UnDefType.UNDEF;
    }

    /**
     * Tries to cast a string to a {@link DecimalType}
     *
     * @param str String to cast
     * @return the DecimalType state or null if it could not be casted
     */
    protected State stringToDecimal(String str) {
        try {
            double decimal = Double.valueOf(str).doubleValue();
            return new DecimalType(decimal);
        } catch (NumberFormatException nfe) {
            // Not a double
        }
        return UnDefType.UNDEF;
    }

    /**
     * Tries to cast a string to a {@link DateTimeType}
     *
     * @param str String to cast
     * @return the DateTimeType state or null if it could not be casted
     */
    protected State stringToDateTime(String timeInMs) {
        try {
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(Long.valueOf(timeInMs).longValue());
            return new DateTimeType(formatter.format(time.getTime()));
        } catch (NumberFormatException nfe) {
            // Not a double
        }
        return UnDefType.UNDEF;

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
        logger.debug("Channel {} was linked", channel.toString());
        super.channelLinked(channelUID);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        FibaroChannel channel = FibaroChannel.fromId(channelUID.getId());
        linkedChannels.remove(channel);
        logger.debug("Channel  {}  was unlinked", channel.toString());
        super.channelUnlinked(channelUID);
    }

    /**
     * Force implementing classes to add method for updates from Fibaro
     */
    protected abstract void update(FibaroUpdate fibaroUpdate);

}
