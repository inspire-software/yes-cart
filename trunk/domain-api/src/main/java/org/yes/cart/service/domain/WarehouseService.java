/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface WarehouseService extends GenericService<Warehouse> {


    /**
     * Find wharehouses, that assigned to given shop id.
     *
     * @param shopId given shop id
     * @return list of assigned warehouses
     */
    List<Warehouse> getByShopId(long shopId);

    /**
     * Set usage rank to ShopWarehouseDTO.
     * @param shopWarehouseId   pk of given ShopWarehouseDTO
     * @param newRank  rank to set
     */
    void updateShopWarehouseRank(long shopWarehouseId, int newRank);

    /**
     * Assign given warehouse to given shop.
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     * @return {@link ShopWarehouse}
     */
    ShopWarehouse assignWarehouse(long warehouseId, long shopId);


    /**
     * Get shop warehouse by his id.
     * @param shopWarehouseId  given id
     * @return   entity
     */
    ShopWarehouse findShopWarehouseById(long shopWarehouseId);

    /**
     * Unassign given warehouse from shop
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     */
    void unassignWarehouse(long warehouseId, long shopId);


}
