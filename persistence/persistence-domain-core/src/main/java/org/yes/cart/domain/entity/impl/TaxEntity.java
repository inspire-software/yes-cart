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

package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.Tax;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 13:05
 */
public class TaxEntity implements Tax, Serializable {

    private long taxId;
    private long version;

    private BigDecimal taxRate;
    private boolean exclusiveOfPrice;

    private String shopCode;
    private String currency;
    private String code;

    private String description;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;


    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getId() {
        return taxId;
    }

    @Override
    public long getTaxId() {
        return taxId;
    }

    @Override
    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    @Override
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public boolean getExclusiveOfPrice() {
        return exclusiveOfPrice;
    }

    @Override
    public void setExclusiveOfPrice(final boolean exclusiveOfPrice) {
        this.exclusiveOfPrice = exclusiveOfPrice;
    }

    @Override
    public String getShopCode() {
        return shopCode;
    }

    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
