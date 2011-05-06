package org.yes.cart.payment;

import java.util.Map;

/**
 * Payment gateway interface with external form processing. I.e. form action of post operation located on the
 * URL, that belong to particular payment gateway.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayExternalForm extends PaymentGateway {

    /**
     * Get action url for <form action="postActionUrl">
     *
     * @return url for post action.
     */
    String getPostActionUrl();

    /**
     * Restore order number by given parameters
     *
     * @param privateCallBackParameters request parameters
     * @return restore order number
     */
    String restoreOrderGuid(Map privateCallBackParameters);

    /**
     * Process public call back request from payment gateway.
     *
     * Warning !!! Implementation of this method MUST NOT perform any order/payment state modification.
     * I.e. do not use this  as successfult payment determination method
     *
     * @param publicCallBackParameters get/post parameters
     * @return true in case in payment was ok, false in case if payment failed
     */
    // boolean processPublicResponce(Map publicCallBackParameters);


}
