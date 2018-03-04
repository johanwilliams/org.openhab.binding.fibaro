package org.openhab.binding.fibaro.handler;

import org.eclipse.smarthome.core.thing.ThingStatus;

public interface BridgeStatusListener {
    public void bridgeStatusChanged(ThingStatus status);
}
