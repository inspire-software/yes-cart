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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.SkuWarehouseService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:37
 */
public class SkuWarehouseServiceCachedImpl implements SkuWarehouseService {

    private final SkuWarehouseService skuWarehouseService;

    public SkuWarehouseServiceCachedImpl(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "skuWarehouseService-productSkusOnWarehouse")
    public List<SkuWarehouse> getProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return skuWarehouseService.getProductSkusOnWarehouse(productId, warehouseId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "skuWarehouseService-productOnWarehouse")
    public Map<String, BigDecimal> getProductAvailableToSellQuantity(final long productId, final Collection<Warehouse> warehouses) {
        return skuWarehouseService.getProductAvailableToSellQuantity(productId, warehouses);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "skuWarehouseService-productOnWarehouse")
    public Map<String, BigDecimal> getProductSkuAvailableToSellQuantity(final String productSku, final Collection<Warehouse> warehouses) {
        return skuWarehouseService.getProductSkuAvailableToSellQuantity(productSku, warehouses);
    }

    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     *
     * @param warehouses list of warehouses where
     * @param productSkuCode sku
     * @return pair of available and reserved quantity
     */
    public Pair<BigDecimal, BigDecimal> findQuantity(final Collection<Warehouse> warehouses, final String productSkuCode) {
        return skuWarehouseService.findQuantity(warehouses, productSkuCode);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty, final boolean allowBackorder) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty, allowBackorder);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        return skuWarehouseService.voidReservation(warehouse, productSkuCode, voidQty);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        return skuWarehouseService.credit(warehouse, productSkuCode, addQty);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {
        return skuWarehouseService.debit(warehouse, productSkuCode, debitQty);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse create(final SkuWarehouse instance) {
        return skuWarehouseService.create(instance);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse update(final SkuWarehouse instance) {
        return skuWarehouseService.update(instance);
    }

    /** {@inheritDoc} */
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return skuWarehouseService.findByWarehouseSku(warehouse, productSkuCode);
    }

    /** {@inheritDoc} */
    public List<String> findProductSkuForWhichInventoryChangedAfter(final Date lastUpdate) {
        return skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastUpdate);
    }

    /** {@inheritDoc} */
    public boolean isSkuAvailabilityPreorderOrBackorder(final String productSkuCode, final boolean checkAvailabilityDates) {
        return skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(productSkuCode, checkAvailabilityDates);
    }

    /** {@inheritDoc} */
    public List<SkuWarehouse> findAll() {
        return skuWarehouseService.findAll();
    }

    /** {@inheritDoc} */
    public SkuWarehouse findById(final long pk) {
        return skuWarehouseService.findById(pk);
    }

    /** {@inheritDoc} */
    public void delete(final SkuWarehouse instance) {
        skuWarehouseService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<SkuWarehouse> findByCriteria(final Criterion... criterion) {
        return skuWarehouseService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return skuWarehouseService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return skuWarehouseService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public SkuWarehouse findSingleByCriteria(final Criterion... criterion) {
        return skuWarehouseService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<SkuWarehouse, Long> getGenericDao() {
        return skuWarehouseService.getGenericDao();
    }
}
