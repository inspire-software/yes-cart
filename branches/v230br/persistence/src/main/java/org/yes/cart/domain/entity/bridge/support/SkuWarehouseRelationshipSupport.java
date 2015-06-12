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

import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/05/2015
 * Time: 17:53
 */
public interface SkuWarehouseRelationshipSupport {

    /**
     * Get quantities for given sku on all warehouses.
     *
     * @param sku sku code
     *
     * @return all inventory for given sku on the system
     */
    List<SkuWarehouse> getQuantityOnWarehouse(String sku);

    /**
     * Get quantities for given sku on given warehouse.
     *
     * @param sku sku code
     * @param warehouse warehouse to check
     *
     * @return all inventory for given sku on the warehouse
     */
    List<SkuWarehouse> getQuantityOnWarehouse(String sku, Warehouse warehouse);

    /**
     * Get quantities for given sku on given warehouses.
     *
     * @param sku sku code
     * @param warehouses warehouses to check
     *
     * @return all inventory for given sku on the warehouses
     */
    List<SkuWarehouse> getQuantityOnWarehouses(String sku, Collection<Warehouse> warehouses);

}
