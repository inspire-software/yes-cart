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

package org.yes.cart.utils;


import org.yes.cart.shoppingcart.ShoppingCart;

/**
 *
 * Hold current shopping cart context.
 *
 */
public final class ShoppingCartContext {

    private static final ThreadLocal<ShoppingCart> SHOPPING_CART_THREAD_LOCAL = new ThreadLocal<>();

    private ShoppingCartContext() {
        // no instance
    }

    /**
     * Get shopping cart from local thread storage.
     *
     * @return {@link ShoppingCart}
     */
    public static ShoppingCart getShoppingCart() {
        return SHOPPING_CART_THREAD_LOCAL.get();
    }

    /**
     * Set shopping cart to storage.
     *
     * @param shoppingCart current cart.
     */
    public static void setShoppingCart(final ShoppingCart shoppingCart) {
        SHOPPING_CART_THREAD_LOCAL.set(shoppingCart);
    }


    /**
     * Clear thread locals at the end of the request
     */
    public static void clear() {
        SHOPPING_CART_THREAD_LOCAL.set(null);
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        SHOPPING_CART_THREAD_LOCAL.remove();
    }

}
