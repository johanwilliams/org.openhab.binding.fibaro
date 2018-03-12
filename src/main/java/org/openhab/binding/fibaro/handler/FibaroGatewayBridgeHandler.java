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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
import org.eclipse.smarthome.core.common.ThreadPoolManager;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.fibaro.FibaroChannel;
import org.openhab.binding.fibaro.config.FibaroGatewayConfiguration;
import org.openhab.binding.fibaro.internal.FibaroHandlerFactory;
import org.openhab.binding.fibaro.internal.InMemoryCache;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroServer;
import org.openhab.binding.fibaro.internal.exception.FibaroException;
import org.openhab.binding.fibaro.internal.model.json.FibaroDevice;
import org.openhab.binding.fibaro.internal.model.json.FibaroScene;
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

    protected String fibaroIpAddress;
    protected String username;
    protected String password;
    protected int port;
    protected String ohIpAddress;
    protected String scriptName;

    // TODO Considder using ExpiringCacheAsync?
    private InMemoryCache<Integer, FibaroDevice> cache;
    private final int CACHE_EXPIRY = 10; // 10s
    private final int CACHE_SIZE = 500;

    private static final int TIMEOUT = 5;

    private List<BridgeStatusListener> listeners = new CopyOnWriteArrayList<BridgeStatusListener>();

    private static HttpClient httpClient = new HttpClient();
    private FibaroServer server;
    private final String REALM = "fibaro";
    private Gson gson;

    private Map<Integer, FibaroAbstractThingHandler> things;

    private FibaroHandlerFactory factory;

    private ScheduledFuture<?> scriptTask;
    private Map<Integer, FibaroChannel> channelRegistry = new HashMap<Integer, FibaroChannel>();
    private final ScheduledExecutorService scheduler = ThreadPoolManager.getScheduledPool("ScriptUpdater");

    public FibaroGatewayBridgeHandler(@NonNull Bridge bridge, FibaroHandlerFactory factory) {
        super(bridge);
        httpClient = new HttpClient();
        gson = new Gson();
        things = new HashMap<Integer, FibaroAbstractThingHandler>();

        this.factory = factory;

    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");
        loadConfiguration();

        cache = new InMemoryCache<Integer, FibaroDevice>(CACHE_EXPIRY, 1, CACHE_SIZE);

        try {
            validateConfiguration();

            // Populate the cache with all devices to avoid spamming the api when all things refresh
            populateDeviceCache();

            // createScene(devices);

            // Start our http server to listen for device updates
            try {
                server = new FibaroServer(port, new FibaroServerHandler(this));
            } catch (Exception e) {
                throw new FibaroException("Failed to start the server communicating with Fibaro on port " + port);
            }

            updateStatus(ThingStatus.ONLINE);
        } catch (FibaroException fe) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, fe.getMessage());
        }

    }

    private void validateConfiguration() throws FibaroException {
        if (StringUtils.trimToNull(fibaroIpAddress) == null) {
            throw new FibaroException(
                    "Parameter '" + FibaroGatewayConfiguration.IP_ADDRESS + "' is mandatory and must be configured");
        }
        if (port <= 1024 || port > 65535) {
            throw new FibaroException(
                    "Parameter '" + FibaroGatewayConfiguration.PORT + "' must be between 1025 and 65535");
        }
        if (StringUtils.trimToNull(username) == null) {
            throw new FibaroException(
                    "Parameter '" + FibaroGatewayConfiguration.USERNAME + "' is mandatory and must be configured");
        }
        if (StringUtils.trimToNull(password) == null) {
            throw new FibaroException(
                    "Parameter '" + FibaroGatewayConfiguration.PASSWORD + "' is mandatory and must be configured");
        }
        if (StringUtils.trimToNull(ohIpAddress) == null) {
            throw new FibaroException(
                    "Parameter '" + FibaroGatewayConfiguration.OH_IP_ADDRESS + "' is mandatory and must be configured");
        }
        // if (StringUtils.trimToNull(scriptName) == null) {
        // scriptName = "OHBridge";
        // }
    }

    private FibaroDevice[] populateDeviceCache() throws FibaroException {
        String devicesUrl = "http://" + getIpAddress() + "/api/devices";
        FibaroDevice[] devices = null;
        try {
            devices = callFibaroApi(HttpMethod.GET, devicesUrl, "", FibaroDevice[].class);
            for (FibaroDevice device : devices) {
                addToCache(device.getId(), device);
            }
        } catch (Exception e1) {
            throw new FibaroException("Failed to connect to the Fibaro gateway through api call '" + devicesUrl
                    + "'. Please check that username, password and ip is correctly configured.");
        }
        return devices;
    }

    private void createScene(Set<Entry<Integer, FibaroChannel>> channels) throws FibaroException {

        String scenesBaseUrl = "http://" + getIpAddress() + "/api/scenes";

        try {
            /* Create or update OH bridge scene on Fibaro */
            int sceneId = 0;

            // Get all scenes and find out if it already exists
            FibaroScene[] scenes = callFibaroApi(HttpMethod.GET, scenesBaseUrl, "", FibaroScene[].class);
            for (FibaroScene fibaroScene : scenes) {
                if (scriptName.equals(fibaroScene.getName())) {
                    sceneId = fibaroScene.getId();
                    break;
                }
            }

            if (sceneId == 0) {
                // Create new scene stub and set id
                FibaroScene newScene = callFibaroApi(HttpMethod.POST, scenesBaseUrl,
                        "{ \"name\": \"" + scriptName + "\"," + "  \"type\": \"com.fibaro.luaScene\" }",
                        FibaroScene.class);
                sceneId = newScene.getId();
            }

            // Call fibaro and set all current values in the scene
            String luaDevices = "";
            for (Entry<Integer, FibaroChannel> fibaroChannel : channels) {
                luaDevices += fibaroChannel.getKey() + " " + fibaroChannel.getValue() + "\\n";
            }

            String script = "-- Give debug a fancy color\\n" + "function log(message, color) \\n"
                    + " fibaro:debug(string.format(\\\"<span style = 'color:%s;'>%s</span>\\\", color, message)) \\n"
                    + "end\\n" + "\\n" + "-- HTTP requests\\n" + "local function request(requestUrl, deviceData) \\n"
                    + " local http = net.HTTPClient() \\n" + " \\n" + " http:request(requestUrl, { \\n"
                    + " options = {\\n" + " method = 'PUT',\\n" + " headers = {},\\n" + " data = deviceData \\n"
                    + " }, \\n" + " success = function (response) \\n"
                    + " log('OK: ' .. requestUrl .. ' - ' .. deviceData, 'green')\\n" + " end, \\n"
                    + " error = function (err) \\n"
                    + " log('FAIL: ' .. requestUrl .. ' - ' .. deviceData '. Error: ' .. err, 'red') \\n" + " end\\n"
                    + " })\\n" + "end\\n" + "\\n" + "-- MAIN\\n" + "\\n" + "-- Server settings \\n"
                    + "local openhabIp = '" + ohIpAddress + "'\\n" + "local openhabPort = 9000\\n"
                    + "local openhabUrl = 'http://' .. openhabIp .. ':' .. openhabPort\\n" + "\\n"
                    + "-- Info needed in the json request\\n" + "local trigger = fibaro:getSourceTrigger()\\n"
                    + "local deviceID = trigger['deviceID']\\n" + "local deviceName = fibaro:getName(deviceID)\\n"
                    + "local propertyName = trigger['propertyName']\\n"
                    + "local propertyValue = fibaro:getValue(deviceID, propertyName)\\n" + "\\n"
                    + "-- Assemble the json string\\n" + "jsonTable = {}\\n" + "jsonTable.id = deviceID\\n"
                    + "jsonTable.name = deviceName\\n" + "jsonTable.property = propertyName\\n"
                    + "jsonTable.value = propertyValue\\n" + "jsonString = json.encode(jsonTable)\\n" + "\\n"
                    + "log(jsonString,red)\\n" + "\\n" + "-- Send it!\\n" + "request(openhabUrl, jsonString)";

            String lua = "--[[ \\n" + "%% properties \\n" + luaDevices + "%% globals \\n" + "--]] \\n\\n" + script;

            String content = "{ \"autostart\": true, " + "\"protectedByPIN\": false, " + "\"killable\": true, "
                    + "\"killOtherInstances\": false, " + "\"maxRunningInstances\": 10, "
                    + "\"runningManualInstances\": 0, " + "\"visible\": true, " + "\"lua\": \"" + lua + "\"}";

            FibaroScene scc = callFibaroApi(HttpMethod.PUT, scenesBaseUrl + "/" + sceneId, content, FibaroScene.class);
        } catch (Exception e1) {
            throw new FibaroException("Failed to connect to the Fibaro gateway through api call '" + scenesBaseUrl
                    + "'. Please check that username, password and ip is correctly configured.");
        }
    }

    @Override
    protected void updateStatus(ThingStatus status, ThingStatusDetail detail, String comment) {
        super.updateStatus(status, detail, comment);
        logger.debug("Updating listeners with status {}", status);
        for (BridgeStatusListener listener : listeners) {
            listener.bridgeStatusChanged(status);
        }
    }

    private void loadConfiguration() {
        FibaroGatewayConfiguration config = getConfigAs(FibaroGatewayConfiguration.class);
        fibaroIpAddress = config.ipAddress;
        username = config.username;
        password = config.password;
        port = config.port;
        ohIpAddress = config.ohIpAddress;
        scriptName = config.scriptName;

        logger.debug("config ipAddress = {}", fibaroIpAddress);
        logger.debug("config id = {}", port);
        logger.debug("config id = {}", username);
        logger.debug("config id = (omitted from logging)");
        logger.debug("config id = {}", ohIpAddress);
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
        return fibaroIpAddress;
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

        if (statusCode != HttpStatus.OK_200 && statusCode != HttpStatus.CREATED_201 && statusCode != HttpStatus.ACCEPTED_202) {
            String statusLine = response.getStatus() + " " + response.getReason();
            logger.debug("Method failed: {}", statusLine);
            throw new FibaroException("Method failed: " + statusLine);
        }

        logger.debug("{}", response.getContentAsString());
        return gson.fromJson(response.getContentAsString(), result);
    }


    public void addBridgeStatusListener(BridgeStatusListener listener) {
        listeners.add(listener);
        listener.bridgeStatusChanged(getThing().getStatus());
    }

    public void removeBridgeStatusListener(BridgeStatusListener listener) {
        listeners.remove(listener);
    }

    public FibaroServer getFibaroServer(){
        return this.server;
    }

    public void childThingLinkedToChannel(int id, FibaroChannel channel){
        channelRegistry.put(Integer.valueOf(id), channel);

        if(scriptTask != null && !scriptTask.isDone()) {
            logger.debug("ScriptTask is already running. Cancelling");
            scriptTask.cancel(false);
        }

        logger.debug("Scheduling new script task to execute in 60 s");
        scriptTask = scheduler.schedule(() -> {
            try {
                logger.debug("Executing scheduled script task.");
                createScene(channelRegistry.entrySet());
                logger.debug("Script task done.");
            }catch(FibaroException fe) {

            }
        }, 15, TimeUnit.SECONDS);

    }

    public void childThingUnlinkedFromChannel(int id){
        channelRegistry.remove(Integer.valueOf(id));

        if(scriptTask != null && !scriptTask.isDone()) {
            logger.debug("ScriptTask is already running. Cancelling");
            scriptTask.cancel(false);
        }

        logger.debug("Scheduling new script task to execute in 60 s");
        scriptTask = scheduler.schedule(() -> {
            try {
                logger.debug("Executing scheduled script task.");
                createScene(channelRegistry.entrySet());
                logger.debug("Script task done.");
            }catch(FibaroException fe) {

            }
        }, 60, TimeUnit.SECONDS);
    }

}
