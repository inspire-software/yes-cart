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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

/**
 * Extends default login command to include wicket specific authentication flow.
 *
 * User: denispavlov
 * Date: 26/08/2014
 * Time: 21:07
 */
public class RESTLoginCommandImpl extends LoginCommandImpl {

    private static final long serialVersionUID = 20101026L;

    private final CartRepository cartRepository;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerService customer service
     * @param shopService shop service
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param cartRepository cart repository
     */
    public RESTLoginCommandImpl(final ShoppingCartCommandRegistry registry,
                                final CustomerService customerService,
                                final ShopService shopService,
                                final PriceService priceService,
                                final PricingPolicyProvider pricingPolicyProvider,
                                final ProductService productService,
                                final CartRepository cartRepository) {
        super(registry, customerService, shopService, priceService, pricingPolicyProvider, productService);
        this.cartRepository = cartRepository;
    }

    /**
     * Merges and recalculates the cart.
     */
    @Override
    protected void recalculate(final MutableShoppingCart shoppingCart) {

        // This call will merge the cart
        cartRepository.storeShoppingCart(shoppingCart);

        super.recalculate(shoppingCart);

    }
}
