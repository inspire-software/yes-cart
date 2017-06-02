/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/01/2017
 * Time: 10:36
 */
public class VoShopSummary {

    private long shopId;

    private boolean disabled;

    private String code;
    private String name;
    private String themeChain;

    private Long masterId;
    private String masterCode;

    private List<MutablePair<String, Boolean>> categories = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> carriers = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> fulfilmentCentres = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, Boolean>> paymentGateways = new ArrayList<MutablePair<String, Boolean>>();
    private String paymentGatewaysIPsRegEx;

    private List<MutablePair<String, String>> locales = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, Boolean>> i18nOverrides = new ArrayList<MutablePair<String, Boolean>>();

    private List<MutablePair<String, String>> currencies = new ArrayList<MutablePair<String, String>>();

    private List<MutablePair<String, String>> billingLocations = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, String>> shippingLocations = new ArrayList<MutablePair<String, String>>();

    private String previewUrl;
    private String previewCss;

    private MutablePair<String, String> primaryUrlAndThemeChain;
    private List<MutablePair<String, String>> aliasUrlAndThemeChain = new ArrayList<MutablePair<String, String>>();

    private List<String> aliases = new ArrayList<String>();

    private MutablePair<String, Boolean> checkoutEnableGuest;
    private MutablePair<String, Boolean> checkoutEnableCoupons;
    private MutablePair<String, Boolean> checkoutEnableMessage;
    private MutablePair<String, Boolean> checkoutEnableQuanityPicker;

    private MutablePair<String, Boolean> taxEnableShow;
    private MutablePair<String, Boolean> taxEnableShowNet;
    private MutablePair<String, Boolean> taxEnableShowAmount;

    private MutablePair<String, Boolean> searchInSubCatsEnable;
    private MutablePair<String, Boolean> searchCompoundEnable;
    private MutablePair<String, Boolean> searchSuggestEnable;
    private MutablePair<String, Integer> searchSuggestMaxResults;
    private MutablePair<String, Integer> searchSuggestMinChars;


    private MutablePair<String, String> adminEmail;
    private MutablePair<String, Boolean> b2bProfileActive;
    private MutablePair<String, Boolean> b2bAddressbookActive;
    private MutablePair<String, Boolean> b2bStrictPriceActive;
    private MutablePair<String, Boolean> cookiePolicy;
    private MutablePair<String, Boolean> anonymousBrowsing;
    private MutablePair<String, String> customerSession;
    private List<MutablePair<String, String>> customerTypes = new ArrayList<MutablePair<String, String>>();
    private MutablePair<String, List<String>> customerTypesAbleToRegister;
    private MutablePair<String, List<String>> customerTypesRequireRegistrationApproval;
    private MutablePair<String, List<String>> customerTypesRequireRegistrationNotification;
    private MutablePair<String, List<String>> customerTypesSeeTax;
    private MutablePair<String, List<String>> customerTypesSeeNetPrice;
    private MutablePair<String, List<String>> customerTypesSeeTaxAmount;
    private MutablePair<String, List<String>> customerTypesChangeTaxView;
    private MutablePair<String, List<String>> customerTypesHidePrices;
    private MutablePair<String, List<String>> customerTypesDisableOneAddress;
    private MutablePair<String, List<String>> customerTypesRfq;
    private MutablePair<String, List<String>> customerTypesOrderApproval;
    private MutablePair<String, List<String>> customerTypesBlockCheckout;
    private MutablePair<String, List<String>> customerTypesRepeatOrders;
    private MutablePair<String, List<String>> customerTypesShoppingLists;
    private MutablePair<String, List<String>> customerTypesB2BOrderLineRemarks;
    private MutablePair<String, List<String>> customerTypesB2BOrderForm;
    private MutablePair<String, List<String>> customerTypesAddressBookDisabled;

