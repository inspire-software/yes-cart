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

package org.yes.cart.service.dto.support.impl;

import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.service.dto.support.InventoryFilter;

/**
 * Basic filter bean implementation.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:05 PM
 */
public class InventoryFilterImpl implements InventoryFilter {

    private WarehouseDTO warehouse;
    private String productCode;
    private Boolean productCodeExact = Boolean.FALSE;

    /** {@inheritDoc} */
    public WarehouseDTO getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(final WarehouseDTO warehouse) {
        this.warehouse = warehouse;
    }

    /** {@inheritDoc} */
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    /** {@inheritDoc} */
    public Boolean getProductCodeExact() {
        return productCodeExact;
    }

    public void setProductCodeExact(final Boolean productCodeExact) {
        this.productCodeExact = productCodeExact != null && productCodeExact;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "InventoryFilterImpl{" +
                "warehouse=" + warehouse +
                ", productCode='" + productCode + '\'' +
                ", productCodeExact=" + productCodeExact +
                '}';
    }
}
