package org.yes.cart.exception;

/**
 * Exception thrown by  DtoFactory when an unmapped object is requested.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:36:29 PM
 */
public class UnmappedInterfaceException extends Exception {

    private static final long serialVersionUID = 20100122L;

    /**
     * Default constructor.
     */
    public UnmappedInterfaceException(final String message) {
        super(message);
    }
}
