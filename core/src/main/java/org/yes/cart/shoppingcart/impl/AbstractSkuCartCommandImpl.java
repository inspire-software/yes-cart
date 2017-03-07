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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * Abstract sku cart command.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractSkuCartCommandImpl extends AbstractRecalculatePriceCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSkuCartCommandImpl.class);

    /**
     * Construct abstract sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     */
    public AbstractSkuCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                      final PriceService priceService,
                                      final PricingPolicyProvider pricingPolicyProvider,
                                      final ProductService productService,
                                      final ShopService shopService) {
        super(registry, priceService, pricingPolicyProvider, productService, shopService);
    }

    /** {@inheritDoc} */
    @Override
    public final void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {

        if (parameters.containsKey(getCmdKey())) {
            final String skuCode = (String) parameters.get(getCmdKey());
            try {
                final ProductSku productSku = getProductService().getProductSkuByCode(skuCode);
                execute(shoppingCart, productSku, skuCode, parameters);
            } catch (Exception e) {
                LOG.error("Error processing command for " + skuCode + ", caused: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Abstract execute method.
     *
     * @param shoppingCart shopping cart
     * @param productSku current sku object (if exists)
     * @param skuCode actual SKU code sent
     * @param parameters all parameters
     */
    protected abstract void execute(final MutableShoppingCart shoppingCart,
                                    final ProductSku productSku,
                                    final String skuCode,
                                    final Map<String, Object> parameters);


}
