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
 * Date: 28/10/2014
 * Time: 17:23
 */
@Dto
public class VoTax {

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

    public long getTaxId() {
        return taxId;
    }

    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isExclusiveOfPrice() {
        return exclusiveOfPrice;
    }

    public void setExclusiveOfPrice(final boolean exclusiveOfPrice) {
        this.exclusiveOfPrice = exclusiveOfPrice;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
