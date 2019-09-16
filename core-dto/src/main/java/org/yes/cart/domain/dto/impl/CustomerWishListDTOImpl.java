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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;
import org.yes.cart.domain.dto.CustomerWishListDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CustomerWishListDTOImpl implements CustomerWishListDTO {

    @DtoField(value = "customerwishlistId", readOnly = true)
    private long customerwishlistId;

    @DtoField(value = "skuCode", readOnly = true)
    private String skuCode;

    @DtoField(value = "supplierCode", readOnly = true)
    private String supplierCode;

    @DtoVirtualField(converter = "customerWishListSkuCodeToName", readOnly = true)
    private String skuName;

    @DtoField(value = "wlType")
    private String wlType;

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;

    /** {@inheritDoc} */
    @Override
    public long getCustomerwishlistId() {
        return customerwishlistId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return customerwishlistId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerwishlistId(final long customerwishlistId) {
        this.customerwishlistId = customerwishlistId;
    }

    /** {@inheritDoc} */
    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
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
    public String getWlType() {
        return wlType;
    }

    /** {@inheritDoc} */
    @Override
    public void setWlType(final String wlType) {
        this.wlType = wlType;
    }

    /** {@inheritDoc} */
    @Override
    public long getCustomerId() {
        return customerId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "CustomerWishListDTOImpl{" +
                "customerwishlistId=" + customerwishlistId +
                ", supplierCode='" + supplierCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", wlType='" + wlType + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}
