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

package org.yes.cart.shoppingcart.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
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
    private long customerShopId;
    private String customerShopCode;
    private String countryCode;
    private String stateCode;
    private String customerEmail;
    private List<String> customerShops;

    private boolean taxInfoChangeViewEnabled;
    private boolean taxInfoEnabled;
    private boolean taxInfoUseNet;
    private boolean taxInfoShowAmount;

    private boolean hidePrices;

    private String managerEmail;
    private String managerName;

    private List<String> latestViewedSkus;
    private List<String> latestViewedCategories;
    private String resolvedIp;

    /** {@inheritDoc} */
    @Override
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
         * Set customer email.
         * @param customerEmail customer email.
         */
    @Override
    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
         * Clear context.
         */
    @Override
    public void clearContext() {
        clearShopRelatedParameters();
        customerEmail = null;
        customerName = null;
        customerShops = new ArrayList<>(0);
        customerShopId = shopId;
        customerShopCode = shopCode;
        managerEmail = null;
        managerName = null;
    }

    private void clearShopRelatedParameters() {
        //we do not empty the cart on log off, so we really should not remove these as well then
        //latestViewedSkus = null;
        //latestViewedCategories = null;
        resolvedIp = null;
        taxInfoChangeViewEnabled = false;
        taxInfoEnabled = false;
        taxInfoUseNet = false;
        taxInfoShowAmount = false;
        hidePrices = false;
    }

    /** {@inheritDoc} */
    @Override
    public String getResolvedIp() {
        return resolvedIp;
    }

    /**
     * Set shopper ip address.
     *
     * @param resolvedIp resolved ip address.
     */
    @Override
    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /**
     * Set latest viewed sku codes.
     *
     * @param latestViewedSkus latest viewed skus.
     */
    @Override
    public void setLatestViewedSkus(final List<String> latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public void setLatestViewedCategories(final List<String> latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories;
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Set customer name.
     *
     * @param customerName customer name.
     */
    @Override
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getCustomerShops() {
        return customerShops;
    }

    /**
     * Set customer active shops.
     *
     * @param shops customer active shops
     */
    @Override
    public void setCustomerShops(final List<String> shops) {
        this.customerShops = shops;
    }

    /** {@inheritDoc} */
    @Override
    public long getShopId() {
        return shopId;
    }

    /**
     * Set current shop id.
     *
     * @param shopId current shop id.
     */
    @Override
    public void setShopId(final long shopId) {
        if (this.shopId > 0L && this.shopId != shopId) {
            clearShopRelatedParameters();
        }
        this.shopId = shopId;
        this.customerShopId = shopId;
    }

    /** {@inheritDoc} */
    @Override
    public String getShopCode() {
        return shopCode;
    }

    /**
     * Set current shop code.
     *
     * @param shopCode current shop code.
     */
    @Override
    public void setShopCode(final String shopCode) {
        if (this.shopCode != null && !shopCode.equals(this.shopCode)) {
            clearShopRelatedParameters();
        }
        this.shopCode = shopCode;
        this.customerShopCode = shopCode;
    }

    /** {@inheritDoc} */
    @Override
    public long getCustomerShopId() {
        return customerShopId;
    }

    /**
     * Set current customer shop id.
     *
     * @param customerShopId current customer shop id.
     */
    @Override
    public void setCustomerShopId(final long customerShopId) {
        this.customerShopId = customerShopId;
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerShopCode() {
        return customerShopCode;
    }

    /**
     * Set current customer shop code.
     *
     * @param customerShopCode current customer shop code.
     */
    @Override
    public void setCustomerShopCode(final String customerShopCode) {
        this.customerShopCode = customerShopCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Set current country code.
     *
     * @param countryCode current country code.
     */
    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Set current state code.
     *
     * @param stateCode current state code.
     */
    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoChangeViewEnabled() {
        return taxInfoChangeViewEnabled;
    }

    /**
     * Set flag to indicate if tax info view change is enabled.
     *
     * @param taxInfoChangeViewEnabled true if enabled
     */
    @Override
    public void setTaxInfoChangeViewEnabled(final boolean taxInfoChangeViewEnabled) {
        this.taxInfoChangeViewEnabled = taxInfoChangeViewEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    /**
     * Set flag to indicate if tax info is enabled.
     *
     * @param taxInfoEnabled true if enabled
     */
    @Override
    public void setTaxInfoEnabled(final boolean taxInfoEnabled) {
        this.taxInfoEnabled = taxInfoEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    /**
     * Set flag to indicate to use net prices to display.
     *
     * @param taxInfoUseNet true for net, false for gross
     */
    @Override
    public void setTaxInfoUseNet(final boolean taxInfoUseNet) {
        this.taxInfoUseNet = taxInfoUseNet;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    /**
     * Set flag to indicate to display amount of tax.
     *
     * @param taxInfoShowAmount true to display amount, false to display rate
     */
    @Override
    public void setTaxInfoShowAmount(final boolean taxInfoShowAmount) {
        this.taxInfoShowAmount = taxInfoShowAmount;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHidePrices() {
        return hidePrices;
    }

    /**
     * Set flag to indicate to display/hide prices.
     *
     * @param hidePrices true to hide prices, false to show prices
     */
    @Override
    public void setHidePrices(final boolean hidePrices) {
        this.hidePrices = hidePrices;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isManagedCart() {
        return StringUtils.isNotBlank(managerEmail);
    }

    /** {@inheritDoc} */
    @Override
    public String getManagerEmail() {
        return managerEmail;
    }

    /** {@inheritDoc} */
    @Override
    public void setManagerEmail(final String managerEmail) {
        this.managerEmail = managerEmail;
    }

    /** {@inheritDoc} */
    @Override
    public String getManagerName() {
        return managerName;
    }

    /** {@inheritDoc} */
    @Override
    public void setManagerName(final String managerName) {
        this.managerName = managerName;
    }
    
}
