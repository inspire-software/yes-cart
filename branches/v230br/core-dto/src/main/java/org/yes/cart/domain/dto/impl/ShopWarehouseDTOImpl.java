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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
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

    @DtoField(value = "rank")
    private int rank;

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

    /** {@inheritDoc}*/
    public long getId() {
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

    /** {@inheritDoc} */
    public int getRank() {
        return this.rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "ShopWarehouseDTOImpl{" +
                "shopWarehouseId=" + shopWarehouseId +
                ", shopId=" + shopId +
                ", warehouseId=" + warehouseId +
                ", warehouseName='" + warehouseName + '\'' +
                ", rank=" + rank +
                '}';
    }
}
