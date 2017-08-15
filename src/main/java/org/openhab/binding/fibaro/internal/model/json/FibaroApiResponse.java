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
 * Json pojo for the response message from the Fibao API
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroApiResponse {

    private String id;
    private String jsonrpc;
    private FibaroResult result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public FibaroResult getResult() {
        return result;
    }

    public void setResult(FibaroResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FibaroApiResponse [id=" + id + ", jsonrpc=" + jsonrpc + ", result=" + result + "]";
    }

}
