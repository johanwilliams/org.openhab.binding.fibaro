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
