/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link FibaroBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroBindingConstants {

    public static final String BINDING_ID = "fibaro";

    // List of Fibaro bridge types
    public static final String BRIDGE_ID_GATEWAY = "gateway";

    // List of Fibaro thing types
    public static final String THING_ID_ACTOR = "actor";
    public static final String THING_ID_SENSOR = "sensor";

    // List of all Bridge Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BRIDGE_GATEWAY = new ThingTypeUID(BINDING_ID, BRIDGE_ID_GATEWAY);

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACTOR = new ThingTypeUID(BINDING_ID, THING_ID_ACTOR);
    public static final ThingTypeUID THING_TYPE_SENSOR = new ThingTypeUID(BINDING_ID, THING_ID_SENSOR);

    // Set of all supported Thing Type UIDs
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE_GATEWAY,
            THING_TYPE_ACTOR, THING_TYPE_SENSOR);

    // Set of all supported Bridge Type UIDs
    public static final Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet
            .of(THING_TYPE_BRIDGE_GATEWAY);

}
