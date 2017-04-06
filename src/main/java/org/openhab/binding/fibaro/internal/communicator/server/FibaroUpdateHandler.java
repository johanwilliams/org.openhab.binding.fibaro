package org.openhab.binding.fibaro.internal.communicator.server;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.openhab.binding.fibaro.handler.FibaroBridgeHandler;
import org.openhab.binding.fibaro.internal.model.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class FibaroUpdateHandler extends AbstractHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroUpdateHandler.class);

    protected FibaroBridgeHandler fibaroBridgeHandler;

    public FibaroUpdateHandler(FibaroBridgeHandler fibaroBridgeHandler) {
        super();
        this.fibaroBridgeHandler = fibaroBridgeHandler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        BufferedReader reader = request.getReader();
        Gson gson = new Gson();

        FibaroUpdate fibaroUpdate = gson.fromJson(reader, FibaroUpdate.class);
        fibaroBridgeHandler.handleFibaroUpdate(fibaroUpdate);

        response.setStatus(HttpServletResponse.SC_OK);

        baseRequest.setHandled(true);
    }

}
