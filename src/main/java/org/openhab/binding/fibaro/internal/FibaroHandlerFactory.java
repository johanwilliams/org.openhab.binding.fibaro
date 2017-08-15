/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
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
import org.openhab.binding.fibaro.config.FibaroGatewayConfiguration;
import org.openhab.binding.fibaro.handler.FibaroActorThingHandler;
import org.openhab.binding.fibaro.handler.FibaroGatewayBridgeHandler;
import org.openhab.binding.fibaro.handler.FibaroSensorThingHandler;
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
        if (FibaroBindingConstants.THING_TYPE_BRIDGE_GATEWAY.equals(thingTypeUID)) {
            ThingUID fibaroGatewayUID = getFibaroGatewayThingUID(thingTypeUID, thingUID, configuration);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}",
                    FibaroBindingConstants.BRIDGE_ID_GATEWAY, thingTypeUID, fibaroGatewayUID.getId());
            return super.createThing(thingTypeUID, configuration, fibaroGatewayUID, null);
        } else if (FibaroBindingConstants.THING_TYPE_ACTOR.equals(thingTypeUID)) {
            ThingUID fibaroActorThingUID = getFibaroActorUID(thingTypeUID, thingUID, configuration, bridgeUID);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}", FibaroBindingConstants.THING_ID_ACTOR,
                    thingTypeUID, fibaroActorThingUID.getId());
            return super.createThing(thingTypeUID, configuration, fibaroActorThingUID, bridgeUID);
        } else if (FibaroBindingConstants.THING_TYPE_SENSOR.equals(thingTypeUID)) {
            ThingUID fibaroSensorThingUID = getFibaroSensorUID(thingTypeUID, thingUID, configuration, bridgeUID);
            logger.debug("createThing(): {}: Creating an '{}' type Thing - {}", FibaroBindingConstants.THING_ID_ACTOR,
                    thingTypeUID, fibaroSensorThingUID.getId());
            return super.createThing(thingTypeUID, configuration, fibaroSensorThingUID, bridgeUID);
        }
        throw new IllegalArgumentException(
                "createThing(): The thing type " + thingTypeUID + " is not supported by the Fibaro binding.");
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    /**
     * Get the Fibaro Gateway Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @return thingUID
     */
    private ThingUID getFibaroGatewayThingUID(ThingTypeUID thingTypeUID, ThingUID thingUID,
            Configuration configuration) {
        if (thingUID == null) {
            String ipAddress = (String) configuration.get(FibaroGatewayConfiguration.IP_ADDRESS);
            String bridgeID = ipAddress.replace('.', '_');
            thingUID = new ThingUID(thingTypeUID, bridgeID);
        }
        return thingUID;
    }

    /**
     * Get the Actor Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @param bridgeUID
     * @return thingUID
     */
    private ThingUID getFibaroActorUID(ThingTypeUID thingTypeUID, ThingUID thingUID, Configuration configuration,
            ThingUID bridgeUID) {
        if (thingUID == null) {
            thingUID = new ThingUID(thingTypeUID, FibaroBindingConstants.THING_ID_ACTOR, bridgeUID.getId());
        }
        return thingUID;
    }

    /**
     * Get the Sensor Thing UID.
     *
     * @param thingTypeUID
     * @param thingUID
     * @param configuration
     * @param bridgeUID
     * @return thingUID
     */
    private ThingUID getFibaroSensorUID(ThingTypeUID thingTypeUID, ThingUID thingUID, Configuration configuration,
            ThingUID bridgeUID) {
        if (thingUID == null) {
            thingUID = new ThingUID(thingTypeUID, FibaroBindingConstants.THING_ID_SENSOR, bridgeUID.getId());
        }
        return thingUID;
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_BRIDGE_GATEWAY)) {
            FibaroGatewayBridgeHandler handler = new FibaroGatewayBridgeHandler((Bridge) thing);
            // registerFibaroDiscoveryService(handler);
            logger.debug("createHandler(): {}: ThingHandler created for {}", FibaroBindingConstants.BRIDGE_ID_GATEWAY,
                    thingTypeUID);
            return handler;
        } else if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_ACTOR)) {
            logger.debug("createHandler(): {}: ThingHandler created for {}", FibaroBindingConstants.THING_ID_ACTOR,
                    thingTypeUID);
            return new FibaroActorThingHandler(thing);
        } else if (thingTypeUID.equals(FibaroBindingConstants.THING_TYPE_SENSOR)) {
            logger.debug("createHandler(): {}: ThingHandler created for {}", FibaroBindingConstants.THING_ID_SENSOR,
                    thingTypeUID);
            return new FibaroSensorThingHandler(thing);
        } else {
            logger.debug("createHandler(): ThingHandler not found for {}", thingTypeUID);
            return null;
        }
    }
}
