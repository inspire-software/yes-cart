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

package org.yes.cart.shoppingcart.support.tokendriven;

import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:02
 */
public interface ShoppingCartStateSerializer {

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
