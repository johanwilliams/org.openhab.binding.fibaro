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
 * Json pojo containing the device information returned from the Fibaro API.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroDevice {

    private int id;
    private String name;
    private int roomID;
    private String type;
    private String baseType;
    private boolean enabled;
    private int parentId;
    private FibaroProperties properties;
    private int created;
    private int modified;

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

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public FibaroProperties getProperties() {
        return properties;
    }

    public void setProperties(FibaroProperties properties) {
        this.properties = properties;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getModified() {
        return modified;
    }

    public void setModified(int modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "FibaroDevice [id=" + id + ", name=" + name + ", roomID=" + roomID + ", type=" + type + ", baseType="
                + baseType + ", enabled=" + enabled + ", parentId=" + parentId + ", properties=" + properties
                + ", created=" + created + ", modified=" + modified + "]";
    }

}
