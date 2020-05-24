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

package org.yes.cart.service.domain.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 19:06
 */
public class WarehouseServiceCachedImpl implements WarehouseService {

    private final WarehouseService warehouseService;

    public WarehouseServiceCachedImpl(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> findByShopId(final long shopId, final boolean includeDisabled) {
        return warehouseService.findByShopId(shopId, includeDisabled);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-shopWarehouses")
    public List<Warehouse> getByShopId(final long shopId, final boolean includeDisabled) {
        return warehouseService.getByShopId(shopId, includeDisabled);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-shopWarehousesMap")
    public Map<String, Warehouse> getByShopIdMapped(final long shopId, final boolean includeDisabled) {
        return warehouseService.getByShopIdMapped(shopId, includeDisabled);
    }



    /** {@inheritDoc} */
    @Override
    public void updateShopWarehouseRank(final long shopWarehouseId, final int newRank) {
        warehouseService.updateShopWarehouseRank(shopWarehouseId, newRank);
    }

    /** {@inheritDoc} */
    @Override
    public ShopWarehouse findShopWarehouseById(final long shopWarehouseId) {
        return warehouseService.findShopWarehouseById(shopWarehouseId);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void assignWarehouse(final long warehouseId, final long shopId, final boolean soft) {
        warehouseService.assignWarehouse(warehouseId, shopId, soft);
    }


    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void unassignWarehouse(final long warehouseId, final long shopId, final boolean soft) {
        warehouseService.unassignWarehouse(warehouseId, shopId, soft);
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> findWarehouses(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return warehouseService.findWarehouses(start, offset, sort, sortDescending, filter);
    }

    /** {@inheritDoc} */
    @Override
    public int findWarehouseCount(final Map<String, List> filter) {
        return warehouseService.findWarehouseCount(filter);
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> findAll() {
        return warehouseService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Warehouse> callback) {
        warehouseService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Warehouse> callback) {
        warehouseService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public Warehouse findById(final long pk) {
        return warehouseService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public Warehouse create(final Warehouse instance) {
        return warehouseService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public Warehouse update(final Warehouse instance) {
        return warehouseService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void delete(final Warehouse instance) {
        warehouseService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<Warehouse> findByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Warehouse, Long> getGenericDao() {
        return warehouseService.getGenericDao();
    }

    /** {@inheritDoc} */
    @Override
    public Warehouse findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findSingleByCriteria(eCriteria, parameters);
    }
}
