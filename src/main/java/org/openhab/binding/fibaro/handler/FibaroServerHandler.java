/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

/**
 * Handler class for the Fibaro Server
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroServerHandler extends AbstractHandler {

    // private Logger logger = LoggerFactory.getLogger(FibaroServerHandler.class);

    protected FibaroGatewayBridgeHandler fibaroBridgeHandler;
    private Gson gson;

    public FibaroServerHandler(FibaroGatewayBridgeHandler fibaroBridgeHandler) {
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
