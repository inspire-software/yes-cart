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

    private String latestViewedSkus;
    private String latestViewedCategories;
    private String customerName;
    private long shopId;
    private String resolvedIp;
    private String customerEmail;

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
        latestViewedSkus = null;
        latestViewedCategories = null;
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
    public String getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /** {@inheritDoc} */
    public void setLatestViewedSkus(final String latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /** {@inheritDoc} */
    public String getLatestViewedCategories() {
        return latestViewedCategories;
    }

    /** {@inheritDoc} */
    public void setLatestViewedCategories(final String latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories;
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

/**
     * Get current shop id
     * @return current shop id.
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * Set current shop id.
     * @param shopId current shop id.
     */
    public void setShopId(final long shopId) {
        if (this.shopId != shopId) {
            clearShopRelatedParameters();
        }
        this.shopId = shopId;
    }

    
}
