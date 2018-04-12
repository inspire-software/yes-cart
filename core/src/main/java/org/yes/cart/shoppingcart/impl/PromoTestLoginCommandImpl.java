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

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.PriceResolver;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

/**
 * User: denispavlov
 * Date: 10/04/2018
 * Time: 19:09
 */
public class PromoTestLoginCommandImpl extends LoginCommandImpl {

    private final CustomerService customerService;

    /**
     * Construct command.
     *
     * @param registry              shopping cart command registry
     * @param customerService       customer service
     * @param shopService           shop service
     * @param priceResolver         price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService        product service
     */
    public PromoTestLoginCommandImpl(final ShoppingCartCommandRegistry registry,
                                     final CustomerService customerService,
                                     final ShopService shopService,
                                     final PriceResolver priceResolver,
                                     final PricingPolicyProvider pricingPolicyProvider,
                                     final ProductService productService) {
        super(registry, customerService, shopService, priceResolver, pricingPolicyProvider, productService);
        this.customerService = customerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return "promoTestLoginCmd";
    }


    /**
     * Authenticates automatically if customer exists in shop.
     *
     * @param username username
     * @param shop     shop
     * @param password password
     *
     * @return true if customer exists
     */
    @Override
    protected boolean authenticate(final String username, final Shop shop, final String password) {
        return customerService.isCustomerExists(username, shop);
    }
}
