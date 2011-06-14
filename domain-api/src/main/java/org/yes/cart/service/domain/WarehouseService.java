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
    List<Warehouse> findByShopId(long shopId);

    /**
     * Set usage rank to ShopWarehouseDTO.
     * @param shopWarehouseId   pk of given ShopWarehouseDTO
     * @param newRank  rank to set
     */
    void setShopWarehouseRank(long shopWarehouseId, int newRank);

    /**
     * Assign given warehouse to given shop.
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     * @return {@link ShopWarehouse}
     */
    ShopWarehouse assignWarehouse(long warehouseId, long shopId);

    /**
     * Unassign given warehouse from shop
     *
     * @param warehouseId warehouse id
     * @param shopId      shop id
     */
    void unassignWarehouse(long warehouseId, long shopId);


}
