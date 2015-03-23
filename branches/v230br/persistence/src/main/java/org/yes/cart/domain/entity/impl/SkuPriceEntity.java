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


import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SkuPriceEntity implements org.yes.cart.domain.entity.SkuPrice, java.io.Serializable {

    private long skuPriceId;
    private long version;

    private ProductSku sku;
    private Shop shop;
    private String currency;
    private BigDecimal quantity;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private BigDecimal minimalPrice;
    private Date salefrom;
    private Date saleto;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;
    private String tag;

    public SkuPriceEntity() {
    }


    public ProductSku getSku() {
        return this.sku;
    }

    public void setSku(ProductSku sku) {
        this.sku = sku;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRegularPrice() {
        return this.regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getSalePriceForCalculation() {  //TODO: V2 time machine
        if (salefrom == null) {
            if (saleto == null) {
                return this.salePrice;
            } else {
                if (System.currentTimeMillis() < saleto.getTime()) {
                    return this.salePrice;  //sale not yet end
                } else {
                    return null; //the sale is end;
                }
            }
        } else {
            if (saleto == null) {
                if (System.currentTimeMillis() > salefrom.getTime()) {
                    return this.salePrice; //endless sale
                } else {
                    return null; // sale not yet started
                }
            } else {
                if (System.currentTimeMillis() > salefrom.getTime() && System.currentTimeMillis() < saleto.getTime()) {
                    return this.salePrice; //sale in time range
                } else {
                    return null;
                }
            }
        }
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getMinimalPrice() {
        return this.minimalPrice;
    }

    public void setMinimalPrice(BigDecimal minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    public Date getSalefrom() {
        return this.salefrom;
    }

    public void setSalefrom(Date salefrom) {
        this.salefrom = salefrom;
    }

    public Date getSaleto() {
        return this.saleto;
    }

    public void setSaleto(Date saleto) {
        this.saleto = saleto;
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

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public long getSkuPriceId() {
        return this.skuPriceId;
    }

    public long getId() {
        return this.skuPriceId;
    }

    public void setSkuPriceId(long skuPriceId) {
        this.skuPriceId = skuPriceId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


