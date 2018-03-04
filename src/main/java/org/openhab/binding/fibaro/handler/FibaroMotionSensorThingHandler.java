package org.openhab.binding.fibaro.handler;

import java.util.List;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.fibaro.internal.exception.FibaroConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibaroMotionSensorThingHandler extends FibaroAbstractSensorThingHandler {

    private Logger logger = LoggerFactory.getLogger(FibaroMotionSensorThingHandler.class);

    public FibaroMotionSensorThingHandler(Thing thing) {
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
        ThingUID uid = thing.getUID();

        Bridge b = getBridge();
        List<Thing> t = b.getThings();

        boolean contains = false;
        for (Thing thing2 : t) {
            if (uid.equals(thing2.getUID())) {
                contains = true;
                break;
            }
        }

        if (!contains) {
            List<Channel> channels = thing.getChannels();
            logger.debug(channels.toString());
        }
        reportThingIdToBridge(id);
    }
}
