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
import org.yes.cart.domain.dto.TaxDTO;

import java.math.BigDecimal;

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

    @DtoField(value = "description")
    private String description;

    /** {@inheritDoc} */
    public long getId() {
        return taxId;
    }

    /** {@inheritDoc} */
    public long getTaxId() {
        return taxId;
    }

    /** {@inheritDoc} */
    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    /** {@inheritDoc} */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /** {@inheritDoc} */
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** {@inheritDoc} */
    public boolean getExclusiveOfPrice() {
        return exclusiveOfPrice;
    }

    /** {@inheritDoc} */
    public void setExclusiveOfPrice(final boolean exclusiveOfPrice) {
        this.exclusiveOfPrice = exclusiveOfPrice;
    }

    /** {@inheritDoc} */
    public String getShopCode() {
        return shopCode;
    }

    /** {@inheritDoc} */
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

}
