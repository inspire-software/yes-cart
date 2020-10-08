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
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;
import org.yes.cart.domain.dto.InventoryDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class InventoryDTOImpl implements InventoryDTO {

    private static final long serialVersionUID = 20121130L;

    @DtoField(value = "skuWarehouseId", readOnly = true)
    private long skuWarehouseId;

    @DtoField(value = "skuCode", readOnly = true)
    private String skuCode;

    @DtoVirtualField(converter = "warehouseSkuCodeToName", readOnly = true)
    private String skuName;

    @DtoField(value = "warehouse.code", readOnly = true)
    private String warehouseCode;

    @DtoField(value = "warehouse.name", readOnly = true)
    private String warehouseName;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    @DtoField(value = "reserved", readOnly = true)
    private BigDecimal reserved;

    @DtoField(value = "disabled")
    private boolean disabled;

    @DtoField(value = "availablefrom")
    private LocalDateTime availablefrom;

    @DtoField(value = "availableto")
    private LocalDateTime availableto;

    @DtoField(value = "availability")
    private int availability;

    @DtoField(value = "featured")
    private Boolean featured;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "minOrderQuantity")
    private BigDecimal minOrderQuantity;

    @DtoField(value = "maxOrderQuantity")
    private BigDecimal maxOrderQuantity;

    @DtoField(value = "stepOrderQuantity")
    private BigDecimal stepOrderQuantity;

    @DtoField(value = "releaseDate")
    private LocalDateTime releaseDate;

    @DtoField(value = "restockDate")
    private LocalDateTime restockDate;

    @DtoField(value = "restockNote", converter = "i18nModelConverter")
    private Map<String, String> restockNotes;


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
    public long getSkuWarehouseId() {
        return skuWarehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public void setSkuWarehouseId(final long skuWarehouseId) {
        this.skuWarehouseId = skuWarehouseId;
    }
     /** {@inheritDoc}*/
    @Override
    public long getId() {
        return skuWarehouseId;
    }

    /** {@inheritDoc} */
    @Override
    public String getSkuCode() {
        return skuCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getSkuName() {
        return skuName;
    }

    /** {@inheritDoc} */
    @Override
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /** {@inheritDoc} */
    @Override
    public String getWarehouseCode() {
        return warehouseCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setWarehouseCode(final String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getWarehouseName() {
        return warehouseName;
    }

    /** {@inheritDoc} */
    @Override
    public void setWarehouseName(final String warehouseName) {
        this.warehouseName = warehouseName;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    /** {@inheritDoc} */
    @Override
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getReserved() {
        return reserved;
    }

    /** {@inheritDoc} */
    @Override
    public void setReserved(final BigDecimal reserved) {
        this.reserved = reserved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAvailability() {
        return availability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getFeatured() {
        return featured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReleaseDate(final LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getRestockDate() {
        return restockDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRestockDate(final LocalDateTime restockDate) {
        this.restockDate = restockDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getRestockNotes() {
        return restockNotes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRestockNotes(final Map<String, String> restockNotes) {
        this.restockNotes = restockNotes;
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
        return "SkuWarehouseDTOImpl{" +
                "skuWarehouseId=" + skuWarehouseId +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", quantity=" + quantity +
                ", availablefrom=" + availablefrom +
                ", availableto=" + availableto +
                ", availability=" + availability +
                ", featured=" + featured +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
