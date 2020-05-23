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

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 11/04/2018
 * Time: 14:54
 */
public class VoPromotionTest {

    private String shopCode;
    private String currency;
    private String customer;
    private String supplier;
    private String shipping;
    private String coupons;
    private String sku;
    private String language;
    private Instant time;

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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(final String supplier) {
        this.supplier = supplier;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(final String customer) {
        this.customer = customer;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(final String shipping) {
        this.shipping = shipping;
    }

    public String getCoupons() {
        return coupons;
    }

    public void setCoupons(final String coupons) {
        this.coupons = coupons;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(final Instant time) {
        this.time = time;
    }
}
