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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 13:16
 */
public abstract class AbstractCartCommandImpl implements ShoppingCartCommand {


    /**
     * Recalculate shopping cart.
     *
     * @param shoppingCart current cart
     */
    protected void recalculate(final ShoppingCart shoppingCart) {
        shoppingCart.recalculate();
    }

    /**
     * Mark shopping cart dirty and thus eligible for persistence.
     *
     * @param shoppingCart current cart
     */
    protected void markDirty(final ShoppingCart shoppingCart) {
        shoppingCart.markDirty();
    }
}
