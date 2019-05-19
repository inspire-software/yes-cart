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
package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SkuWarehouseEntity implements org.yes.cart.domain.entity.SkuWarehouse, java.io.Serializable {

    private long skuWarehouseId;
    private long version;

    private Warehouse warehouse;
    private String skuCode;
    private BigDecimal quantity;
    private BigDecimal reserved;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public SkuWarehouseEntity() {
    }


    @Override
    public Warehouse getWarehouse() {
        return this.warehouse;
    }

    @Override
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String getSkuCode() {
        return skuCode;
    }

    @Override
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    @Override
    public BigDecimal getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getReserved() {
        return this.reserved;
    }

    @Override
    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getSkuWarehouseId() {
        return skuWarehouseId;
    }

    @Override
    public long getId() {
        return this.skuWarehouseId;
    }

    @Override
    public void setSkuWarehouseId(long skuWarehouseId) {
        this.skuWarehouseId = skuWarehouseId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public boolean isAvailableToSell() {
        return MoneyUtils.isPositive(getQuantity())
                && MoneyUtils.isFirstBiggerThanSecond(getQuantity(), getReserved());
    }

    @Override
    public boolean isAvailableToSell(final BigDecimal required) {
        return MoneyUtils.isFirstBiggerThanOrEqualToSecond(getAvailableToSell(), required);
    }

    @Override
    public boolean isAvailableToAllocate(final BigDecimal required) {
        return MoneyUtils.isFirstBiggerThanOrEqualToSecond(getQuantity(), required);
    }

    @Override
    public BigDecimal getAvailableToSell() {
        return MoneyUtils.notNull(getQuantity()).subtract(
                MoneyUtils.notNull(getReserved(), BigDecimal.ZERO));
    }
}


