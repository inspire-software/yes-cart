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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.ProductSku;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CustomerWishListEntity implements org.yes.cart.domain.entity.CustomerWishList, java.io.Serializable {

    private long customerwishlistId;
    private long version;

    private ProductSku skus;
    private Customer customer;
    private String wlType;
    private String visibility;
    private String tag;
    private BigDecimal quantity;
    private BigDecimal regularPriceWhenAdded;
    private String regularPriceCurrencyWhenAdded;

    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CustomerWishListEntity() {
    }


    public ProductSku getSkus() {
        return this.skus;
    }

    public void setSkus(ProductSku skus) {
        this.skus = skus;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getWlType() {
        return this.wlType;
    }

    public void setWlType(String wlType) {
        this.wlType = wlType;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRegularPriceWhenAdded() {
        return regularPriceWhenAdded;
    }

    public void setRegularPriceWhenAdded(final BigDecimal regularPriceWhenAdded) {
        this.regularPriceWhenAdded = regularPriceWhenAdded;
    }

    public String getRegularPriceCurrencyWhenAdded() {
        return regularPriceCurrencyWhenAdded;
    }

    public void setRegularPriceCurrencyWhenAdded(final String regularPriceCurrencyWhenAdded) {
        this.regularPriceCurrencyWhenAdded = regularPriceCurrencyWhenAdded;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getCustomerwishlistId() {
        return this.customerwishlistId;
    }

    public long getId() {
        return this.customerwishlistId;
    }

    public void setCustomerwishlistId(long customerwishlistId) {
        this.customerwishlistId = customerwishlistId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


