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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface WarehouseService extends GenericService<Warehouse> {


    /**
     * Find warehouse by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of warehouses, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Warehouse> findWarehouses(int start,
                                   int offset,
                                   String sort,
                                   boolean sortDescending,
                                   Map<String, List> filter);

    /**
     * Find warehouses by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return count
     */
    int findWarehouseCount(Map<String, List> filter);


    /**
     * Find warehouses, that assigned to given shop id.
     *
     * @param shopId given shop id
     * @param includeDisabled true to include disabled links
     * @return list of assigned warehouses
     */
    List<Warehouse> findByShopId(long shopId, boolean includeDisabled);


    /**
     * Find warehouses, that assigned to given shop id.
     *
     * @param shopId given shop id
     * @param includeDisabled true to include disabled links
     * @return list of assigned warehouses
     */
    List<Warehouse> getByShopId(long shopId, boolean includeDisabled);


    /**
     * Find warehouses, that assigned to given shop id.
     *
     * @param shopId given shop id
     * @param includeDisabled true to include disabled links
     * @return map of assigned warehouses by code
     */
    Map<String, Warehouse> getByShopIdMapped(long shopId, boolean includeDisabled);

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
     * @param soft true disables the link, false enabled the link right away
     */
    void assignWarehouse(long warehouseId, long shopId, boolean soft);


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
     * @param soft true disables the link but does not remove it, false removed the ShopWarehouse link completely
     */
    void unassignWarehouse(long warehouseId, long shopId, boolean soft);


}
