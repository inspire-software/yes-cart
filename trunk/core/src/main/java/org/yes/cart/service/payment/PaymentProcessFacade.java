package org.yes.cart.service.payment;

import java.util.Map;

/**
 *
 * This facade hide interaction with order and payment
 * services during payment.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:08
 */
public interface PaymentProcessFacade {


    /**
     * Perform order payment.
     * @param paymentParameter the payment parameters.
     * @return {@link PaymentResult} in cae of succesfull payment
     * @throws PaymentFailedException in case of payment failure.
     */
    PaymentResult pay(final Map paymentParameter) throws PaymentFailedException;


}
