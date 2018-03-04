package org.openhab.binding.fibaro.handler;

import java.util.List;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibaroDoorSensorThingHandler extends FibaroAbstractSensorThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroDoorSensorThingHandler.class);

    public FibaroDoorSensorThingHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        try {
            init();
            updateStatus(ThingStatus.ONLINE);
        } catch (FibaroConfigurationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void init() throws FibaroConfigurationException {
        super.init();

        // TODO Figure out what Thing type and add default channels here maybe?
        Thing thing = this.thing;

        if (!getBridge().getThings().contains(thing)) {
            List<Channel> channels = thing.getChannels();
            logger.debug(channels.toString());
        }

        reportThingIdToBridge(id);

    }
}
