package org.yes.cart.service.payment;

import java.util.Map;

/**
 *
 * Responsible to handle call back call from payment gateway with external form processing.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface PaymentCallBackHandlerFacade {


    /**
     * Handle call back from payment gateway
     * @param parameters map with http parameters
     * @param paymentGatewayLabel particular payment gateway label
     */
    void handlePaymentCallback(Map parameters, final String paymentGatewayLabel);

}
