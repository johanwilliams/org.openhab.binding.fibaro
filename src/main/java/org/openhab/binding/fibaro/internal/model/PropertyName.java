/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
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

    BATTERY("batteryLevel"),
    DEAD("dead"),
    ENERGY("energy"),
    POWER("power"),
    VALUE("value");

    private final String name;

    private PropertyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PropertyName fromName(String name) {
        for (PropertyName property : PropertyName.values()) {
            if (property.name.equalsIgnoreCase(name)) {
                return property;
            }
        }
        throw new IllegalArgumentException("No property with name " + name + " found");
    }

}
