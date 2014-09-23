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

import org.yes.cart.shoppingcart.ShoppingContext;

import java.util.List;

/**
 *
 * Shopping context implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:37:13
 */
public class ShoppingContextImpl implements ShoppingContext {

    private static final long serialVersionUID =  20110509L;

    private String customerName;
    private long shopId;
    private String shopCode;
    private String customerEmail;

    private List<String> latestViewedSkus;
    private List<String> latestViewedCategories;
    private String resolvedIp;

    /** {@inheritDoc} */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /** {@inheritDoc} */
    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /** {@inheritDoc} */
    public void clearContext() {
        clearShopRelatedParameters();
        customerEmail = null;
        customerName = null;
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

    /** {@inheritDoc} */
    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    /** {@inheritDoc} */
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /** {@inheritDoc} */
    public void setLatestViewedSkus(final List<String> latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /** {@inheritDoc} */
    public List<String> getLatestViewedCategories() {
        return latestViewedCategories;
    }

    /** {@inheritDoc} */
    public void setLatestViewedCategories(final List<String> latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories;
    }

    /** {@inheritDoc} */
    public String getCustomerName() {
        return customerName;
    }

    /** {@inheritDoc} */
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void setShopCode(final String shopCode) {
        if (!shopCode.equals(this.shopCode)) {
            clearShopRelatedParameters();
        }
        this.shopCode = shopCode;
    }
}
