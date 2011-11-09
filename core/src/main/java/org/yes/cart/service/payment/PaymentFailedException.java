package org.yes.cart.service.payment;

/**
 *
 * Payment failure exception.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:11
 */
public class PaymentFailedException extends Exception {


    /**
     *
     * Create payment failed exception.
     *
     * @param message message
     * @param cause real cause
     */
    public PaymentFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
