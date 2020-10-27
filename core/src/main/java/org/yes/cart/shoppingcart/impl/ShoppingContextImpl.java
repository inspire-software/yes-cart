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

    private String customerLogin;
    private String customerName;
    private long shopId;
    private String shopCode;
    private long customerShopId;
    private String customerShopCode;
    private String countryCode;
    private String stateCode;
    private List<String> customerShops;

    private boolean taxInfoChangeViewEnabled;
    private boolean taxInfoEnabled;
    private boolean taxInfoUseNet;
    private boolean taxInfoShowAmount;

    private boolean hidePrices;

    private String managerLogin;
    private String managerName;

    private List<String> latestViewedSkus;
    private List<String> latestViewedCategories;
    private String resolvedIp;

    /**
     * Clear context.
     */
    @Override
    public void clearContext() {
        clearShopRelatedParameters();
        customerLogin = null;
        customerName = null;
        customerShops = new ArrayList<>(0);
        customerShopId = shopId;
        customerShopCode = shopCode;
        managerLogin = null;
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
    public String getCustomerLogin() {
        return customerLogin;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerLogin(final String customerLogin) {
        this.customerLogin = customerLogin;
    }

    /** {@inheritDoc} */
    @Override
    public String getResolvedIp() {
        return resolvedIp;
    }

    /** {@inheritDoc} */
    @Override
    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getCustomerShops() {
        return customerShops;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerShops(final List<String> shops) {
        this.customerShops = shops;
    }

    /** {@inheritDoc} */
    @Override
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void setCustomerShopId(final long customerShopId) {
        this.customerShopId = customerShopId;
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerShopCode() {
        return customerShopCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setCustomerShopCode(final String customerShopCode) {
        this.customerShopCode = customerShopCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoChangeViewEnabled() {
        return taxInfoChangeViewEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxInfoChangeViewEnabled(final boolean taxInfoChangeViewEnabled) {
        this.taxInfoChangeViewEnabled = taxInfoChangeViewEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxInfoEnabled(final boolean taxInfoEnabled) {
        this.taxInfoEnabled = taxInfoEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxInfoUseNet(final boolean taxInfoUseNet) {
        this.taxInfoUseNet = taxInfoUseNet;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    /** {@inheritDoc} */
    @Override
    public void setTaxInfoShowAmount(final boolean taxInfoShowAmount) {
        this.taxInfoShowAmount = taxInfoShowAmount;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHidePrices() {
        return hidePrices;
    }

    /** {@inheritDoc} */
    @Override
    public void setHidePrices(final boolean hidePrices) {
        this.hidePrices = hidePrices;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isManagedCart() {
        return StringUtils.isNotBlank(managerLogin);
    }

    /** {@inheritDoc} */
    @Override
    public String getManagerLogin() {
        return managerLogin;
    }

    /** {@inheritDoc} */
    @Override
    public void setManagerLogin(final String managerLogin) {
        this.managerLogin = managerLogin;
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
