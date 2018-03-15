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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Dto
@XmlRootElement(name = "cart-shopping-context")
public class CartShoppingContextRO implements Serializable {

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
    private List<String> latestViewedSkus;
    @DtoField(readOnly = true)
    private List<String> latestViewedCategories;
    @DtoField(readOnly = true)
    private String resolvedIp;

    @XmlElement(name = "customer-email")
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }


    @XmlAttribute(name = "resolved-ip")
    public String getResolvedIp() {
        return resolvedIp;
    }

    public void setResolvedIp(final String resolvedIp) {
        this.resolvedIp = resolvedIp;
    }

    @XmlElementWrapper(name = "latest-viewed-skus")
    @XmlElement(name = "sku")
    public List<String> getLatestViewedSkus() {
        return latestViewedSkus;
    }

    public void setLatestViewedSkus(final List<String> latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus != null ? new ArrayList<>(latestViewedSkus) : new ArrayList<>(0);
    }

    @XmlElementWrapper(name = "latest-viewed-categories")
    @XmlElement(name = "category")
    public List<String> getLatestViewedCategories() {
        return latestViewedCategories;
    }

    public void setLatestViewedCategories(final List<String> latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories != null ? new ArrayList<>(latestViewedCategories) : new ArrayList<>(0);
    }

    @XmlElement(name = "customer-name")
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    @XmlElementWrapper(name = "customer-shops")
    @XmlElement(name = "shop")
    public List<String> getCustomerShops() {
        return customerShops;
    }

    public void setCustomerShops(final List<String> shops) {
        this.customerShops = shops != null ? new ArrayList<>(shops) : new ArrayList<>(0);
    }

    @XmlAttribute(name = "shop-id")
    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    @XmlAttribute(name = "shop-code")
    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    @XmlAttribute(name = "customer-shop-id")
    public long getCustomerShopId() {
        return customerShopId;
    }

    public void setCustomerShopId(final long customerShopId) {
        this.customerShopId = customerShopId;
    }

    @XmlAttribute(name = "customer-shop-code")
    public String getCustomerShopCode() {
        return customerShopCode;
    }

    public void setCustomerShopCode(final String customerShopCode) {
        this.customerShopCode = customerShopCode;
    }

    @XmlAttribute(name = "country-code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    @XmlAttribute(name = "state-code")
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    @XmlAttribute(name = "tax-info-change-view-enabled")
    public boolean isTaxInfoChangeViewEnabled() {
        return taxInfoChangeViewEnabled;
    }

    public void setTaxInfoChangeViewEnabled(final boolean taxInfoChangeViewEnabled) {
        this.taxInfoChangeViewEnabled = taxInfoChangeViewEnabled;
    }

    @XmlAttribute(name = "tax-info-enabled")
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    public void setTaxInfoEnabled(final boolean taxInfoEnabled) {
        this.taxInfoEnabled = taxInfoEnabled;
    }

    @XmlAttribute(name = "tax-info-use-net")
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    public void setTaxInfoUseNet(final boolean taxInfoUseNet) {
        this.taxInfoUseNet = taxInfoUseNet;
    }

    @XmlAttribute(name = "tax-info-show-amount")
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    public void setTaxInfoShowAmount(final boolean taxInfoShowAmount) {
        this.taxInfoShowAmount = taxInfoShowAmount;
    }

    @XmlAttribute(name = "hide-prices")
    public boolean isHidePrices() {
        return hidePrices;
    }

    public void setHidePrices(final boolean hidePrices) {
        this.hidePrices = hidePrices;
    }
}
