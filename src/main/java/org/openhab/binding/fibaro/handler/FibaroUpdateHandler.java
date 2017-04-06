package org.openhab.binding.fibaro.handler;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;

import com.google.gson.Gson;

public class FibaroUpdateHandler extends AbstractHandler {

    // private Logger logger = LoggerFactory.getLogger(FibaroUpdateHandler.class);

    protected FibaroBridgeHandler fibaroBridgeHandler;
    private Gson gson;

    public FibaroUpdateHandler(FibaroBridgeHandler fibaroBridgeHandler) {
        super();
        this.fibaroBridgeHandler = fibaroBridgeHandler;
        gson = new Gson();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        BufferedReader reader = request.getReader();

        FibaroUpdate fibaroUpdate = gson.fromJson(reader, FibaroUpdate.class);
        fibaroBridgeHandler.handleFibaroUpdate(fibaroUpdate);

        response.setStatus(HttpServletResponse.SC_OK);

        baseRequest.setHandled(true);
    }

}
