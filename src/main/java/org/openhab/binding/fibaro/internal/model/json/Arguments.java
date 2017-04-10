/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.model.json;

import java.util.Arrays;

/**
 * Json pojo for arguments sent in with post requests to the Finaro api
 *
 * @author Johan Williams - Initial contribution
 */
public class Arguments {

    private String[] args;

    @Override
    public String toString() {
        return "Arguments [args=" + Arrays.toString(args) + "]";
    }

}
