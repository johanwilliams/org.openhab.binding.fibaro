/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.exception;

import java.io.IOException;

/**
 * Exception if something happens in the communication to Fibaro.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroException extends IOException {

    private static final long serialVersionUID = 7046320281639980528L;

    public FibaroException(String message) {
        super(message);
    }

    public FibaroException(Throwable ex) {
        super(ex);
    }

    public FibaroException(String message, Throwable cause) {
        super(message, cause);
    }

}
