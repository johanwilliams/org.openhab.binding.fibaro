/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal;

import static org.openhab.binding.fibaro.FibaroBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.fibaro.FibaroBindingConstants;
import org.openhab.binding.fibaro.config.FibaroBridgeConfiguration;
import org.openhab.binding.fibaro.handler.BinarySwitchThingHandler;
import org.openhab.binding.fibaro.handler.FibaroBridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FibaroHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroHandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(FibaroHandlerFactory.class);

    @Override
    public Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID,
            ThingUID bridgeUID) {
        if (FibaroBindingConstants.FIBAROBRIDGE_THING_TYPE.equals(thingTypeUID)) {
            ThingUID fibaroBridgeUID = getFibaroBridgeThingUID(thingTypeUID, thingUID, configuration);
            logger.debug("createThing(): FIBARO_BRIDGE: Creating an '{}' type Thing - {}", thingTypeUID,
                    fibaroBridgeUID.getId());
            return super.createThing(thingTypeUID, configuration, fibaroBridgeUID, null);
        } else if (FibaroBindingConstants.BINARY_SWITCH_THING_TYPE.equals(thingTypeUID)) {
            ThingUID binarySwitchThingUID = getFibaroBinarySwitchUID(thingTypeUID, thingUID, configuration, bridgeUID);
            logger.debug("createThing(): BINARY_SWITCH_THING: Creating '{}' type Thing - {}", thingTypeUID,
                    binarySwitchThingUID.getId());
            return super.createThing(thingTypeUID, configuration, binarySwitchThingUID, bridgeUID);
        }

        throw new IllegalArgumentException(
                "createThing(): The thing type " + thingTypeUID + " is not supported by the Fibaro binding.");
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    /**
     * Get the Fibaro Bridge Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @return thingUID
     */
    private ThingUID getFibaroBridgeThingUID(ThingTypeUID thingTypeUID, ThingUID thingUID,
            Configuration configuration) {
        if (thingUID == null) {
            String ipAddress = (String) configuration.get(FibaroBridgeConfiguration.IP_ADDRESS);
            String bridgeID = ipAddress.replace('.', '_');
            thingUID = new ThingUID(thingTypeUID, bridgeID);
        }
        return thingUID;
    }

    /**
     * Get the Binary Switch Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @param bridgeUID
     * @return thingUID
     */
    private ThingUID getFibaroBinarySwitchUID(ThingTypeUID thingTypeUID, ThingUID thingUID, Configuration configuration,
            ThingUID bridgeUID) {
        if (thingUID == null) {
            String panelId = "binarySwitch";
            thingUID = new ThingUID(thingTypeUID, panelId, bridgeUID.getId());
        }
        return thingUID;
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(FibaroBindingConstants.FIBAROBRIDGE_THING_TYPE)) {
            FibaroBridgeHandler handler = new FibaroBridgeHandler((Bridge) thing);
            // registerFibaroDiscoveryService(handler);
            logger.debug("createHandler(): FIBAROBRIDGE_THING: ThingHandler created for {}", thingTypeUID);
            return handler;
        } else if (thingTypeUID.equals(FibaroBindingConstants.BINARY_SWITCH_THING_TYPE)) {
            logger.debug("createHandler(): BINARY_SWITCH_THING: ThingHandler created for {}", thingTypeUID);
            return new BinarySwitchThingHandler(thing);
        } else {
            logger.debug("createHandler(): ThingHandler not found for {}", thingTypeUID);
            return null;
        }
    }
}
