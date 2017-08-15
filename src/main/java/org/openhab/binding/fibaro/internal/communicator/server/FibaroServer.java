/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.communicator.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

/**
 * Embedded jetty server which is used to listen for device updates. The http client is a lua scene in the Fibaro
 * controller.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroServer {

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
