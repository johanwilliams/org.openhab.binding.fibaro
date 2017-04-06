/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.config;

/**
 * Configuration class for the Fibaro bridge, used to connect to the Fibaro controller.
 *
 * @author Johan Williams - Initial contribution
 */

public class BinarySwitchConfiguration {

    public static String ID = "Id";

    /**
     * The z-wave device id for this item/device on the Fibaro controller
     */
    public int id;

}
