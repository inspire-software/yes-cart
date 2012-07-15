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

package org.yes.cart.service.payment;

import org.yes.cart.service.order.OrderException;
import org.yes.cart.shoppingcart.ShoppingCart;

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
     * @param shoppingCart  the shopping cart.
     * @throws OrderException in case if payment was failed
     * @return true in case of succesfull payment
     */
    boolean pay(final ShoppingCart shoppingCart, final Map paymentParameter) throws OrderException;


}
