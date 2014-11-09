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

import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartRepository;

import java.util.Map;

/**
 * Wicket version of clean command hooks into cart repository to force
 * shopping cart eviction thus destroying the cart. (This is necessary to
 * clear out cart after successful order).
 *
 * User: denispavlov
 * Date: 26/08/2014
 * Time: 22:18
 */
public class WicketCleanCartCommandImpl extends CleanCartCommandImpl {

    private static final long serialVersionUID = 20101026L;

    private final CartRepository cartRepository;

    /**
     * Wicket clean command.
     *
     * @param registry shopping cart command registry
     * @param cartRepository cart repository
     */
    public WicketCleanCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                      final CartRepository cartRepository) {
        super(registry);
        this.cartRepository = cartRepository;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            cartRepository.evictShoppingCart(shoppingCart);
            shoppingCart.clean();
            markDirty(shoppingCart);
        }
    }
}
