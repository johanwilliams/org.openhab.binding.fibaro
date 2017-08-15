/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.model.json;

/**
 * Json pojo representing the result of a call to the Fibaro API to get the controller settings.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroSettings {

    private String serialNumber;
    private String hcName;
    private String mac;
    private String softVersion;
    private boolean beta;
    private String zwaveVersion;
    private int timeFormat;
    private String zwaveRegion;
    private int serverStatus;
    private String defaultLanguage;
    private String sunsetHour;
    private String sunriseHour;
    private boolean hotelMode;
    private boolean updateStableAvailable;
    private String temperatureUnit;
    private boolean updateBetaAvailable;
    private boolean batteryLowNotification;
    private boolean smsManagement;
    private String date;
    private boolean online;
    private String recoveryCondition;

    @Override
    public String toString() {
        return "FibaroSettings [serialNumber=" + serialNumber + ", hcName=" + hcName + ", mac=" + mac + ", softVersion="
                + softVersion + ", beta=" + beta + ", zwaveVersion=" + zwaveVersion + ", timeFormat=" + timeFormat
                + ", zwaveRegion=" + zwaveRegion + ", serverStatus=" + serverStatus + ", defaultLanguage="
                + defaultLanguage + ", sunsetHour=" + sunsetHour + ", sunriseHour=" + sunriseHour + ", hotelMode="
                + hotelMode + ", updateStableAvailable=" + updateStableAvailable + ", temperatureUnit="
                + temperatureUnit + ", updateBetaAvailable=" + updateBetaAvailable + ", batteryLowNotification="
                + batteryLowNotification + ", smsManagement=" + smsManagement + ", date=" + date + ", online=" + online
                + ", recoveryCondition=" + recoveryCondition + "]";
    }

}
