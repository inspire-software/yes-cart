/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.shoppingcart.ProductQuantityStrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 01/07/2019
 * Time: 20:18
 */
public class ProductQuantityStrategyImpl
        implements ProductQuantityStrategy, ConfigurationRegistry<Long, ProductQuantityStrategy> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQuantityStrategy.class);

    private final ProductQuantityStrategy defaultQuantityStrategy;
    private final Map<Long, ProductQuantityStrategy> customQuantityStrategies = new HashMap<>();

    public ProductQuantityStrategyImpl(final ProductQuantityStrategy defaultQuantityStrategy) {
        this.defaultQuantityStrategy = defaultQuantityStrategy;
    }


    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final Product product,
                                          final String supplier) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, product, supplier);

    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSku productSku,
                                          final String supplier) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, productSku, supplier);

    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSearchResultDTO product) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, product);

    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final ProductSkuSearchResultDTO sku) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, sku);

    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final String sku,
                                          final String supplier) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, sku, supplier);

    }

    /** {@inheritDoc} */
    @Override
    public QuantityModel getQuantityModel(final long shopId,
                                          final BigDecimal cartQty,
                                          final String sku,
                                          final BigDecimal min,
                                          final BigDecimal max,
                                          final BigDecimal step,
                                          final String supplier) {

        return getProductQuantityStrategy(shopId).getQuantityModel(shopId, cartQty, sku, min, max, step, supplier);

    }

    protected ProductQuantityStrategy getProductQuantityStrategy(final Long shopId) {
        ProductQuantityStrategy resolver = customQuantityStrategies.get(shopId);
        if (resolver == null) {
            resolver = defaultQuantityStrategy;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof ProductQuantityStrategy ||
                (configuration instanceof Class && ProductQuantityStrategy.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final Long shopCode, final ProductQuantityStrategy strategy) {

        if (strategy != null) {
            LOG.debug("Custom shop settings for {} registering quantity strategy {}", shopCode, strategy.getClass());
            customQuantityStrategies.put(shopCode, strategy);
        } else {
            LOG.debug("Custom shop settings for {} registering quantity strategy DEFAULT", shopCode);
            customQuantityStrategies.remove(shopCode);
        }

    }
}
