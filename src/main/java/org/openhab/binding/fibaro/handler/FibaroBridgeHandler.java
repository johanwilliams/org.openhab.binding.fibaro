package org.openhab.binding.fibaro.handler;

import java.net.URI;
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
import org.openhab.binding.fibaro.config.FibaroBridgeConfiguration;
import org.openhab.binding.fibaro.internal.communicator.server.FibaroServer;
import org.openhab.binding.fibaro.internal.model.json.FibaroUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Abstract class for a Fibaro Bridge Handler.
 *
 * @author Johan Williams - Initial Contribution
 */
public class FibaroBridgeHandler extends BaseBridgeHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroBridgeHandler.class);

    private static int TIMEOUT = 5;
    private static HttpClient httpClient = new HttpClient();
    private FibaroServer server;
    private Gson gson;

    public FibaroBridgeHandler(Bridge bridge) {
        super(bridge);
        httpClient = new HttpClient();
        gson = new Gson();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing the Fibaro Bridge handler.");

        FibaroBridgeConfiguration config = getConfigAs(FibaroBridgeConfiguration.class);

        logger.debug("config ipAddress = {}", config.ipAddress);
        logger.debug("config id = {}", config.port);
        logger.debug("config id = {}", config.username);
        logger.debug("config id = (omitted from logging)");

        boolean validConfig = true;
        String errorMsg = null;

        if (StringUtils.trimToNull(config.ipAddress) == null) {
            errorMsg = "Parameter '" + FibaroBridgeConfiguration.IP_ADDRESS + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (config.port <= 1024 || config.port > 65535) {
            errorMsg = "Parameter '" + FibaroBridgeConfiguration.PORT + "' must be between 1025 and 65535";
            validConfig = false;
        }
        if (StringUtils.trimToNull(config.username) == null) {
            errorMsg = "Parameter '" + FibaroBridgeConfiguration.USERNAME + "' is mandatory and must be configured";
            validConfig = false;
        }
        if (StringUtils.trimToNull(config.password) == null) {
            errorMsg = "Parameter '" + FibaroBridgeConfiguration.PASSWORD + "' is mandatory and must be configured";
            validConfig = false;
        }
        // TODO: Make a call to the api to 1. Verify connectivity, ip, username/password and 2. Fetch properties that we
        // might want to keep in the bridge config

        try {
            server = new FibaroServer(config.port, new FibaroUpdateHandler(this));
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
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
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

    /**
     * Simple logic to perform a post request
     *
     * @param url
     * @param timeout
     * @return
     */
    public synchronized <T> T callFibaroApi(HttpMethod method, String url, String content, Class<T> result)
            throws Exception {
        if (!httpClient.isStarted()) {
            httpClient.start();
        }

        URI uri = new URI(url);
        String realm = "fibaro";
        String user = "admin";
        String pass = "admin";

        // Add authentication credentials
        AuthenticationStore auth = httpClient.getAuthenticationStore();
        auth.addAuthentication(new BasicAuthentication(uri, realm, user, pass));

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
