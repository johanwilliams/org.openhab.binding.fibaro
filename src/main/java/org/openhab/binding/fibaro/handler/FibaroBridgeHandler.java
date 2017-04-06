package org.openhab.binding.fibaro.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.config.FibaroBridgeConfiguration;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroServer;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroUpdateHandler;
import org.openhab.binding.fibaro.internal.model.FibaroUpdate;
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
    private int port;
    private String username;
    private String password;

    private FibaroServer server;

    public FibaroBridgeHandler(Bridge bridge) {
        super(bridge);

        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");

        try {
            FibaroBridgeConfiguration configuration = getConfigAs(FibaroBridgeConfiguration.class);

            if (configuration.ipAddress != null) {

                ipAddress = configuration.ipAddress;
                port = configuration.port;
                username = configuration.username;
                password = configuration.password;

                logger.debug("Fibaro Bridge Handler Initialized");
                logger.debug("   IP Address:         {},", ipAddress);
                logger.debug("   Port:               {},", port);
                logger.debug("   Username:           {},", username);
                logger.debug("   Password:           {},", password);

                server = new FibaroServer(port, new FibaroUpdateHandler(this));

            }

            // TODO This should probably be moved to when we have successfully called the Ficaro api to verify it is
            // working ok
            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            logger.debug("Failed to initialize bridge " + e.getMessage());
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    public void handleFibaroUpdate(FibaroUpdate fibaroUpdate) {
        logger.debug(fibaroUpdate.toString());
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }
}
