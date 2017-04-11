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
import org.openhab.binding.fibaro.config.FibaroControllerConfiguration;
import org.openhab.binding.fibaro.handler.BinarySwitchThingHandler;
import org.openhab.binding.fibaro.handler.DimmerThingHandler;
import org.openhab.binding.fibaro.handler.FibaroControllerHandler;
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
        if (FibaroBindingConstants.THING_TYPE_BRIDGE_CONTROLLER.equals(thingTypeUID)) {
            ThingUID fibaroControllerUID = getFibaroControllerThingUID(thingTypeUID, thingUID, configuration);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}",
                    FibaroBindingConstants.BRIDGE_ID_CONTROLLER, thingTypeUID, fibaroControllerUID.getId());
            return super.createThing(thingTypeUID, configuration, fibaroControllerUID, null);
        } else if (FibaroBindingConstants.THING_TYPE_BINARY_SWITCH.equals(thingTypeUID)) {
            ThingUID binarySwitchThingUID = getFibaroBinarySwitchUID(thingTypeUID, thingUID, configuration, bridgeUID);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}",
                    FibaroBindingConstants.THING_ID_BINARY_SWITCH, thingTypeUID, binarySwitchThingUID.getId());
            return super.createThing(thingTypeUID, configuration, binarySwitchThingUID, bridgeUID);
        } else if (FibaroBindingConstants.THING_TYPE_DIMMER.equals(thingTypeUID)) {
            ThingUID dimmerThingUID = getFibaroDimmerUID(thingTypeUID, thingUID, configuration, bridgeUID);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}", FibaroBindingConstants.THING_ID_DIMMER,
                    thingTypeUID, dimmerThingUID.getId());
            return super.createThing(thingTypeUID, configuration, dimmerThingUID, bridgeUID);
        }

        throw new IllegalArgumentException(
                "createThing(): The thing type " + thingTypeUID + " is not supported by the Fibaro binding.");
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    /**
     * Get the Fibaro Controller Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @return thingUID
     */
    private ThingUID getFibaroControllerThingUID(ThingTypeUID thingTypeUID, ThingUID thingUID,
            Configuration configuration) {
        if (thingUID == null) {
            String ipAddress = (String) configuration.get(FibaroControllerConfiguration.IP_ADDRESS);
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
            thingUID = new ThingUID(thingTypeUID, FibaroBindingConstants.THING_ID_BINARY_SWITCH, bridgeUID.getId());
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
    private ThingUID getFibaroDimmerUID(ThingTypeUID thingTypeUID, ThingUID thingUID, Configuration configuration,
            ThingUID bridgeUID) {
        if (thingUID == null) {
            thingUID = new ThingUID(thingTypeUID, FibaroBindingConstants.THING_ID_DIMMER, bridgeUID.getId());
        }
        return thingUID;
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_BRIDGE_CONTROLLER)) {
            FibaroControllerHandler handler = new FibaroControllerHandler((Bridge) thing);
            // registerFibaroDiscoveryService(handler);
            logger.debug("createHandler(): {}: ThingHandler created for {}",
                    FibaroBindingConstants.BRIDGE_ID_CONTROLLER, thingTypeUID);
            return handler;
        } else if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_BINARY_SWITCH)) {
            logger.debug("createHandler(): {}: ThingHandler created for {}",
                    FibaroBindingConstants.THING_ID_BINARY_SWITCH, thingTypeUID);
            return new BinarySwitchThingHandler(thing);
        } else if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_DIMMER)) {
            logger.debug("createHandler(): {}: ThingHandler created for {}", FibaroBindingConstants.THING_ID_DIMMER,
                    thingTypeUID);
            return new DimmerThingHandler(thing);
        } else {
            logger.debug("createHandler(): ThingHandler not found for {}", thingTypeUID);
            return null;
        }
    }
}
