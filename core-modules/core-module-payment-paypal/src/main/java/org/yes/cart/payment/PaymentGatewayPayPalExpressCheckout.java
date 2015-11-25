/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.payment.dto.Payment;

import java.io.IOException;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/11/2015
 * Time: 17:58
 */
public interface PaymentGatewayPayPalExpressCheckout extends PaymentGatewayExternalForm {


    /**
     * Support for pp express checkout. In case if gateway not support this operation , return will be empty hashmap
     *
     * @param payment       payment
     * @param verify        verification token for callback
     *
     * @return redirect URL
     */
    String setExpressCheckoutMethod(Payment payment, String verify);


    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token the token obtained via   SetExpressCheckout method
     * @param payment       payment
     *
     * @return map of parsed key - values with detail information
     */
    Map<String, String> doExpressCheckoutPayment(Payment payment, String token) throws IOException;

}
