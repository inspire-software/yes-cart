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

import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/04/2016
 * Time: 14:07
 */
public class RecalculateSkuPricesEventCommandImpl extends AbstractRecalculatePriceCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     */
    public RecalculateSkuPricesEventCommandImpl(final ShoppingCartCommandRegistry registry,
                                                final PriceService priceService,
                                                final PricingPolicyProvider pricingPolicyProvider,
                                                final ProductService productService,
                                                final ShopService shopService) {
        super(registry, priceService, pricingPolicyProvider, productService, shopService);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_RECALCULATEPRICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {

        if (parameters.containsKey(getCmdKey())) {

            recalculatePricesInCart(shoppingCart);
            recalculate(shoppingCart);
            markDirty(shoppingCart);

        }

    }
}
