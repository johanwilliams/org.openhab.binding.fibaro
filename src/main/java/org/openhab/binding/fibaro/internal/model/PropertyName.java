/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.model;

/**
 * Enum class for properties names in the device data that we want to read
 *
 * @author Johan Williams - Initial contribution
 *
 */
public enum PropertyName {

    VALUE("value"),
    DEAD("dead"),
    ENERGY("energy"),
    POWER("power"),
    BATTERY("batteryLevel");

    private final String name;

    private PropertyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
