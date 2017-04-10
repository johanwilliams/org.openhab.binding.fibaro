/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
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
 * The {@link FibaroBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroBindingConstants {

    public static final String BINDING_ID = "fibaro";

    // List of Fibaro bridge types
    public static final String BRIDGE_ID_FIBARO = "fibaroBridge";

    // List of Fibaro thing types
    public static final String THING_ID_BINARY_SWITCH = "binarySwitch";
    public static final String THING_ID_DIMMER = "dimmer";

    // List of all Bridge Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_BRIDGE_FIBARO = new ThingTypeUID(BINDING_ID, BRIDGE_ID_FIBARO);

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_BINARY_SWITCH = new ThingTypeUID(BINDING_ID, THING_ID_BINARY_SWITCH);
    public final static ThingTypeUID THING_TYPE_DIMMER = new ThingTypeUID(BINDING_ID, THING_ID_DIMMER);

    // List of all Channel ids
    public final static String CHANNEL_ID_SWITCH = "switch";
    public final static String CHANNEL_ID_DIMMER = "dimmer";
    public final static String CHANNEL_ID_DEAD = "dead";
    public final static String CHANNEL_ID_ENERGY = "energy";
    public final static String CHANNEL_ID_POWER = "power";

    // Set of all supported Thing Type UIDs
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE_FIBARO,
            THING_TYPE_BINARY_SWITCH, THING_TYPE_DIMMER);

    // Set of all supported Bridge Type UIDs
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_BRIDGE_FIBARO);

}
