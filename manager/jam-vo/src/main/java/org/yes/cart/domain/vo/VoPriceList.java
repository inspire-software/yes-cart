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
import java.util.Date;

/**
 * User: denispavlov
 */
@Dto
public class VoPriceList {

    @DtoField(value = "skuPriceId", readOnly = true)
    private long skuPriceId;

    @DtoField(value = "regularPrice")
    private BigDecimal regularPrice;

    @DtoField(value = "minimalPrice")
    private BigDecimal minimalPrice;

    @DtoField(value = "salePrice")
    private BigDecimal salePrice;

    @DtoField(value = "salefrom")
    private Date salefrom;

    @DtoField(value = "saleto")
    private Date saleto;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    @DtoField(value = "currency")
    private String currency;

    @DtoField(value = "skuCode")
    private String skuCode;

    @DtoField(value = "skuName", readOnly = true)
    private String skuName;

    @DtoField(value = "shopCode")
    private String shopCode;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "pricingPolicy")
    private String pricingPolicy;

    public long getSkuPriceId() {
        return skuPriceId;
    }

    public void setSkuPriceId(final long skuPriceId) {
        this.skuPriceId = skuPriceId;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(final BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getMinimalPrice() {
        return minimalPrice;
    }

    public void setMinimalPrice(final BigDecimal minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Date getSalefrom() {
        return salefrom;
    }

    public void setSalefrom(final Date salefrom) {
        this.salefrom = salefrom;
    }

    public Date getSaleto() {
        return saleto;
    }

    public void setSaleto(final Date saleto) {
        this.saleto = saleto;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
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

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getPricingPolicy() {
        return pricingPolicy;
    }

    public void setPricingPolicy(final String pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }
}
