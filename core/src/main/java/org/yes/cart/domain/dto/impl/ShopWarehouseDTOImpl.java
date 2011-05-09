package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ShopWarehouseDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ShopWarehouseDTOImpl implements ShopWarehouseDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "shopWarehouseId", readOnly = true)
    private long shopWarehouseId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "warehouse.warehouseId", readOnly = true)
    private long warehouseId;

    @DtoField(value = "warehouse.name", readOnly = true)
    private String warehouseName;

    /** {@inheritDoc} */
    public String getWarehouseName() {
        return warehouseName;
    }

    /** {@inheritDoc} */
    public void setWarehouseName(final String warehouseName) {
        this.warehouseName = warehouseName;
    }

    /** {@inheritDoc} */
    public long getShopWarehouseId() {
        return shopWarehouseId;
    }

    /** {@inheritDoc} */
    public void setShopWarehouseId(final long shopWarehouseId) {
        this.shopWarehouseId = shopWarehouseId;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public long getWarehouseId() {
        return warehouseId;
    }

    /** {@inheritDoc} */
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
