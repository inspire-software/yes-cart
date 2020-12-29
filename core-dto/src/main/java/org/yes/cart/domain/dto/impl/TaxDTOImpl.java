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
import org.yes.cart.domain.dto.TaxDTO;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User: denispavlov
 * Date: 28/10/2014
 * Time: 17:23
 */
@Dto
public class TaxDTOImpl implements TaxDTO {

    @DtoField(value = "taxId", readOnly = true)
    private long taxId;

    @DtoField(value = "taxRate")
    private BigDecimal taxRate;
    @DtoField(value = "exclusiveOfPrice")
    private boolean exclusiveOfPrice;

    @DtoField(value = "shopCode")
    private String shopCode;
    @DtoField(value = "currency")
    private String currency;
    @DtoField(value = "code")
    private String code;

    @DtoField(value = "guid", readOnly = true)
    private String guid;

    @DtoField(value = "description")
    private String description;


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
    public long getId() {
        return taxId;
    }

    /** {@inheritDoc} */
    @Override
    public String getGuid() {
        return guid;
    }

    /** {@inheritDoc} */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /** {@inheritDoc} */
    @Override
    public long getTaxId() {
        return taxId;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getExclusiveOfPrice() {
        return exclusiveOfPrice;
    }

    /** {@inheritDoc} */
    @Override
    public void setExclusiveOfPrice(final boolean exclusiveOfPrice) {
        this.exclusiveOfPrice = exclusiveOfPrice;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    @Override
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
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

}
