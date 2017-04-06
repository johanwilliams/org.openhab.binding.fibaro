package org.openhab.binding.fibaro.internal.model;

public class FibaroUpdate {

    private int id;
    private String name;
    private String property;
    private String value;

    public FibaroUpdate(int id, String name, String property, String value) {
        this.id = id;
        this.name = name;
        this.property = property;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FibaroUpdate [id=" + id + ", name=" + name + ", property=" + property + ", value=" + value + "]";
    }

}
