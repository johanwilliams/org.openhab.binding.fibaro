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
 * Json pojo containing the device properties for a {@link FibaroDevice}
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroProperties {

    private String zwaveCompany;
    private String zwaveInfo;
    private String configured;
    private String dead;
    private double energy;
    private double power;
    private String value;
    private int batteryLevel;

    public String getZwaveCompany() {
        return zwaveCompany;
    }

    public void setZwaveCompany(String zwaveCompany) {
        this.zwaveCompany = zwaveCompany;
    }

    public String getZwaveInfo() {
        return zwaveInfo;
    }

    public void setZwaveInfo(String zwaveInfo) {
        this.zwaveInfo = zwaveInfo;
    }

    public String getConfigured() {
        return configured;
    }

    public void setConfigured(String configured) {
        this.configured = configured;
    }

    public String getDead() {
        return dead;
    }

    public void setDead(String dead) {
        this.dead = dead;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public String toString() {
        return "FibaroProperties [zwaveCompany=" + zwaveCompany + ", zwaveInfo=" + zwaveInfo + ", configured="
                + configured + ", dead=" + dead + ", energy=" + energy + ", power=" + power + ", value=" + value
                + ", batteryLevel=" + batteryLevel + "]";
    }
}
