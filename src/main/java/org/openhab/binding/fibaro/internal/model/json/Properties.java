package org.openhab.binding.fibaro.internal.model.json;

/**
 * Json pojo containing the device properties for a {@link Device}
 *
 * @author Johan Williams - Initial contribution
 */
public class Properties {

    private String zwaveCompany;
    private String zwaveInfo;
    private String configured;
    private String dead;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Properties [zwaveCompany=" + zwaveCompany + ", zwaveInfo=" + zwaveInfo + ", configured=" + configured
                + ", dead=" + dead + ", value=" + value + "]";
    }

}
