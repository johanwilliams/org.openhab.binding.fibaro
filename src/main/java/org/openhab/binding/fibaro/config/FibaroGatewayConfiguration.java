/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.config;

/**
 * Configuration class for the Fibaro controller, used to connect to the Fibaro controller.
 *
 * @author Johan Williams - Initial contribution
 */

public class FibaroGatewayConfiguration {

    public static final String IP_ADDRESS = "Ip address";
    public static final String PORT = "Port";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

    /**
     * The IP address of the Fibaro controller
     */
    public String ipAddress;

    /**
     * The port Fibaro uses to communicate with
     */
    public int port;

    /**
     * The admin username of the Fibaro controller
     */
    public String username;

    /**
     * The admin password of the Fibaro controller
     */
    public String password;

}
