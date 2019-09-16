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

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.shoppingcart.PriceResolver;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 17:04
 */
public class PriceResolverDefaultImpl implements PriceResolver, Configuration {

    private final PriceService priceService;

    private ConfigurationContext cfgContext;

    public PriceResolverDefaultImpl(final PriceService priceService) {
        this.priceService = priceService;
    }

    /** {@inheritDoc} */
    @Override
    public SkuPrice getMinimalPrice(final Long productId,
                                    final String selectedSku,
                                    final long customerShopId,
                                    final Long masterShopId,
                                    final String currencyCode,
                                    final BigDecimal quantity,
                                    final boolean enforceTier,
                                    final String pricingPolicy,
                                    final String supplier) {

        return this.priceService.getMinimalPrice(
                productId,
                selectedSku,
                customerShopId,
                masterShopId,
                currencyCode,
                quantity,
                enforceTier,
                pricingPolicy
        );
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy,
                                              final String supplier) {

        return this.priceService.getAllCurrentPrices(
                productId,
                selectedSku,
                customerShopId,
                masterShopId,
                currencyCode,
                pricingPolicy
        );
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
