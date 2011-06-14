package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopWarehouseDTO extends Identifiable {

    /**
     * @return primary key
     */
    long getShopWarehouseId();

    /**
     * Set primary key.
     *
     * @param shopWarehouseId primary key value.
     */
    void setShopWarehouseId(long shopWarehouseId);

    /**
     * @return shop id
     */
    long getShopId();

    /**
     * @param shopId shop id
     */
    void setShopId(long shopId);

    /**
     * @return warehouse id
     */
    long getWarehouseId();

    /**
     * Set warehouse id.
     *
     * @param warehouseId warehouse id
     */
    void setWarehouseId(long warehouseId);

    /**
     * @return Warehouse name.
     */
    String getWarehouseName();

    /**
     * Set warehouse name.
     *
     * @param warehouseName warehouse name
     */
    void setWarehouseName(String warehouseName);

    /**
     * Get the rank of warehouse usage in shop.
     * @return    rank of warehouse usage
     */
    int getRank();


    /**
     * Set the rank of warehouse usage.
     * @param rank of warehouse usage.
     */
    void setRank(int rank);


}
