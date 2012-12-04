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

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.service.dto.support.PriceListFilter;

import java.util.Date;

/**
 * Simple filter bean implementation.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:07 PM
 */
public class PriceListFilterImpl implements PriceListFilter {

    private ShopDTO shop;
    private String currencyCode;
    private String productCode;
    private Boolean productCodeExact = Boolean.FALSE;
    private String tag;
    private Boolean tagExact = Boolean.FALSE;
    private Date from;
    private Date to;

    /** {@inheritDoc} */
    public ShopDTO getShop() {
        return shop;
    }

    public void setShop(final ShopDTO shop) {
        this.shop = shop;
    }

    /** {@inheritDoc} */
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /** {@inheritDoc} */
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String product) {
        this.productCode = product;
    }

    /** {@inheritDoc} */
    public Boolean getProductCodeExact() {
        return productCodeExact;
    }

    public void setProductCodeExact(final Boolean productCodeExact) {
        this.productCodeExact = productCodeExact != null && productCodeExact;
    }

    /** {@inheritDoc} */
    public Date getFrom() {
        return from;
    }

    public void setFrom(final Date from) {
        this.from = from;
    }

    /** {@inheritDoc} */
    public Date getTo() {
        return to;
    }

    public void setTo(final Date to) {
        this.to = to;
    }

    /** {@inheritDoc} */
    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    /** {@inheritDoc} */
    public Boolean getTagExact() {
        return tagExact;
    }

    public void setTagExact(final Boolean tagExact) {
        this.tagExact = tagExact != null && tagExact;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "PriceListFilterImpl{" +
                "shop=" + shop +
                ", currencyCode='" + currencyCode + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productCodeExact=" + productCodeExact +
                ", tag='" + tag + '\'' +
                ", tagExact=" + tagExact +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
