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
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.shoppingcart.InventoryResolver;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 11/07/2017
 * Time: 14:16
 */
public class InventoryResolverImpl
        implements InventoryResolver, ConfigurationRegistry<String, InventoryResolver>, Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryResolverImpl.class);

    private final InventoryResolver defaultInventoryResolver;
    private final Map<String, InventoryResolver> customInventoryResolvers = new HashMap<>();

    private ConfigurationContext cfgContext;

    public InventoryResolverImpl(final InventoryResolver defaultInventoryResolver) {
        this.defaultInventoryResolver = defaultInventoryResolver;
    }


    /** {@inheritDoc} */
    @Override
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {
        return getInventoryResolver(warehouse.getCode()).reservation(warehouse, productSkuCode, reserveQty);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty, final boolean allowBackorder) {
        return getInventoryResolver(warehouse.getCode()).reservation(warehouse, productSkuCode, reserveQty, allowBackorder);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        return getInventoryResolver(warehouse.getCode()).voidReservation(warehouse, productSkuCode, voidQty);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {
        return getInventoryResolver(warehouse.getCode()).debit(warehouse, productSkuCode, debitQty);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        return getInventoryResolver(warehouse.getCode()).credit(warehouse, productSkuCode, addQty);
    }

    /** {@inheritDoc} */
    @Override
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return getInventoryResolver(warehouse.getCode()).findByWarehouseSku(warehouse, productSkuCode);
    }


    protected InventoryResolver getInventoryResolver(final String warehouseCode) {
        InventoryResolver resolver = customInventoryResolvers.get(warehouseCode);
        if (resolver == null) {
            resolver = defaultInventoryResolver;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final Object configuration) {
        return configuration instanceof InventoryResolver ||
                (configuration instanceof Class && InventoryResolver.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final String warehouseCode, final InventoryResolver provider) {

        if (provider != null) {
            LOG.debug("Custom fulfilment centre settings for {} registering inventory resolver {}", warehouseCode, provider.getClass());
            customInventoryResolvers.put(warehouseCode, provider);
        } else {
            LOG.debug("Custom fulfilment centre settings for {} registering inventory resolver DEFAULT", warehouseCode);
            customInventoryResolvers.remove(warehouseCode);
        }

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
