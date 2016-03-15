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

package org.yes.cart.shoppingcart.support;

import org.yes.cart.shoppingcart.ShoppingCart;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 22:39
 */
public interface CartTuplizer<S, T> extends Serializable {

    /**
     * Check if this tuplizer supports this request
     *
     * @param source request
     *
     * @return true if cart can be extracted
     */
    boolean supports(S source);

    /**
     * Extract object from request tuple.
     *
     * @param source request
     *
     * @return cart object
     *
     * @throws CartDetuplizationException in case detuplization fails
     */
    ShoppingCart detuplize(S source) throws CartDetuplizationException;

    /**
     * Package shopping cart into a tuple to put into response.
     *
     * @param source request
     * @param target response
     * @param shoppingCart shopping cart
     *
     * @throws CartTuplizationException in case was unable to create tuple
     */
    void tuplize(S source,
                 T target,
                 ShoppingCart shoppingCart) throws CartTuplizationException;

}
