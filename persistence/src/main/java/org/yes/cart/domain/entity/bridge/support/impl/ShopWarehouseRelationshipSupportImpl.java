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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.bridge.support.ShopWarehouseRelationshipSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-16
 * Time: 5:06 PM
 */
public class ShopWarehouseRelationshipSupportImpl implements ShopWarehouseRelationshipSupport {


    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<Warehouse, Long> warehouseDao;
    private final GenericDAO<ShopWarehouse, Long> shopWarehouseDao;

    public ShopWarehouseRelationshipSupportImpl(final GenericDAO<Shop, Long> shopDao,
                                                final GenericDAO<Warehouse, Long> warehouseDao,
                                                final GenericDAO<ShopWarehouse, Long> shopWarehouseDao) {
        this.shopDao = shopDao;
        this.warehouseDao = warehouseDao;
        this.shopWarehouseDao = shopWarehouseDao;
    }

    /** {@inheritDoc} */
    public List<Shop> getAll() {
        return this.shopDao.findAll();
    }

    /** {@inheritDoc} */
    public Map<Long, Set<Long>> getShopsByFulfilmentMap() {

        final List<ShopWarehouse> swAll = this.shopWarehouseDao.findAll();
        final Map<Long, Set<Long>> map = new HashMap<Long, Set<Long>>(swAll.size() * 2);

        for (final ShopWarehouse sw : swAll) {

            if (!sw.isDisabled()) {

                final long wId = sw.getWarehouse().getWarehouseId();
                final long sId = sw.getShop().getShopId();

                Set<Long> sws = map.get(wId);
                if (sws == null) {
                    sws = new HashSet<Long>();
                    map.put(wId, sws);
                }

                sws.add(sId);
            }

        }

        return map;
    }

    /** {@inheritDoc} */
    public List<Warehouse> getShopWarehouses(final long shopId, final boolean includeDisabled) {
        if (includeDisabled) {
            return new ArrayList<Warehouse>(warehouseDao.findByNamedQuery("ASSIGNED.WAREHOUSES.TO.SHOP", shopId));
        }
        return new ArrayList<Warehouse>(warehouseDao.findByNamedQuery("ASSIGNED.WAREHOUSES.TO.SHOP.DISABLED", shopId, Boolean.FALSE));
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getShopWarehouseIds(final long shopId, final boolean includeDisabled) {
        return transform(getShopWarehouses(shopId, includeDisabled));
    }

    public Set<Long> transform(final Collection<Warehouse> warehouses) {
        final Set<Long> result = new LinkedHashSet<Long>(warehouses.size());
        for (Warehouse category : warehouses) {
            result.add(category.getWarehouseId());
        }
        return result;
    }

}
