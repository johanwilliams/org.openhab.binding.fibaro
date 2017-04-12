/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.model;

/**
 * Enum class for all actions that we might be calling on a device using the Fibaro api
 *
 * @author Johan Williams - Initial contribution
 *
 */
public enum FibaroAction {

    SET_VALUE("setValue"),
    TURN_ON("turnOn"),
    TURN_OFF("turnOff"),
    LEVEL_INCREASE("startLevelIncrease"),
    LEVEL_DECREASE("startLevelDecrease");

    private final String action;

    private FibaroAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

}
