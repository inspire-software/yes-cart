/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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
     * Get submit button, that can be different for different eternal gateways,
     * because of regulation policy about button view.
     * @return html code for submit button if it supported by gateway, otherwise null, so the default submit button has to be used.
     */
    String getSubmitButton();

    /**
     * Restore order number by given parameters
     *
     * @param privateCallBackParameters request parameters
     * @return restore order number
     */
    String restoreOrderGuid(Map privateCallBackParameters);

    /**
     * Support for pp express checkout. In case if gateway not support this operation , return will be empty hashmap
     *
     * @param amount       amount
     * @param currencyCode currency
     * @return map with auth token
     * @throws java.io.IOException in case of errors
     */
    Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode)
            throws IOException;
    
    
    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token the token obtained via   SetExpressCheckout method
     * @param payerId the token obtained via   GetExpressCheckoutDetails method
     * @param amount the amount
     * @param currencyCode currency
     * @return map of parsed key - values with detail information
     * @throws java.io.IOException in case of errors
     */
    Map<String, String> doDoExpressCheckoutPayment(String token, String payerId, BigDecimal amount, String currencyCode)
            throws IOException;


    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token the token obtained via   SetExpressCheckout method
     * @return map of parsed key - values with detail information
     * @throws java.io.IOException in case of errors
     */
    Map<String, String> getExpressCheckoutDetails(String token)
            throws IOException;

    /**
     * Check the result for success attributes.
     * @param nvpCallResult  call result
     * @return  true in case of success
     */
    boolean isSuccess(Map<String, String> nvpCallResult);


    /**
     * Handle raw request. Call to this method will be delegated from particular filter if
     * gateway support several commands / notification via single call back filter. Like google checkout.
     * @param request http request
     * @param response http responce.
     */
    void handleNotification(final HttpServletRequest request, final HttpServletResponse response);

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
