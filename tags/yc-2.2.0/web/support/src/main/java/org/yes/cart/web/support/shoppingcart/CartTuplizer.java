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

package org.yes.cart.web.support.shoppingcart;

import org.yes.cart.shoppingcart.ShoppingCart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 22:39
 */
public interface CartTuplizer extends Serializable {

    /**
     * Check if this tuplizer supports this request
     *
     * @param httpServletRequest request
     *
     * @return true if cart can be extracted
     */
    boolean supports(HttpServletRequest httpServletRequest);

    /**
     * Extract object from request tuple.
     *
     * @param httpServletRequest request
     *
     * @return cart object
     *
     * @throws CartDetuplizationException in case detuplization fails
     */
    ShoppingCart detuplize(HttpServletRequest httpServletRequest) throws CartDetuplizationException;

    /**
     * Package shopping cart into a tuple to put into response.
     *
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @param shoppingCart shopping cart
     *
     * @throws CartTuplizationException in case was unable to create tuple
     */
    void tuplize(HttpServletRequest httpServletRequest,
                 HttpServletResponse httpServletResponse,
                 ShoppingCart shoppingCart) throws CartTuplizationException;

}
