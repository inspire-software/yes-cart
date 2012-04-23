package org.yes.cart.service.order;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 4/21/12
 * Time: 8:42 PM
 */
public class OrderException extends Exception {



    /**
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause
     */
    public OrderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public OrderException(final String message) {
        super(message);
    }


}
