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
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;

import java.math.BigDecimal;

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

    @DtoField(value = "sku.code", readOnly = true)
    private String skuCode;

    @DtoField(value = "sku.name", readOnly = true)
    private String skuName;

    @DtoField(value = "warehouse.code", readOnly = true)
    private String warehouseCode;

    @DtoField(value = "warehouse.name", readOnly = true)
    private String warehouseName;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    /** {@inheritDoc} */
    public long getId() {
        return getSkuWarehouseId();
    }

    /** {@inheritDoc} */
    public long getSkuWarehouseId() {
        return skuWarehouseId;
    }

    /** {@inheritDoc} */
    public void setSkuWarehouseId(final long skuWarehouseId) {
        this.skuWarehouseId = skuWarehouseId;
    }

    /** {@inheritDoc} */
    public String getSkuCode() {
        return skuCode;
    }

    /** {@inheritDoc} */
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /** {@inheritDoc} */
    public String getSkuName() {
        return skuName;
    }

    /** {@inheritDoc} */
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /** {@inheritDoc} */
    public String getWarehouseCode() {
        return warehouseCode;
    }

    /** {@inheritDoc} */
    public void setWarehouseCode(final String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    /** {@inheritDoc} */
    public String getWarehouseName() {
        return warehouseName;
    }

    /** {@inheritDoc} */
    public void setWarehouseName(final String warehouseName) {
        this.warehouseName = warehouseName;
    }

    /** {@inheritDoc} */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /** {@inheritDoc} */
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
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
                '}';
    }
}
