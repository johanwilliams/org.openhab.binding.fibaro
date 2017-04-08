/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.fibaro.internal.exception;

/**
 * Exception for invalid user and password.
 *
 * @author Johan Williams - Initial contribution
 */
public class FibaroUnauthorizedException extends FibaroException {

    private static final long serialVersionUID = 1231114236506945196L;

    public FibaroUnauthorizedException(Throwable ex) {
        super(ex);
    }

}
