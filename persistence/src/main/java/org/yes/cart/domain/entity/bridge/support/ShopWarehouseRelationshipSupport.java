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

package org.yes.cart.domain.entity.bridge.support;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Warehouse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-16
 * Time: 5:01 PM
 */
public interface ShopWarehouseRelationshipSupport {

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<Shop> getAll();

    /**
     * Get map of all shops by fulfilment centre id.
     *
     * @return key => ff, value => shops
     */
    Map<Long, Set<Long>> getShopsByFulfilmentMap();

    /**
     * Get all warehouses that belong to given shop.
     *
     * @param shopId given shop
     * @param includeDisabled true to include disabled links
     * @return list of warehouses
     */
    Set<Long> getShopWarehouseIds(long shopId, boolean includeDisabled);

    /**
     * Get all warehouses that belong to given shop.
     *
     * @param shopId given shop
     * @param includeDisabled true to include disabled links
     * @return list of warehouses PKs
     */
    List<Warehouse> getShopWarehouses(long shopId, boolean includeDisabled);

}
