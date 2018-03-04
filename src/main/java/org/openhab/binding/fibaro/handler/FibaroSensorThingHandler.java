/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.fibaro.config.FibaroThingConfiguration;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FibaroSensorThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroSensorThingHandler extends FibaroAbstractSensorThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroSensorThingHandler.class);

    public FibaroSensorThingHandler(@NonNull Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
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

        logger.debug("Initializing the binary switch handler with id {}", id);

        if (id < 1) {
            throw new FibaroConfigurationException(FibaroThingConfiguration.ID + "' must be larget than 0");
        }

        try {
            bridge.getDeviceData(id);
        } catch (Exception e) {
            throw new FibaroConfigurationException(
                    "Could not get device data from the Fibaro api for id " + id + ". Does this id exist?", e);
        }

        reportThingIdToBridge(id);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            if (command instanceof RefreshType) {
                updateChannel(channelUID.getId(), bridge.getDeviceData(id));
            } else {
                logger.debug("Can't handle command: {}", command.toString());
            }
        } catch (Exception e) {
            logger.debug("Failed to handle command {} : {}", command.toString(), e.getMessage());
        }

    }
}