    private List<MutablePair<String, Boolean>> emailTemplates = new ArrayList<MutablePair<String, Boolean>>();
    private List<MutablePair<String, Boolean>> emailTemplatesYCE = new ArrayList<MutablePair<String, Boolean>>();
    private List<MutablePair<String, String>> emailTemplatesFrom = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, String>> emailTemplatesTo = new ArrayList<MutablePair<String, String>>();
    private List<MutablePair<String, Boolean>> emailTemplatesShop = new ArrayList<MutablePair<String, Boolean>>();

    private MutablePair<String, Boolean> sfPageTraceEnabled;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(final Long masterId) {
        this.masterId = masterId;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public void setMasterCode(final String masterCode) {
        this.masterCode = masterCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getThemeChain() {
        return themeChain;
    }

    public void setThemeChain(final String themeChain) {
        this.themeChain = themeChain;
    }

    public List<MutablePair<String, Boolean>> getCategories() {
        return categories;
    }

    public void setCategories(final List<MutablePair<String, Boolean>> categories) {
        this.categories = categories;
    }

    public List<MutablePair<String, Boolean>> getCarriers() {
        return carriers;
    }

    public void setCarriers(final List<MutablePair<String, Boolean>> carriers) {
        this.carriers = carriers;
    }

    public List<MutablePair<String, Boolean>> getFulfilmentCentres() {
        return fulfilmentCentres;
    }

    public void setFulfilmentCentres(final List<MutablePair<String, Boolean>> fulfilmentCentres) {
        this.fulfilmentCentres = fulfilmentCentres;
    }

    public List<MutablePair<String, Boolean>> getPaymentGateways() {
        return paymentGateways;
    }

    public void setPaymentGateways(final List<MutablePair<String, Boolean>> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }

    public String getPaymentGatewaysIPsRegEx() {
        return paymentGatewaysIPsRegEx;
    }

    public void setPaymentGatewaysIPsRegEx(final String paymentGatewaysIPsRegEx) {
        this.paymentGatewaysIPsRegEx = paymentGatewaysIPsRegEx;
    }

    public List<MutablePair<String, String>> getLocales() {
        return locales;
    }

    public void setLocales(final List<MutablePair<String, String>> locales) {
        this.locales = locales;
    }

    public List<MutablePair<String, Boolean>> getI18nOverrides() {
        return i18nOverrides;
    }

    public void setI18nOverrides(final List<MutablePair<String, Boolean>> i18nOverrides) {
        this.i18nOverrides = i18nOverrides;
    }

    public List<MutablePair<String, String>> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(final List<MutablePair<String, String>> currencies) {
        this.currencies = currencies;
    }

    public List<MutablePair<String, String>> getBillingLocations() {
        return billingLocations;
    }

    public void setBillingLocations(final List<MutablePair<String, String>> billingLocations) {
        this.billingLocations = billingLocations;
    }

    public List<MutablePair<String, String>> getShippingLocations() {
        return shippingLocations;
    }

    public void setShippingLocations(final List<MutablePair<String, String>> shippingLocations) {
        this.shippingLocations = shippingLocations;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(final String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPreviewCss() {
        return previewCss;
    }

    public void setPreviewCss(final String previewCss) {
        this.previewCss = previewCss;
    }

    public MutablePair<String, String> getPrimaryUrlAndThemeChain() {
        return primaryUrlAndThemeChain;
    }

    public void setPrimaryUrlAndThemeChain(final MutablePair<String, String> primaryUrlAndThemeChain) {
        this.primaryUrlAndThemeChain = primaryUrlAndThemeChain;
    }

    public List<MutablePair<String, String>> getAliasUrlAndThemeChain() {
        return aliasUrlAndThemeChain;
    }

    public void setAliasUrlAndThemeChain(final List<MutablePair<String, String>> aliasUrlAndThemeChain) {
        this.aliasUrlAndThemeChain = aliasUrlAndThemeChain;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(final List<String> aliases) {
        this.aliases = aliases;
    }

    public MutablePair<String, Boolean> getCheckoutEnableGuest() {
        return checkoutEnableGuest;
    }

    public void setCheckoutEnableGuest(final MutablePair<String, Boolean> checkoutEnableGuest) {
        this.checkoutEnableGuest = checkoutEnableGuest;
    }

    public MutablePair<String, Boolean> getCheckoutEnableCoupons() {
        return checkoutEnableCoupons;
    }

    public void setCheckoutEnableCoupons(final MutablePair<String, Boolean> checkoutEnableCoupons) {
        this.checkoutEnableCoupons = checkoutEnableCoupons;
    }

    public MutablePair<String, Boolean> getCheckoutEnableMessage() {
        return checkoutEnableMessage;
    }

    public void setCheckoutEnableMessage(final MutablePair<String, Boolean> checkoutEnableMessage) {
        this.checkoutEnableMessage = checkoutEnableMessage;
    }

    public MutablePair<String, Boolean> getCheckoutEnableQuanityPicker() {
        return checkoutEnableQuanityPicker;
    }

    public void setCheckoutEnableQuanityPicker(final MutablePair<String, Boolean> checkoutEnableQuanityPicker) {
        this.checkoutEnableQuanityPicker = checkoutEnableQuanityPicker;
    }

    public MutablePair<String, Boolean> getSearchInSubCatsEnable() {
        return searchInSubCatsEnable;
    }

    public void setSearchInSubCatsEnable(final MutablePair<String, Boolean> searchInSubCatsEnable) {
        this.searchInSubCatsEnable = searchInSubCatsEnable;
    }

    public MutablePair<String, Boolean> getSearchCompoundEnable() {
        return searchCompoundEnable;
    }

    public void setSearchCompoundEnable(final MutablePair<String, Boolean> searchCompoundEnable) {
        this.searchCompoundEnable = searchCompoundEnable;
    }

    public MutablePair<String, Boolean> getSearchSuggestEnable() {
        return searchSuggestEnable;
    }

    public void setSearchSuggestEnable(final MutablePair<String, Boolean> searchSuggestEnable) {
        this.searchSuggestEnable = searchSuggestEnable;
    }

    public MutablePair<String, Integer> getSearchSuggestMaxResults() {
        return searchSuggestMaxResults;
    }

    public void setSearchSuggestMaxResults(final MutablePair<String, Integer> searchSuggestMaxResults) {
        this.searchSuggestMaxResults = searchSuggestMaxResults;
    }

    public MutablePair<String, Integer> getSearchSuggestMinChars() {
        return searchSuggestMinChars;
    }

    public void setSearchSuggestMinChars(final MutablePair<String, Integer> searchSuggestMinChars) {
        this.searchSuggestMinChars = searchSuggestMinChars;
    }

    public MutablePair<String, String> getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(final MutablePair<String, String> adminEmail) {
        this.adminEmail = adminEmail;
    }

    public MutablePair<String, Boolean> getB2bProfileActive() {
        return b2bProfileActive;
    }

    public void setB2bProfileActive(final MutablePair<String, Boolean> b2bProfileActive) {
        this.b2bProfileActive = b2bProfileActive;
    }

    public MutablePair<String, Boolean> getB2bAddressbookActive() {
        return b2bAddressbookActive;
    }

    public void setB2bAddressbookActive(final MutablePair<String, Boolean> b2bAddressbookActive) {
        this.b2bAddressbookActive = b2bAddressbookActive;
    }

    public MutablePair<String, Boolean> getB2bStrictPriceActive() {
        return b2bStrictPriceActive;
    }

    public void setB2bStrictPriceActive(final MutablePair<String, Boolean> b2bStrictPriceActive) {
        this.b2bStrictPriceActive = b2bStrictPriceActive;
    }

    public MutablePair<String, Boolean> getCookiePolicy() {
        return cookiePolicy;
    }

    public void setCookiePolicy(final MutablePair<String, Boolean> cookiePolicy) {
        this.cookiePolicy = cookiePolicy;
    }

    public MutablePair<String, Boolean> getAnonymousBrowsing() {
        return anonymousBrowsing;
    }

    public void setAnonymousBrowsing(final MutablePair<String, Boolean> anonymousBrowsing) {
        this.anonymousBrowsing = anonymousBrowsing;
    }

    public MutablePair<String, String> getCustomerSession() {
        return customerSession;
    }

    public void setCustomerSession(final MutablePair<String, String> customerSession) {
        this.customerSession = customerSession;
    }

    public List<MutablePair<String, String>> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(final List<MutablePair<String, String>> customerTypes) {
        this.customerTypes = customerTypes;
    }

    public MutablePair<String, List<String>> getCustomerTypesAbleToRegister() {
        return customerTypesAbleToRegister;
    }

    public void setCustomerTypesAbleToRegister(final MutablePair<String, List<String>> customerTypesAbleToRegister) {
        this.customerTypesAbleToRegister = customerTypesAbleToRegister;
    }

    public MutablePair<String, List<String>> getCustomerTypesRequireRegistrationApproval() {
        return customerTypesRequireRegistrationApproval;
    }

    public void setCustomerTypesRequireRegistrationApproval(final MutablePair<String, List<String>> customerTypesRequireRegistrationApproval) {
        this.customerTypesRequireRegistrationApproval = customerTypesRequireRegistrationApproval;
    }

    public MutablePair<String, List<String>> getCustomerTypesRequireRegistrationNotification() {
        return customerTypesRequireRegistrationNotification;
    }

    public void setCustomerTypesRequireRegistrationNotification(final MutablePair<String, List<String>> customerTypesRequireRegistrationNotification) {
        this.customerTypesRequireRegistrationNotification = customerTypesRequireRegistrationNotification;
    }

    public MutablePair<String, List<String>> getCustomerTypesSeeTax() {
        return customerTypesSeeTax;
    }

    public void setCustomerTypesSeeTax(final MutablePair<String, List<String>> customerTypesSeeTax) {
        this.customerTypesSeeTax = customerTypesSeeTax;
    }

    public MutablePair<String, List<String>> getCustomerTypesSeeNetPrice() {
        return customerTypesSeeNetPrice;
    }

    public void setCustomerTypesSeeNetPrice(final MutablePair<String, List<String>> customerTypesSeeNetPrice) {
        this.customerTypesSeeNetPrice = customerTypesSeeNetPrice;
    }

    public MutablePair<String, List<String>> getCustomerTypesSeeTaxAmount() {
        return customerTypesSeeTaxAmount;
    }

    public void setCustomerTypesSeeTaxAmount(final MutablePair<String, List<String>> customerTypesSeeTaxAmount) {
        this.customerTypesSeeTaxAmount = customerTypesSeeTaxAmount;
    }

    public MutablePair<String, List<String>> getCustomerTypesChangeTaxView() {
        return customerTypesChangeTaxView;
    }

    public void setCustomerTypesChangeTaxView(final MutablePair<String, List<String>> customerTypesChangeTaxView) {
        this.customerTypesChangeTaxView = customerTypesChangeTaxView;
    }

    public MutablePair<String, List<String>> getCustomerTypesHidePrices() {
        return customerTypesHidePrices;
    }

    public void setCustomerTypesHidePrices(final MutablePair<String, List<String>> customerTypesHidePrices) {
        this.customerTypesHidePrices = customerTypesHidePrices;
    }

    public MutablePair<String, List<String>> getCustomerTypesDisableOneAddress() {
        return customerTypesDisableOneAddress;
    }

    public void setCustomerTypesDisableOneAddress(final MutablePair<String, List<String>> customerTypesDisableOneAddress) {
        this.customerTypesDisableOneAddress = customerTypesDisableOneAddress;
    }

    public MutablePair<String, List<String>> getCustomerTypesRfq() {
        return customerTypesRfq;
    }

    public void setCustomerTypesRfq(final MutablePair<String, List<String>> customerTypesRfq) {
        this.customerTypesRfq = customerTypesRfq;
    }

    public MutablePair<String, List<String>> getCustomerTypesOrderApproval() {
        return customerTypesOrderApproval;
    }

    public void setCustomerTypesOrderApproval(final MutablePair<String, List<String>> customerTypesOrderApproval) {
        this.customerTypesOrderApproval = customerTypesOrderApproval;
    }

    public MutablePair<String, List<String>> getCustomerTypesBlockCheckout() {
        return customerTypesBlockCheckout;
    }

    public void setCustomerTypesBlockCheckout(final MutablePair<String, List<String>> customerTypesBlockCheckout) {
        this.customerTypesBlockCheckout = customerTypesBlockCheckout;
    }

    public MutablePair<String, List<String>> getCustomerTypesRepeatOrders() {
        return customerTypesRepeatOrders;
    }

    public void setCustomerTypesRepeatOrders(final MutablePair<String, List<String>> customerTypesRepeatOrders) {
        this.customerTypesRepeatOrders = customerTypesRepeatOrders;
    }

    public MutablePair<String, List<String>> getCustomerTypesShoppingLists() {
        return customerTypesShoppingLists;
    }

    public void setCustomerTypesShoppingLists(final MutablePair<String, List<String>> customerTypesShoppingLists) {
        this.customerTypesShoppingLists = customerTypesShoppingLists;
    }

    public MutablePair<String, List<String>> getCustomerTypesB2BOrderLineRemarks() {
        return customerTypesB2BOrderLineRemarks;
    }

    public void setCustomerTypesB2BOrderLineRemarks(final MutablePair<String, List<String>> customerTypesB2BOrderLineRemarks) {
        this.customerTypesB2BOrderLineRemarks = customerTypesB2BOrderLineRemarks;
    }

    public MutablePair<String, List<String>> getCustomerTypesB2BOrderForm() {
        return customerTypesB2BOrderForm;
    }

    public void setCustomerTypesB2BOrderForm(final MutablePair<String, List<String>> customerTypesB2BOrderForm) {
        this.customerTypesB2BOrderForm = customerTypesB2BOrderForm;
    }

    public MutablePair<String, List<String>> getCustomerTypesAddressBookDisabled() {
        return customerTypesAddressBookDisabled;
    }

    public void setCustomerTypesAddressBookDisabled(final MutablePair<String, List<String>> customerTypesAddressBookDisabled) {
        this.customerTypesAddressBookDisabled = customerTypesAddressBookDisabled;
    }

    public List<MutablePair<String, Boolean>> getEmailTemplates() {
        return emailTemplates;
    }

    public void setEmailTemplates(final List<MutablePair<String, Boolean>> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    public List<MutablePair<String, String>> getEmailTemplatesFrom() {
        return emailTemplatesFrom;
    }

    public void setEmailTemplatesFrom(final List<MutablePair<String, String>> emailTemplatesFrom) {
        this.emailTemplatesFrom = emailTemplatesFrom;
    }

    public List<MutablePair<String, String>> getEmailTemplatesTo() {
        return emailTemplatesTo;
    }

    public void setEmailTemplatesTo(final List<MutablePair<String, String>> emailTemplatesTo) {
        this.emailTemplatesTo = emailTemplatesTo;
    }

    public List<MutablePair<String, Boolean>> getEmailTemplatesShop() {
        return emailTemplatesShop;
    }

    public void setEmailTemplatesShop(final List<MutablePair<String, Boolean>> emailTemplatesShop) {
        this.emailTemplatesShop = emailTemplatesShop;
    }

    public List<MutablePair<String, Boolean>> getEmailTemplatesYCE() {
        return emailTemplatesYCE;
    }

    public void setEmailTemplatesYCE(final List<MutablePair<String, Boolean>> emailTemplatesYCE) {
        this.emailTemplatesYCE = emailTemplatesYCE;
    }

    public MutablePair<String, Boolean> getSfPageTraceEnabled() {
        return sfPageTraceEnabled;
    }

    public void setSfPageTraceEnabled(final MutablePair<String, Boolean> sfPageTraceEnabled) {
        this.sfPageTraceEnabled = sfPageTraceEnabled;
    }
}
