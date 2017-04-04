package org.openhab.binding.fibaro.handler;

import static org.openhab.binding.fibaro.FibaroBindingConstants.SWITCH;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BinarySwitchThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Johan Williams - Initial contribution
 */
public class BinarySwitchThingHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(BinarySwitchThingHandler.class);

    private static int TIMEOUT = 5;
    private static HttpClient httpClient = new HttpClient();

    public BinarySwitchThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(SWITCH)) {
            try {
                if (command.equals(OnOffType.ON)) {
                    post("http://192.168.1.4/api/devices/31/action/turnOn", "");
                } else if (command.equals(OnOffType.OFF)) {
                    post("http://192.168.1.4/api/devices/31/action/turnOff", "");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }

    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    /**
     * Simple logic to perform a post request
     *
     * @param url
     * @param timeout
     * @return
     */
    private String post(String url, String postData) throws Exception {
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
                .method(HttpMethod.POST)
                .content(new StringContentProvider(postData))
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .send();

        int statusCode = response.getStatus();

        if (statusCode != HttpStatus.OK_200 && statusCode != HttpStatus.ACCEPTED_202) {
            String statusLine = response.getStatus() + " " + response.getReason();
            logger.error("Method failed: {}", statusLine);
            throw new Exception("Method failed: " + statusLine);
        }

        return response.getContentAsString();
    }


}
