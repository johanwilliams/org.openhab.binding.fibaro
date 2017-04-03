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
