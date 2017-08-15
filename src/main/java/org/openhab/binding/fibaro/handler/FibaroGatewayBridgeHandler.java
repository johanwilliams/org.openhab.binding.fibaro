/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.handler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.config.FibaroGatewayConfiguration;
import org.openhab.binding.fibaro.internal.InMemoryCache;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroServer;
import org.openhab.binding.fibaro.internal.exception.FibaroException;
import org.openhab.binding.fibaro.internal.model.json.FibaroDevice;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link FibaroGatewayThingHandler} is responsible for the communication between this binding and the Fibaro
 * gateway.
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroGatewayBridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroGatewayBridgeHandler.class);

    protected String ipAddress;
    protected String username;
    protected String password;
    protected int port;

    private InMemoryCache<Integer, FibaroDevice> cache;
    private final int CACHE_EXPIRY = 10; // 10s
    private final int CACHE_SIZE = 500;

    private static final int TIMEOUT = 5;
    private static HttpClient httpClient = new HttpClient();
    private FibaroServer server;
    private final String REALM = "fibaro";
    private Gson gson;

    private Map<Integer, FibaroAbstractThingHandler> things;

    public FibaroGatewayBridgeHandler(@NonNull Bridge bridge) {
        super(bridge);
        httpClient = new HttpClient();
        gson = new Gson();
        things = new HashMap<Integer, FibaroAbstractThingHandler>();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");
        loadConfiguration();

        cache = new InMemoryCache<Integer, FibaroDevice>(CACHE_EXPIRY, 1, CACHE_SIZE);

        boolean validConfig = true;
        String errorMsg = null;

        if (StringUtils.trimToNull(ipAddress) == null) {
            errorMsg = "Parameter '" + FibaroGatewayConfiguration.IP_ADDRESS + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (port <= 1024 || port > 65535) {
            errorMsg = "Parameter '" + FibaroGatewayConfiguration.PORT + "' must be between 1025 and 65535";
            validConfig = false;
        }
        if (StringUtils.trimToNull(username) == null) {
            errorMsg = "Parameter '" + FibaroGatewayConfiguration.USERNAME + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (StringUtils.trimToNull(password) == null) {
            errorMsg = "Parameter '" + FibaroGatewayConfiguration.PASSWORD + "' is mandatory and must be configured";
            validConfig = false;
        }

        // Populate the cache with all devices to avoid spamming the api when all things refresh
        String url = "http://" + getIpAddress() + "/api/devices";
        try {
            FibaroDevice[] devices = callFibaroApi(HttpMethod.GET, url, "", FibaroDevice[].class);
            for (FibaroDevice device : devices) {
                addToCache(device.getId(), device);
            }
        } catch (Exception e1) {
            errorMsg = "Failed to connect to the Fibaro gateway through api call '" + url
                    + "'. Please check that username, password and ip is correctly configured.";
            validConfig = false;
        }

        // Start our http server to listen for device updates
        try {
            server = new FibaroServer(port, new FibaroServerHandler(this));
        } catch (Exception e) {
            errorMsg = "Failed to start the server communicating with Fibaro on port " + port;
            validConfig = false;
        }

        if (validConfig) {
            // TODO: startAutomaticRefresh();
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMsg);
        }
    }

    private void loadConfiguration() {
        FibaroGatewayConfiguration config = getConfigAs(FibaroGatewayConfiguration.class);
        ipAddress = config.ipAddress;
        username = config.username;
        password = config.password;
        port = config.port;

        logger.debug("config ipAddress = {}", ipAddress);
        logger.debug("config id = {}", port);
        logger.debug("config id = {}", username);
        logger.debug("config id = (omitted from logging)");
    }

    public void handleFibaroUpdate(FibaroUpdate fibaroUpdate) {
        logger.debug("{}", fibaroUpdate.toString());
        FibaroAbstractThingHandler fibaroThingHandler = things.get(fibaroUpdate.getId());
        if (fibaroThingHandler == null) {
            logger.debug("No thing with id {} is configured", fibaroUpdate.getId());
        } else {
            fibaroThingHandler.update(fibaroUpdate);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
    }

    public void addThing(int id, FibaroAbstractThingHandler fibaroThingHandler) {
        things.put(id, fibaroThingHandler);
    }

    public void removeThing(int id) {
        things.remove(id);
    }

    public FibaroAbstractThingHandler getThing(int id) {
        return things.get(id);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing the Fibaro Bridge handler.");
        try {
            server.stop();
        } catch (Exception e) {
            logger.debug("Error stopping Fibaro update server {}", e.getMessage());
        }
    }

    @Override
    public <T> T getConfigAs(Class<T> configurationClass) {
        return getConfig().as(configurationClass);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public FibaroDevice getDeviceData(int id) throws Exception {
        FibaroDevice device = cache.get(id);
        if (device == null) {
            String url = "http://" + getIpAddress() + "/api/devices/" + id;
            device = callFibaroApi(HttpMethod.GET, url, "", FibaroDevice.class);
            addToCache(id, device);
        }
        return device;
    }

    public void addToCache(int id, FibaroDevice device) {
        cache.put(id, device);
    }

    public void removeFromCache(int id) {
        cache.remove(id);
    }

    /**
     * Calls the Fibaro API and returns a pojo of type passed in as result parameter
     *
     * @param method The http method to send the request with
     * @param url Url to the api
     * @param content The data sent with the request (if any)
     * @param result The json pojo to parse the response into (using gson)
     * @return json pojo holding the response data
     * @throws Exception
     */
    public synchronized <T> T callFibaroApi(HttpMethod method, String url, String content, Class<T> result)
            throws Exception {
        if (!httpClient.isStarted()) {
            httpClient.start();
        }
        logger.debug("Calling the Fibaro api on url: {} with content: {}", url, content);

        // Add authentication credentials
        AuthenticationStore auth = httpClient.getAuthenticationStore();
        URI uri = new URI(url);
        auth.addAuthentication(new BasicAuthentication(uri, REALM, username, password));

        // @formatter:off
        ContentResponse response = httpClient.newRequest(uri)
                .method(method)
                .content(new StringContentProvider(content))
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .send();

        int statusCode = response.getStatus();

        if (statusCode != HttpStatus.OK_200 && statusCode != HttpStatus.ACCEPTED_202) {
            String statusLine = response.getStatus() + " " + response.getReason();
            logger.debug("Method failed: {}", statusLine);
            throw new FibaroException("Method failed: " + statusLine);
        }

        logger.debug("{}", response.getContentAsString());
        return gson.fromJson(response.getContentAsString(), result);
    }


}
