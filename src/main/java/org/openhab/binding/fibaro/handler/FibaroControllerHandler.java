/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
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
import org.openhab.binding.fibaro.config.FibaroControllerConfiguration;
import org.openhab.binding.fibaro.internal.InMemoryCache;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroServer;
import org.openhab.binding.fibaro.internal.model.json.Device;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.openhab.binding.fibaro.internal.model.json.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Abstract class for a Fibaro Bridge Handler.
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroControllerHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroControllerHandler.class);

    private InMemoryCache<Integer, Device> cache;
    private final int CACHE_EXPIRY = 10; // 10s
    private final int CACHE_SIZE = 100; // 10s

    private static int TIMEOUT = 5;
    private static HttpClient httpClient = new HttpClient();
    private FibaroServer server;
    private final String REALM = "fibaro";
    private Gson gson;

    private Map<Integer, FibaroThingHandler> things;

    public FibaroControllerHandler(Bridge bridge) {
        super(bridge);
        httpClient = new HttpClient();
        gson = new Gson();
        things = new HashMap<Integer, FibaroThingHandler>();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");

        cache = new InMemoryCache<Integer, Device>(CACHE_EXPIRY, 1, CACHE_SIZE);

        FibaroControllerConfiguration config = getConfigAs(FibaroControllerConfiguration.class);

        logger.debug("config ipAddress = {}", config.ipAddress);
        logger.debug("config id = {}", config.port);
        logger.debug("config id = {}", config.username);
        logger.debug("config id = (omitted from logging)");

        boolean validConfig = true;
        String errorMsg = null;

        if (StringUtils.trimToNull(config.ipAddress) == null) {
            errorMsg = "Parameter '" + FibaroControllerConfiguration.IP_ADDRESS + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (config.port <= 1024 || config.port > 65535) {
            errorMsg = "Parameter '" + FibaroControllerConfiguration.PORT + "' must be between 1025 and 65535";
            validConfig = false;
        }
        if (StringUtils.trimToNull(config.username) == null) {
            errorMsg = "Parameter '" + FibaroControllerConfiguration.USERNAME + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (StringUtils.trimToNull(config.password) == null) {
            errorMsg = "Parameter '" + FibaroControllerConfiguration.PASSWORD + "' is mandatory and must be configured";
            validConfig = false;
        }

        // Make a call to the controller API
        String url = "http://" + getIpAddress() + "/api/settings/info";
        try {
            Settings settings = callFibaroApi(HttpMethod.GET, url, "", Settings.class);
            logger.debug("Successfully connected to the Fibaro controller " + settings.toString());
        } catch (Exception e1) {
            errorMsg = "Failed to connect to the Fibaro controller through api call '" + url
                    + "'. Please check that username, password and ip is correctly configured.";
            validConfig = false;
        }

        // Start our http server to listen for device updates
        try {
            server = new FibaroServer(config.port, new FibaroServerHandler(this));
        } catch (Exception e) {
            errorMsg = "Failed to start the server communicating with Fibaro on port " + config.port;
            validConfig = false;
        }

        if (validConfig) {
            // TODO: startAutomaticRefresh();
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMsg);
        }
    }

    public void handleFibaroUpdate(FibaroUpdate fibaroUpdate) {
        logger.debug(fibaroUpdate.toString());
        FibaroThingHandler fibaroThingHandler = things.get(fibaroUpdate.getId());
        if (fibaroThingHandler == null) {
            logger.debug("No thing with id " + fibaroUpdate.getId() + " is configured");
        } else {
            fibaroThingHandler.update(fibaroUpdate);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
    }

    public void addThing(int id, FibaroThingHandler fibaroThingHandler) {
        things.put(id, fibaroThingHandler);
    }

    public void removeThing(int id) {
        things.remove(id);
    }

    public FibaroThingHandler getThing(int id) {
        return things.get(id);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing the Fibaro Bridge handler.");
        try {
            server.stop();
        } catch (Exception e) {
            logger.debug("Error stopping Fibaro update server " + e.getMessage());
        }
    }

    @Override
    public <T> T getConfigAs(Class<T> configurationClass) {
        return getConfig().as(configurationClass);
    }

    public String getIpAddress() {
        return getConfigAs(FibaroControllerConfiguration.class).ipAddress;
    }

    public Device getDeviceData(int id) throws Exception {
        Device device = cache.get(id);
        if (device == null) {
            String url = "http://" + getIpAddress() + "/api/devices/" + id;
            device = callFibaroApi(HttpMethod.GET, url, "", Device.class);
            addToCache(id, device);
        }
        return device;
    }

    public void addToCache(int id, Device device) {
        cache.put(id, device);
    }

    public void removeFromCache(int id) {
        cache.remove(id);
    }

    /**
     * Calls the Finaro API and returns a pojo of type passed in as result parameter
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
        FibaroControllerConfiguration config = getConfigAs(FibaroControllerConfiguration.class);

        // Add authentication credentials
        AuthenticationStore auth = httpClient.getAuthenticationStore();
        URI uri = new URI(url);
        auth.addAuthentication(new BasicAuthentication(uri, REALM, config.username, config.password));

        // @formatter:off
        ContentResponse response = httpClient.newRequest(uri)
                .method(method)
                .content(new StringContentProvider(content))
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .send();

        int statusCode = response.getStatus();

        if (statusCode != HttpStatus.OK_200 && statusCode != HttpStatus.ACCEPTED_202) {
            String statusLine = response.getStatus() + " " + response.getReason();
            logger.error("Method failed: {}", statusLine);
            throw new Exception("Method failed: " + statusLine);
        }

        logger.debug(response.getContentAsString());
        return gson.fromJson(response.getContentAsString(), result);
    }


}
