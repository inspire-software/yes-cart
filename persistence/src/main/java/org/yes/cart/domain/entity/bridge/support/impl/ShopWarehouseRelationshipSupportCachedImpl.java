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

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.entity.bridge.support.ShopWarehouseRelationshipSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 13:37
 */
public class ShopWarehouseRelationshipSupportCachedImpl implements ShopWarehouseRelationshipSupport {

    private final ShopWarehouseRelationshipSupport support;

    public ShopWarehouseRelationshipSupportCachedImpl(final ShopWarehouseRelationshipSupport support) {
        this.support = support;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return support.getAll();
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-allShopsFulfilmentMap")
    public Map<Long, Set<Long>> getShopsByFulfilmentMap() {
        return support.getShopsByFulfilmentMap();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopWarehousesIds")
    public Set<Long> getShopWarehouseIds(final long shopId, final boolean includeDisabled) {
        return support.getShopWarehouseIds(shopId, includeDisabled);
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-shopWarehouses")
    public List<Warehouse> getShopWarehouses(final long shopId, final boolean includeDisabled) {
        return support.getShopWarehouses(shopId, includeDisabled);
    }
}
