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
public class PriceResolverImpl implements PriceResolver, ConfigurationRegistry<Long, PriceResolver> {

    private static final Logger LOG = LoggerFactory.getLogger(PricingPolicyProviderImpl.class);

    private final PriceResolver defaultPriceResolver;
    private final Map<Long, PriceResolver> customPriceResolvers = new HashMap<Long, PriceResolver>();

    public PriceResolverImpl(final PriceResolver defaultPriceResolver) {
        this.defaultPriceResolver = defaultPriceResolver;
    }

    /** {@inheritDoc} */
    @Override
    public SkuPrice getMinimalPrice(final Long productId, final String selectedSku, final long customerShopId, final Long masterShopId, final String currencyCode, final BigDecimal quantity, final boolean enforceTier, final String pricingPolicy) {
        return getPriceResolver(customerShopId).getMinimalPrice(productId, selectedSku, customerShopId, masterShopId, currencyCode, quantity, enforceTier, pricingPolicy);
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuPrice> getAllCurrentPrices(final Long productId, final String selectedSku, final long customerShopId, final Long masterShopId, final String currencyCode, final String pricingPolicy) {
        return getPriceResolver(customerShopId).getAllCurrentPrices(productId, selectedSku, customerShopId, masterShopId, currencyCode, pricingPolicy);
    }

    protected PriceResolver getPriceResolver(final Long shopId) {
        PriceResolver resolver = customPriceResolvers.get(shopId);
        if (resolver == null) {
            resolver = defaultPriceResolver;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    public boolean supports(final Object configuration) {
        return configuration instanceof PriceResolver;
    }

    /** {@inheritDoc} */
    public void register(final Long shopCode, final PriceResolver provider) {

        LOG.info("Custom shop settings for {} Registering pricing resolver {}", shopCode, provider.getClass());

        customPriceResolvers.put(shopCode, provider);

    }


}
