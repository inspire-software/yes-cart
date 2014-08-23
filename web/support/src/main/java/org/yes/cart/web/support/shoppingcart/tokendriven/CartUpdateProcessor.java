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

package org.yes.cart.web.support.shoppingcart.tokendriven;

import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 18:04
 */
public interface CartUpdateProcessor {

    /**
     * Perform all necessary actions (in single transaction) in order to persist the
     * latest valid state of the cart.
     *
     * Note that the cart parameter will be updated with (potentially) merged result
     * when user login is detected (i.e. current cart is LOGGED_IN but ShoppingCartState
     * with corresponding GUID had no CustomerEmail)
     *
     * @param shoppingCart cart to merge (if required) and persist
     */
    void updateShoppingCart(ShoppingCart shoppingCart);

    /**
     * Restore shopping cart from bytes as is.
     *
     * @param bytes bytes to restore cart from
     *
     * @return shopping cart
     */
    ShoppingCart restoreState(byte[] bytes);

    /**
     * Package shopping cart as is into bytes.
     *
     * @param shoppingCart shopping cart
     *
     * @return bytes representation.
     */
    byte[] saveState(ShoppingCart shoppingCart);

}
