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
 * Json pojo representing the result of a call to the Fibaro API. The result is nested in other json pojos such as the
 * {@link FibaroApiResponse}
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroResult {

    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FibaroResult [result=" + result + "]";
    }

}
