package org.openhab.binding.fibaro.internal.communicator.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibaroServer {

    private Logger logger = LoggerFactory.getLogger(FibaroServer.class);

    protected Server server;

    public FibaroServer(int serverPort, Handler handler) throws Exception {
        server = new Server(serverPort);
        server.setHandler(handler);
        start();
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

}
