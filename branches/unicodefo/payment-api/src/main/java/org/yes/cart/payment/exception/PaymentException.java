package org.yes.cart.payment.exception;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:21:53
 */
public class PaymentException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public PaymentException(final String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public PaymentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
