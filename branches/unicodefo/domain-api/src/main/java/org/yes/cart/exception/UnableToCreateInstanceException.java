package org.yes.cart.exception;

/**
 * Exception thrown by DtoFactory when an instance cannot be
 * created for mapped class.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:38:05 PM
 */
public class UnableToCreateInstanceException extends Exception {

    private static final long serialVersionUID = 20100122L;

    /**
     * Default constructor.
     */
    public UnableToCreateInstanceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
