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


import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.utils.DomainApiUtils;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

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
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal reserved = BigDecimal.ZERO;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private boolean disabled;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private LocalDateTime releaseDate;
    private int availability = AVAILABILITY_STANDARD;

    private BigDecimal minOrderQuantity;
    private BigDecimal maxOrderQuantity;
    private BigDecimal stepOrderQuantity;

    private Boolean featured = Boolean.FALSE;
    private String tag;

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
    public boolean isAvailable(final LocalDateTime now) {
        return DomainApiUtils.isObjectAvailableNow(!this.disabled, this.availablefrom, this.availableto, now);
    }

    @Override
    public boolean isReleased(final LocalDateTime now) {
        return releaseDate == null || !now.isBefore(releaseDate);
    }

    @Override
    public boolean isAvailableToSell(final boolean allowOrderMore) {
        return availability == SkuWarehouse.AVAILABILITY_ALWAYS
                || (availability != SkuWarehouse.AVAILABILITY_SHOWROOM
                        && MoneyUtils.isPositive(getQuantity())
                        && MoneyUtils.isFirstBiggerThanSecond(getQuantity(), getReserved()))
                || allowOrderMore && availability == SkuWarehouse.AVAILABILITY_BACKORDER;
    }

    @Override
    public boolean isAvailableToSell(final BigDecimal required, final boolean allowOrderMore) {
        return availability == SkuWarehouse.AVAILABILITY_ALWAYS
                || (availability != SkuWarehouse.AVAILABILITY_SHOWROOM
                    && MoneyUtils.isFirstBiggerThanOrEqualToSecond(getAvailableToSell(), required))
                || allowOrderMore && availability == SkuWarehouse.AVAILABILITY_BACKORDER;
    }

    @Override
    public boolean isAvailableToAllocate(final BigDecimal required) {
        return availability == SkuWarehouse.AVAILABILITY_ALWAYS
                || (availability != SkuWarehouse.AVAILABILITY_SHOWROOM
                    && MoneyUtils.isFirstBiggerThanOrEqualToSecond(getQuantity(), required));
    }

    @Override
    public BigDecimal getAvailableToSell() {
        return MoneyUtils.notNull(getQuantity()).subtract(
                MoneyUtils.notNull(getReserved(), BigDecimal.ZERO));
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return this.availablefrom;
    }

    @Override
    public void setAvailablefrom(LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailableto() {
        return this.availableto;
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableto(LocalDateTime availableto) {
        this.availableto = availableto;
    }

    @Override
    public int getAvailability() {
        return this.availability;
    }

    @Override
    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @Override
    public Boolean getFeatured() {
        return this.featured;
    }

    @Override
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    @Override
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    @Override
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    @Override
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void setReleaseDate(final LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
}


