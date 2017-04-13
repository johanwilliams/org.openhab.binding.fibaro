/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.fibaro.FibaroChannel;
import org.openhab.binding.fibaro.config.FibaroThingConfiguration;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.openhab.binding.fibaro.internal.model.PropertyName;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FibaroSensorThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroSensorThingHandler extends FibaroAbstractThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroSensorThingHandler.class);

    public FibaroSensorThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            init();
            updateStatus(ThingStatus.ONLINE);
        } catch (FibaroConfigurationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void init() throws FibaroConfigurationException {
        super.init();

        FibaroThingConfiguration config = getConfigAs(FibaroThingConfiguration.class);
        logger.debug("Initializing the binary switch handler with id {}", config.id);

        if (config.id < 1) {
            throw new FibaroConfigurationException(FibaroThingConfiguration.ID + "' must be larget than 0");
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
        try {
            if (command instanceof RefreshType) {
                updateChannel(channelUID.getId(), bridge.getDeviceData(id));
            } else {
                logger.debug("Can't handle command: " + command.toString());
            }
        } catch (Exception e) {
            logger.debug("Failed to handle command " + command.toString() + " : " + e.getMessage());
        }

    }

    @Override
    public void update(FibaroUpdate fibaroUpdate) {
        PropertyName property = PropertyName.fromName(fibaroUpdate.getProperty());
        switch (property) {
            case BATTERY:
                updateChannel(FibaroChannel.BATTERY, stringToDecimal(fibaroUpdate.getValue()));
            case DEAD:
                updateChannel(FibaroChannel.DEAD, stringToOnOff(fibaroUpdate.getValue()));
                break;
            case VALUE:
                updateChannel(FibaroChannel.TEMPERATURE, stringToDecimal(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.DOOR, stringToOnOff(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.ELECTRIC_CURRENT, stringToDecimal(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.HEAT, stringToOnOff(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.SMOKE, stringToOnOff(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.WINDOW, stringToOnOff(fibaroUpdate.getValue()));
                updateChannel(FibaroChannel.ILLUMINANCE, stringToDecimal(fibaroUpdate.getValue()));
                break;
            default:
                logger.debug("Update received for an unknown property: {}", fibaroUpdate.getProperty());
                break;
        }

        // Remove this device from the cache as it has been updated
        bridge.removeFromCache(fibaroUpdate.getId());
    }

    /**
     * Returns the configured id
     *
     * @return Thing id
     */
    public int getId() {
        return getConfigAs(FibaroThingConfiguration.class).id;
    }

}
