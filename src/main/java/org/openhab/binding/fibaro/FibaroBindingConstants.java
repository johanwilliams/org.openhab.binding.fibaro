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

    // List of Fibaro bridge device types
    public static final String FIBARO_BRIDGE = "fibaroBridge";

    // List of FIbaro device types
    public static final String BINARY_SWITCH = "binarySwitch";

    // List of all Bridge Thing Type UIDs
    public final static ThingTypeUID FIBAROBRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, FIBARO_BRIDGE);

    // List of all Thing Type UIDs
    public final static ThingTypeUID BINARY_SWITCH_THING_TYPE = new ThingTypeUID(BINDING_ID, BINARY_SWITCH);

    // List of all Channel ids
    public final static String SWITCH = "switch";
    public final static String DEAD = "dead";
    public final static String ENERGY = "energy";
    public final static String POWER = "power";

    // Set of all supported Thing Type UIDs
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(FIBAROBRIDGE_THING_TYPE,
            BINARY_SWITCH_THING_TYPE);

    // Set of all supported Bridge Type UIDs
    public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet.of(FIBAROBRIDGE_THING_TYPE);

}
