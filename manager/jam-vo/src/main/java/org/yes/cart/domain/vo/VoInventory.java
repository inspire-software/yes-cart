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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import java.math.BigDecimal;

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
}
