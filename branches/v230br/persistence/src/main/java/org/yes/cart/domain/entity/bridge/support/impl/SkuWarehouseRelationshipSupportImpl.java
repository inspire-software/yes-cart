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

import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.bridge.support.SkuWarehouseRelationshipSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/05/2015
 * Time: 18:04
 */
public class SkuWarehouseRelationshipSupportImpl implements SkuWarehouseRelationshipSupport {

    private final GenericDAO<SkuWarehouse, Long> skuWarehouseDao;


    public SkuWarehouseRelationshipSupportImpl(final GenericDAO<SkuWarehouse, Long> skuWarehouseDao) {
        this.skuWarehouseDao = skuWarehouseDao;
    }

    /** {@inheritDoc} */
    public List<SkuWarehouse> getQuantityOnWarehouse(final String sku) {

        return skuWarehouseDao.findByNamedQuery("SKUS.ON.WAREHOUSES.BY.SKUCODE.ALL", sku);
    }

    /** {@inheritDoc} */
    public List<SkuWarehouse> getQuantityOnWarehouse(final String sku, final Warehouse warehouse) {

        return skuWarehouseDao.findByNamedQuery("SKUS.ON.WAREHOUSE.BY.SKUCODE.WAREHOUSEID", sku, warehouse.getWarehouseId());

    }

    /** {@inheritDoc} */
    public List<SkuWarehouse> getQuantityOnWarehouses(final String sku, final Collection<Warehouse> warehouses) {

        if (CollectionUtils.isEmpty(warehouses)) {
            return Collections.emptyList();
        }

        final List<Long> ids = new ArrayList<Long>();
        for (final Warehouse warehouse : warehouses) {
            ids.add(warehouse.getWarehouseId());
        }

        return skuWarehouseDao.findByNamedQuery("SKUS.ON.WAREHOUSES.BY.SKUCODE.IN.WAREHOUSEID", sku, ids);

    }
}
