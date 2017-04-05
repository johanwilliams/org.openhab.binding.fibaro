package org.openhab.binding.fibaro.internal.server;

import java.io.IOException;
import java.net.Socket;

public interface TcpClientRequestListener {

    void processClientRequest(Socket clientSocket) throws IOException;

}
