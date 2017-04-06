package org.openhab.binding.fibaro.config;

/**
 * Configuration class for the Fibaro bridge, used to connect to the Fibaro controller.
 *
 * @author Johan Williams - Initial contribution
 */

public class FibaroBridgeConfiguration {

    // Fibaro Bridge Thing constants
    public static final String IP_ADDRESS = "ipAddress";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

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
