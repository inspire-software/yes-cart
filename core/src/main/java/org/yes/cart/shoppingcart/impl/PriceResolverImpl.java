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
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.shoppingcart.PriceResolver;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 17:04
 */
public class PriceResolverImpl
        implements PriceResolver, ConfigurationRegistry<Long, PriceResolver> {

    private static final Logger LOG = LoggerFactory.getLogger(PriceResolverImpl.class);

    private final PriceResolver defaultPriceResolver;
    private final Map<Long, PriceResolver> customPriceResolvers = new HashMap<>();

    public PriceResolverImpl(final PriceResolver defaultPriceResolver) {
        this.defaultPriceResolver = defaultPriceResolver;
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

        return getPriceResolver(customerShopId).getMinimalPrice(
                productId,
                selectedSku,
                customerShopId,
                masterShopId,
                currencyCode,
                quantity,
                enforceTier,
                pricingPolicy,
                supplier
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

        return getPriceResolver(customerShopId).getAllCurrentPrices(
                productId,
                selectedSku,
                customerShopId,
                masterShopId,
                currencyCode,
                pricingPolicy,
                supplier
        );
    }

    protected PriceResolver getPriceResolver(final Long shopId) {
        PriceResolver resolver = customPriceResolvers.get(shopId);
        if (resolver == null) {
            resolver = defaultPriceResolver;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof PriceResolver ||
                (configuration instanceof Class && PriceResolver.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final Long shopCode, final PriceResolver provider) {

        if (provider != null) {
            LOG.debug("Custom shop settings for {} registering pricing resolver {}", shopCode, provider.getClass());
            customPriceResolvers.put(shopCode, provider);
        } else {
            LOG.debug("Custom shop settings for {} registering pricing resolver DEFAULT", shopCode);
            customPriceResolvers.remove(shopCode);
        }

    }
}
