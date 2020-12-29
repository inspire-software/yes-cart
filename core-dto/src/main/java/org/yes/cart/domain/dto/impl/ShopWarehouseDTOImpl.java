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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ShopWarehouseDTO;

import java.time.Instant;

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

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "disabled")
    private boolean disabled;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /** {@inheritDoc} */
    @Override
    public long getShopWarehouseId() {
        return shopWarehouseId;
    }

    /** {@inheritDoc}*/
    @Override
    public long getId() {
        return shopWarehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopWarehouseId(final long shopWarehouseId) {
        this.shopWarehouseId = shopWarehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    @Override
    public long getWarehouseId() {
        return warehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public int getRank() {
        return this.rank;
    }

    /** {@inheritDoc} */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "ShopWarehouseDTOImpl{" +
                "shopWarehouseId=" + shopWarehouseId +
                ", shopId=" + shopId +
                ", warehouseId=" + warehouseId +
                ", rank=" + rank +
                ", disabled=" + disabled +
                '}';
    }
}
