package org.openhab.binding.fibaro.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.config.FibaroBridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for a Fibaro Bridge Handler.
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroBridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroBridgeHandler.class);

    private String ipAddress;
    private String username;
    private String password;

    public FibaroBridgeHandler(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");

        FibaroBridgeConfiguration configuration = getConfigAs(FibaroBridgeConfiguration.class);

        if (configuration.ipAddress != null) {

            ipAddress = configuration.ipAddress;
            username = configuration.username;
            password = configuration.password;

            logger.debug("Fibaro Bridge Handler Initialized");
            logger.debug("   IP Address:         {},", ipAddress);
            logger.debug("   Username:           {},", username);
            logger.debug("   Password:           {},", password);

        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }

}
