package org.openhab.binding.fibaro.internal.communicator.server;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.openhab.binding.fibaro.internal.model.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class FibaroServer extends AbstractHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroServer.class);

    protected Server server;
    protected int serverPort;

    public FibaroServer(int serverPort) {
        this.serverPort = serverPort;
        server = new Server(serverPort);
        server.setHandler(this);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        FibaroUpdate fibaroUpdate = gson.fromJson(reader, FibaroUpdate.class);
        logger.debug(fibaroUpdate.toString());

        // Declare response encoding and types
        response.setContentType("text/html; charset=utf-8");

        // Declare response status code
        response.setStatus(HttpServletResponse.SC_OK);

        // Write back response
        response.getWriter().println("<h1>Hello World</h1>");

        // Inform jetty that this request has now been handled
        baseRequest.setHandled(true);
    }

    public void startServer() {
        try {
            server.start();
            server.join();
            logger.debug("Server started, listening to port" + serverPort);
        } catch (Exception e) {
            logger.debug("Could not start server " + e.getMessage());
        }
    }

    public void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.debug("Could not stop server " + e.getMessage());
        }
    }

}
