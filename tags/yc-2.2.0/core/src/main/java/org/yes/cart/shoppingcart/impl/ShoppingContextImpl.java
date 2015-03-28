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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.shoppingcart.MutableShoppingContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Shopping context implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:37:13
 */
public class ShoppingContextImpl implements MutableShoppingContext {

    private static final long serialVersionUID =  20110509L;

    private String customerName;
    private long shopId;
    private String shopCode;
    private String countryCode;
    private String stateCode;
    private String customerEmail;
    private List<String> customerShops;

    private List<String> latestViewedSkus;
    private List<String> latestViewedCategories;
    private String resolvedIp;

    /** {@inheritDoc} */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
         * Set customer email.
         * @param customerEmail customer email.
         */
    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
         * Clear context.
         */
    public void clearContext() {
        clearShopRelatedParameters();
        customerEmail = null;
        customerName = null;
        customerShops = new ArrayList<String>(0);
    }

    private void clearShopRelatedParameters() {
        //we do not empty the cart on log off, so we really should not remove these as well then
        //latestViewedSkus = null;
        //latestViewedCategories = null;
        resolvedIp = null;
    }

    /** {@inheritDoc} */
    public String getResolvedIp() {
        return resolvedIp;
    }

    /**
         * Set shopper ip address.
         *
         * TODO: YC-361
         *
         * @param resolvedIp resolved ip address.
         */
    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    /** {@inheritDoc} */
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /**
         * Set latest viewed sku codes.
         *
         * @param latestViewedSkus latest viewed skus.
         */
    public void setLatestViewedSkus(final List<String> latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /** {@inheritDoc} */
    public List<String> getLatestViewedCategories() {
        return latestViewedCategories;
    }

    /**
         * Set last viewed categories.
         *
         * TODO: YC-360
         *
         * @param latestViewedCategories comma separated list of category ids.
         */
    public void setLatestViewedCategories(final List<String> latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories;
    }

    /** {@inheritDoc} */
    public String getCustomerName() {
        return customerName;
    }

    /**
         * Set customer name.
         *
         * @param customerName customer name.
         */
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    /** {@inheritDoc} */
    public List<String> getCustomerShops() {
        return customerShops;
    }

    /**
         * Set customer active shops.
         *
         * @param shops customer active shops
         */
    public void setCustomerShops(final List<String> shops) {
        this.customerShops = shops;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /**
         * Set current shop id.
         *
         * @param shopId current shop id.
         */
    public void setShopId(final long shopId) {
        if (this.shopId != shopId) {
            clearShopRelatedParameters();
        }
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public String getShopCode() {
        return shopCode;
    }

    /**
         * Set current shop code.
         *
         * @param shopCode current shop code.
         */
    public void setShopCode(final String shopCode) {
        if (!shopCode.equals(this.shopCode)) {
            clearShopRelatedParameters();
        }
        this.shopCode = shopCode;
    }

    /** {@inheritDoc} */
    public String getCountryCode() {
        return countryCode;
    }

    /**
         * Set current country code.
         *
         * @param countryCode current country code.
         */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc} */
    public String getStateCode() {
        return stateCode;
    }

    /**
         * Set current state code.
         *
         * @param stateCode current state code.
         */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

}
