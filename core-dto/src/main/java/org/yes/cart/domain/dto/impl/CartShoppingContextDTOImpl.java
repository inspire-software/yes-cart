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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.shoppingcart.ShoppingContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Dto
public class CartShoppingContextDTOImpl implements ShoppingContext, Serializable {

    private static final long serialVersionUID =  20110509L;

    @DtoField(readOnly = true)
    private String customerName;
    @DtoField(readOnly = true)
    private long shopId;
    @DtoField(readOnly = true)
    private String shopCode;
    @DtoField(readOnly = true)
    private long customerShopId;
    @DtoField(readOnly = true)
    private String customerShopCode;
    @DtoField(readOnly = true)
    private String countryCode;
    @DtoField(readOnly = true)
    private String stateCode;
    @DtoField(readOnly = true)
    private String customerEmail;
    @DtoField(readOnly = true)
    private List<String> customerShops;

    @DtoField(readOnly = true)
    private boolean taxInfoChangeViewEnabled;
    @DtoField(readOnly = true)
    private boolean taxInfoEnabled;
    @DtoField(readOnly = true)
    private boolean taxInfoUseNet;
    @DtoField(readOnly = true)
    private boolean taxInfoShowAmount;

    @DtoField(readOnly = true)
    private boolean hidePrices;

    @DtoField(readOnly = true)
    private boolean managedCart;
    @DtoField(readOnly = true)
    private String managerEmail;
    @DtoField(readOnly = true)
    private String managerName;

    @DtoField(readOnly = true)
    private List<String> latestViewedSkus;
    @DtoField(readOnly = true)
    private List<String> latestViewedCategories;
    @DtoField(readOnly = true)
    private String resolvedIp;

    @Override
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }


    @Override
    public String getResolvedIp() {
        return resolvedIp;
    }

    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    @Override
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    public void setLatestViewedSkus(final List<String> latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus != null ? new ArrayList<>(latestViewedSkus) : new ArrayList<>(0);
    }

    @Override
    public List<String> getLatestViewedCategories() {
        return latestViewedCategories;
    }

    public void setLatestViewedCategories(final List<String> latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories != null ? new ArrayList<>(latestViewedCategories) : new ArrayList<>(0);
    }

    @Override
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    @Override
    public List<String> getCustomerShops() {
        return customerShops;
    }

    public void setCustomerShops(final List<String> shops) {
        this.customerShops = shops != null ? new ArrayList<>(shops) : new ArrayList<>(0);
    }

    @Override
    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public long getCustomerShopId() {
        return customerShopId;
    }

    public void setCustomerShopId(final long customerShopId) {
        this.customerShopId = customerShopId;
    }

    @Override
    public String getCustomerShopCode() {
        return customerShopCode;
    }

    public void setCustomerShopCode(final String customerShopCode) {
        this.customerShopCode = customerShopCode;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public boolean isTaxInfoChangeViewEnabled() {
        return taxInfoChangeViewEnabled;
    }

    public void setTaxInfoChangeViewEnabled(final boolean taxInfoChangeViewEnabled) {
        this.taxInfoChangeViewEnabled = taxInfoChangeViewEnabled;
    }

    @Override
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    public void setTaxInfoEnabled(final boolean taxInfoEnabled) {
        this.taxInfoEnabled = taxInfoEnabled;
    }

    @Override
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    public void setTaxInfoUseNet(final boolean taxInfoUseNet) {
        this.taxInfoUseNet = taxInfoUseNet;
    }

    @Override
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    public void setTaxInfoShowAmount(final boolean taxInfoShowAmount) {
        this.taxInfoShowAmount = taxInfoShowAmount;
    }

    @Override
    public boolean isHidePrices() {
        return hidePrices;
    }

    public void setHidePrices(final boolean hidePrices) {
        this.hidePrices = hidePrices;
    }

    @Override
    public boolean isManagedCart() {
        return managedCart;
    }

    public void setManagedCart(final boolean managedCart) {
        this.managedCart = managedCart;
    }

    @Override
    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(final String managerEmail) {
        this.managerEmail = managerEmail;
    }

    @Override
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(final String managerName) {
        this.managerName = managerName;
    }
}
