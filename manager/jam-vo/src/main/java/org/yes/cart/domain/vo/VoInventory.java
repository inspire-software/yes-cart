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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User: denispavlov
 */
@Dto
public class VoInventory {

    @DtoField(value = "skuWarehouseId", readOnly = true)
    private long skuWarehouseId;

    @DtoField(value = "skuCode")
    private String skuCode;

    @DtoField(value = "skuName", readOnly = true)
    private String skuName;

    @DtoField(value = "warehouseCode")
    private String warehouseCode;

    @DtoField(value = "warehouseName", readOnly = true)
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

    @DtoField(value = "restockNotes", converter = "DisplayValues")
    private List<MutablePair<String, String>> restockNotes;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    public long getSkuWarehouseId() {
        return skuWarehouseId;
    }

    public void setSkuWarehouseId(final long skuWarehouseId) {
        this.skuWarehouseId = skuWarehouseId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(final String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(final String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(final BigDecimal reserved) {
        this.reserved = reserved;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    public LocalDateTime getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(final Boolean featured) {
        this.featured = featured;
    }

    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public LocalDateTime getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(final LocalDateTime restockDate) {
        this.restockDate = restockDate;
    }

    public List<MutablePair<String, String>> getRestockNotes() {
        return restockNotes;
    }

    public void setRestockNotes(final List<MutablePair<String, String>> restockNotes) {
        this.restockNotes = restockNotes;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
