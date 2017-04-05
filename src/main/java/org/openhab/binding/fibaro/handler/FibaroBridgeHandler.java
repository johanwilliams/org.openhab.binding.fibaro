package org.openhab.binding.fibaro.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.config.FibaroBridgeConfiguration;
import org.openhab.binding.fibaro.internal.server.TcpClientRequestListener;
import org.openhab.binding.fibaro.internal.server.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for a Fibaro Bridge Handler.
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroBridgeHandler extends BaseBridgeHandler implements TcpClientRequestListener {

    private Logger logger = LoggerFactory.getLogger(FibaroBridgeHandler.class);

    private String ipAddress;
    private String username;
    private String password;

    private TcpServer server;

    public FibaroBridgeHandler(Bridge bridge) {
        super(bridge);

        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");

        FibaroBridgeConfiguration configuration = getConfigAs(FibaroBridgeConfiguration.class);

        // TODO Move the port to bridge configuration
        server = new TcpServer(this, 9000);

        if (configuration.ipAddress != null) {

            ipAddress = configuration.ipAddress;
            username = configuration.username;
            password = configuration.password;

            logger.debug("Fibaro Bridge Handler Initialized");
            logger.debug("   IP Address:         {},", ipAddress);
            logger.debug("   Username:           {},", username);
            logger.debug("   Password:           {},", password);

            new Thread(server).start();

            // TODO This should probably be moved to when we have successfully called the Ficaro api to verify it is
            // working ok
            updateStatus(ThingStatus.ONLINE);

        }
    }

    @Override
    public void processClientRequest(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();
        long time = System.currentTimeMillis();

        output.write(
                ("HTTP/1.1 200 OK\n\n<html><body>" + "Singlethreaded Server: " + time + "</body></html>").getBytes());
        output.close();
        input.close();
        logger.debug("Request processed: " + time);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }
}
