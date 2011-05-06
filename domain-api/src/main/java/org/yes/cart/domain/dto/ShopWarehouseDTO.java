package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopWarehouseDTO extends Serializable {

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
     *
     * @return Warehouse name.
     */
    String getWarehouseName();

    /**
     * Set warehouse name.
     * @param warehouseName warehouse name
     */
    void setWarehouseName(String warehouseName);


}
