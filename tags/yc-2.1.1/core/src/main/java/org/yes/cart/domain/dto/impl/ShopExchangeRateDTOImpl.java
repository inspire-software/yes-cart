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
import org.yes.cart.domain.dto.ShopExchangeRateDTO;

import java.math.BigDecimal;

/**
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 1:12 PM
 */
@Dto
public class ShopExchangeRateDTOImpl implements ShopExchangeRateDTO {

    private static final long serialVersionUID = 20121022L;


    @DtoField(value = "shopexchangerateId", readOnly = true)
    private long shopexchangerateId;

    @DtoField(value = "fromCurrency")
    private String fromCurrency;

    @DtoField(value = "toCurrency")
    private String toCurrency;

    @DtoField(
            value = "shop",
            converter = "shopId2Shop",
            entityBeanKeys = "org.yes.cart.domain.entity.Shop" )
    private long shopId;

    @DtoField(value = "rate")
    private BigDecimal rate;


    /** {@inheritDoc} */
    public long getShopexchangerateId() {
        return shopexchangerateId;
    }

    /** {@inheritDoc} */
    public void setShopexchangerateId(final long shopexchangerateId) {
        this.shopexchangerateId = shopexchangerateId;
    }

    /** {@inheritDoc} */
    public String getFromCurrency() {
        return fromCurrency;
    }

    /** {@inheritDoc} */
    public void setFromCurrency(final String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    /** {@inheritDoc} */
    public String getToCurrency() {
        return toCurrency;
    }

    /** {@inheritDoc} */
    public void setToCurrency(final String toCurrency) {
        this.toCurrency = toCurrency;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public BigDecimal getRate() {
        return rate;
    }

    /** {@inheritDoc} */
    public void setRate(final BigDecimal rate) {
        this.rate = rate;
    }

    /** {@inheritDoc} */
    public long getId() {
        return shopexchangerateId;
    }
}
