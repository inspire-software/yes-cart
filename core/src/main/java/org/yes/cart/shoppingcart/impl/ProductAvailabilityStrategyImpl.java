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
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 11/07/2017
 * Time: 14:00
 */
public class ProductAvailabilityStrategyImpl implements ProductAvailabilityStrategy, ConfigurationRegistry<Long, ProductAvailabilityStrategy> {

    private static final Logger LOG = LoggerFactory.getLogger(PricingPolicyProviderImpl.class);

    private final ProductAvailabilityStrategy defaultAvailabilityStrategy;
    private final Map<Long, ProductAvailabilityStrategy> customAvailabilityStrategies = new HashMap<Long, ProductAvailabilityStrategy>();

    public ProductAvailabilityStrategyImpl(final ProductAvailabilityStrategy defaultAvailabilityStrategy) {
        this.defaultAvailabilityStrategy = defaultAvailabilityStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final Product product) {
        return getProductAvailabilityStrategy(shopId).getAvailabilityModel(shopId, product);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSearchResultDTO product) {
        return getProductAvailabilityStrategy(shopId).getAvailabilityModel(shopId, product);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final ProductSku sku) {
        return getProductAvailabilityStrategy(shopId).getAvailabilityModel(shopId, sku);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAvailabilityModel getAvailabilityModel(final long shopId, final String skuCode) {
        return getProductAvailabilityStrategy(shopId).getAvailabilityModel(shopId, skuCode);
    }

    protected ProductAvailabilityStrategy getProductAvailabilityStrategy(final Long shopId) {
        ProductAvailabilityStrategy resolver = customAvailabilityStrategies.get(shopId);
        if (resolver == null) {
            resolver = defaultAvailabilityStrategy;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    public boolean supports(final Object configuration) {
        return configuration instanceof ProductAvailabilityStrategy;
    }

    /** {@inheritDoc} */
    public void register(final Long shopCode, final ProductAvailabilityStrategy strategy) {

        LOG.info("Custom shop settings for {} Registering availability strategy {}", shopCode, strategy.getClass());

        customAvailabilityStrategies.put(shopCode, strategy);

    }


}
