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
package org.yes.cart.domain.entity.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;

import java.io.StringReader;
import java.time.Instant;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ShopEntity implements org.yes.cart.domain.entity.Shop, java.io.Serializable {

    private long shopId;
    private long version;

    private String code;
    private String name;
    private String description;
    private String fspointer;
    private boolean disabled;
    private Set<ShopUrl> shopUrl = new HashSet<>(0);
    private Set<ShopAlias> shopAlias = new HashSet<>(0);
    private Collection<ShopAdvPlace> advertisingPlaces = new ArrayList<>(0);
    private Collection<AttrValueShop> attributes = new ArrayList<>(0);
    private SeoEntity seoInternal;
    private Collection<ShopCategory> shopCategory = new ArrayList<>(0);
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private Shop master;

    // Below are internal caches as Shop object is long lived.

    private Map<String, AttrValueShop> attributesMap = null;
    private Map<String, List<AttrValueShop>> attributesMapMulti = null;

    private List<String> supportedCurrenciesAsList;
    private List<String> supportedShippingCountriesAsList;
    private List<String> supportedBillingCountriesAsList;
    private List<String> supportedLanguagesAsList;

    private final Map<String, List<String>> supportedRegistrationFormAttributesByType = new HashMap<>();
    private final Map<String, List<String>> supportedProfileFormAttributesByType = new HashMap<>();
    private final Map<String, List<String>> supportedProfileFormReadOnlyAttributesByType = new HashMap<>();

    private final Map<String, String> addressFormattingByTypeByLanguageByCountryCodeByCustomerType = new HashMap<>();

    private Set<String> sfRequireCustomerRegistrationApprovalTypes;
    private Set<String> sfRequireCustomerRegistrationNotificationTypes;
    private Set<String> sfRequireCustomerOrderApprovalTypes;
    private Set<String> sfBlockCustomerCheckoutTypes;
    private Set<String> sfRepeatOrdersEnabledTypes;
    private Set<String> sfB2BOrderLineRemarksEnabledTypes;
    private Set<String> sfB2BOrderFormEnabledTypes;
    private Set<String> sfShoppingListsEnabledTypes;
    private Set<String> sfRFQEnabledTypes;
    private Set<String> sfAddressBookDisabledTypes;
    private Set<String> sfShowTaxInfoTypes;
    private Set<String> sfShowTaxNetTypes;
    private Set<String> sfShowTaxAmountTypes;
    private Set<String> sfShowTaxOptionsTypes;
    private Set<String> sfShowSameBillingAddressDisabledTypes;
    private Set<String> sfHidePricesTypes;

    private Boolean B2BProfileActive = null;
    private Boolean B2BAddressBookActive = null;
    private Boolean B2BStrictPriceActive = null;
    private Boolean sfPageTraceOn = null;
    private Boolean sfRequireCustomerLogin = null;

    private List<String> productStoredAttributesAsList;

    private Set<Long> carrierSlaDisabledAsSet;
    private Map<Long, Integer> carrierSlaRanksAsMap;

    public ShopEntity() {
    }



    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getFspointer() {
        return this.fspointer;
    }

    @Override
    public void setFspointer(String fspointer) {
        this.fspointer = fspointer;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Shop getMaster() {
        return master;
    }

    @Override
    public void setMaster(final Shop master) {
        this.master = master;
    }

    @Override
    public Set<ShopUrl> getShopUrl() {
        return this.shopUrl;
    }

    @Override
    public void setShopUrl(Set<ShopUrl> shopUrl) {
        this.shopUrl = shopUrl;
    }

    @Override
    public Set<ShopAlias> getShopAlias() {
        return shopAlias;
    }

    @Override
    public void setShopAlias(final Set<ShopAlias> shopAlias) {
        this.shopAlias = shopAlias;
    }

    @Override
    public Collection<ShopAdvPlace> getAdvertisingPlaces() {
        return this.advertisingPlaces;
    }

    @Override
    public void setAdvertisingPlaces(Collection<ShopAdvPlace> advertisingPlaces) {
        this.advertisingPlaces = advertisingPlaces;
    }

    @Override
    public Collection<AttrValueShop> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Collection<AttrValueShop> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes != null ? attributes : Collections.emptyList());
    }

    private void resetAttributesMapsInternal() {
        final Map<String, AttrValueShop> rez = new HashMap<>(this.attributes != null ? this.attributes.size() * 2 : 50);
        final Map<String, List<AttrValueShop>> rezMulti = new HashMap<>(this.attributes != null ? this.attributes.size() * 2 : 50);
        if (this.attributes != null) {
            for (AttrValueShop attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttributeCode() != null) {
                    final String code = attrValue.getAttributeCode();
                    rez.put(code, attrValue);
                    final List<AttrValueShop> attrs = rezMulti.computeIfAbsent(code, k -> new ArrayList<>(1));
                    // Most values are single, preserve memory
                    attrs.add(attrValue);
                }
            }
        }
        this.attributesMap = rez;
        this.attributesMapMulti = rezMulti;
    }

    private Map<String, AttrValueShop> getAttributesMapInternal(final boolean reset) {
        if (this.attributesMap == null || reset) {
            this.resetAttributesMapsInternal();
        }
        return this.attributesMap;
    }

    private Map<String, List<AttrValueShop>> getAttributesMapMultiInternal(final boolean reset) {
        if (this.attributesMapMulti == null || reset) {
            this.resetAttributesMapsInternal();
        }
        return this.attributesMapMulti;
    }

    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        return (Map) this.getAttributesMapInternal(false);
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Override
    public Collection<ShopCategory> getShopCategory() {
        return this.shopCategory;
    }

    @Override
    public void setShopCategory(Collection<ShopCategory> shopCategory) {
        this.shopCategory = shopCategory;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getShopId() {
        return this.shopId;
    }

    @Override
    public long getId() {
        return this.shopId;
    }

    @Override
    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public String getSupportedCurrencies() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_CURRENCIES);
    }

    @Override
    public String getDefaultCurrency() {
        List<String> currencies = getSupportedCurrenciesAsList();
        if (currencies != null && !currencies.isEmpty()) {
            return currencies.get(0);
        }
        return null;
    }

    @Override
    public List<String> getSupportedCurrenciesAsList() {
        if (supportedCurrenciesAsList == null) {
            supportedCurrenciesAsList = getCsvValuesTrimmedAsListRaw(getSupportedCurrencies());
        }
        return supportedCurrenciesAsList;
    }


    @Override
    public String getSupportedShippingCountries() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_COUNTRY_SHIP);
    }

    @Override
    public List<String> getSupportedShippingCountriesAsList() {
        if (supportedShippingCountriesAsList == null) {
            supportedShippingCountriesAsList = getCsvValuesTrimmedAsListRaw(getSupportedShippingCountries());
        }
        return supportedShippingCountriesAsList;
    }

    @Override
    public String getSupportedBillingCountries() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_COUNTRY_BILL);
    }

    @Override
    public List<String> getSupportedBillingCountriesAsList() {
        if (supportedBillingCountriesAsList == null) {
            supportedBillingCountriesAsList = getCsvValuesTrimmedAsListRaw(getSupportedBillingCountries());
        }
        return supportedBillingCountriesAsList;
    }

    @Override
    public String getSupportedLanguages() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SUPPORTED_LANGUAGES);
    }

    @Override
    public List<String> getSupportedLanguagesAsList() {
        if (supportedLanguagesAsList == null) {
            supportedLanguagesAsList = getCsvValuesTrimmedAsListRaw(getSupportedLanguages());
        }
        return supportedLanguagesAsList;
    }

    @Override
    public String getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType(final String countryCode, final String locale, final String customerType, final String addressType) {

        final String countryKey = StringUtils.isNotBlank(customerType) ? countryCode + "_" + customerType : countryCode;
        final String formatKey = addressType + "#" + locale + "#" + countryKey;

        String format = addressFormattingByTypeByLanguageByCountryCodeByCustomerType.get(formatKey);

        if (format == null) {

            final String addressTypePrefix = "_" + addressType;

            if (StringUtils.isNotBlank(customerType)) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + locale + "_" + customerType);
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + locale + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + customerType);
                }
                if (format == null) {
                    format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + customerType);
                }
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + countryCode);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + countryCode);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + "_" + locale);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX + addressTypePrefix);
            }
            if (format == null) {
                format = getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER_PREFIX);
            }
            addressFormattingByTypeByLanguageByCountryCodeByCustomerType.put(formatKey, format);
        }
        return format;
    }

    @Override
    public String getSupportedRegistrationFormAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX + '_' + customerType);
    }

    @Override
    public List<String> getSupportedRegistrationFormAttributesAsList(final String customerType) {
        return this.supportedRegistrationFormAttributesByType.computeIfAbsent(
                customerType, t -> getCsvValuesTrimmedAsListRaw(getSupportedRegistrationFormAttributes(t)));
    }

    @Override
    public String getSupportedProfileFormAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX + '_' + customerType);
    }

    @Override
    public List<String> getSupportedProfileFormAttributesAsList(final String customerType) {
        return this.supportedProfileFormAttributesByType.computeIfAbsent(
                customerType, t -> getCsvValuesTrimmedAsListRaw(getSupportedProfileFormAttributes(t)));
    }

    @Override
    public String getSupportedProfileFormReadOnlyAttributes(final String customerType) {
        if (StringUtils.isBlank(customerType)) {
            return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX);
        }
        return getAttributeValueByCode(AttributeNamesKeys.Shop.CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX + '_' + customerType);
    }

    @Override
    public List<String> getSupportedProfileFormReadOnlyAttributesAsList(final String customerType) {
        return this.supportedProfileFormReadOnlyAttributesByType.computeIfAbsent(
                customerType, t -> getCsvValuesTrimmedAsListRaw(getSupportedProfileFormReadOnlyAttributes(t)));
    }

    @Override
    public String getProductStoredAttributes() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_STORED_ATTRIBUTES);
    }

    @Override
    public List<String> getProductStoredAttributesAsList() {
        if (productStoredAttributesAsList == null) {
            productStoredAttributesAsList = getCsvValuesTrimmedAsListRaw(getProductStoredAttributes());
        }
        return productStoredAttributesAsList;
    }

    @Override
    public String getDisabledCarrierSla() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CARRIER_SLA_DISABLED);
    }

    @Override
    public Set<Long> getDisabledCarrierSlaAsSet() {
        if (carrierSlaDisabledAsSet == null) {
            carrierSlaDisabledAsSet = getCsvValuesTrimmedAsSetLong(AttributeNamesKeys.Shop.SHOP_CARRIER_SLA_DISABLED);
        }
        return carrierSlaDisabledAsSet;
    }

    @Override
    public String getSupportedCarrierSlaRanks() {
        return getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CARRIER_SLA_RANKS);
    }

    @Override
    public Map<Long, Integer> getSupportedCarrierSlaRanksAsMap() {
        if (carrierSlaRanksAsMap == null) {
            final Map<Long, Integer> map = new HashMap<>();
            final String conf = getSupportedCarrierSlaRanks();
            if (StringUtils.isNotBlank(conf)) {
                try {
                    final Properties props = new Properties();
                    props.load(new StringReader(conf));
                    for (final String key : props.stringPropertyNames()) {
                        map.put(NumberUtils.toLong(key), NumberUtils.toInt(props.getProperty(key)));
                    }
                } catch (Exception exp) {
                    // do nothing
                }
            }
            carrierSlaRanksAsMap = Collections.unmodifiableMap(map);
        }
        return carrierSlaRanksAsMap;
    }

    @Override
    public Collection<AttrValueShop> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueShop> result = new ArrayList<>();
        if (attributeCode != null) {
            final List<AttrValueShop> list = this.getAttributesMapMultiInternal(false).get(attributeCode); // build maps
            if (list != null) {
                result.addAll(list);
            }
        }
        return result;
    }

    @Override
    public AttrValueShop getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        return this.getAttributesMapInternal(false).get(attributeCode);
    }

    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValueShop val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }

    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValueShop val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }

    @Override
    public boolean isB2BProfileActive() {
        if (this.B2BProfileActive == null) {
            this.B2BProfileActive = isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B);
        }
        return this.B2BProfileActive;
    }

    @Override
    public boolean isB2BAddressBookActive() {
        if (this.B2BAddressBookActive == null) {
            this.B2BAddressBookActive = isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B_ADDRESSBOOK);
        }
        return this.B2BAddressBookActive;
    }

    @Override
    public boolean isB2BStrictPriceActive() {
        if (this.B2BStrictPriceActive == null) {
            this.B2BStrictPriceActive = isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PRICE);
        }
        return this.B2BStrictPriceActive;
    }

    @Override
    public boolean isSfPageTraceOn() {
        if (this.sfPageTraceOn == null) {
            this.sfPageTraceOn = isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_SF_PAGE_TRACE);
        }
        return this.sfPageTraceOn;
    }

    @Override
    public boolean isSfRequireCustomerLogin() {
        if (this.sfRequireCustomerLogin == null) {
            this.sfRequireCustomerLogin = isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_LOGIN);
        }
        return this.sfRequireCustomerLogin;
    }

    private Set<String> getSfRequireCustomerRegistrationApprovalTypes() {
        if (this.sfRequireCustomerRegistrationApprovalTypes == null) {
            this.sfRequireCustomerRegistrationApprovalTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_APPROVE);
        }
        return this.sfRequireCustomerRegistrationApprovalTypes;
    }

    @Override
    public boolean isSfRequireCustomerRegistrationApproval(final String customerType) {
        return getSfRequireCustomerRegistrationApprovalTypes().contains(customerType);
    }


    private Set<String> getSfRequireCustomerRegistrationNotificationTypes() {
        if (this.sfRequireCustomerRegistrationNotificationTypes == null) {
            this.sfRequireCustomerRegistrationNotificationTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_NOTIFICATION);
        }
        return this.sfRequireCustomerRegistrationNotificationTypes;
    }

    @Override
    public boolean isSfRequireCustomerRegistrationNotification(final String customerType) {
        return getSfRequireCustomerRegistrationNotificationTypes().contains(customerType);
    }

    private Set<String> getSfRequireCustomerOrderApprovalTypes() {
        if (this.sfRequireCustomerOrderApprovalTypes == null) {
            this.sfRequireCustomerOrderApprovalTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_ORDER_APPROVE);
        }
        return this.sfRequireCustomerOrderApprovalTypes;
    }

    @Override
    public boolean isSfRequireCustomerOrderApproval(final String customerType) {
        return getSfRequireCustomerOrderApprovalTypes().contains(customerType);
    }

    private Set<String> getSfBlockCustomerCheckoutTypes() {
        if (this.sfBlockCustomerCheckoutTypes == null) {
            this.sfBlockCustomerCheckoutTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_CANNOT_PLACE_ORDER);
        }
        return this.sfBlockCustomerCheckoutTypes;
    }

    @Override
    public boolean isSfBlockCustomerCheckout(final String customerType) {
        return getSfBlockCustomerCheckoutTypes().contains(customerType);
    }

    private Set<String> getSfRepeatOrdersEnabledTypes() {
        if (this.sfRepeatOrdersEnabledTypes == null) {
            this.sfRepeatOrdersEnabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_REPEAT_ORDER_TYPES);
        }
        return this.sfRepeatOrdersEnabledTypes;
    }

    @Override
    public boolean isSfRepeatOrdersEnabled(final String customerType) {
        return getSfRepeatOrdersEnabledTypes().contains(customerType);
    }

    private Set<String> getSfShoppingListsEnabledTypes() {
        if (this.sfShoppingListsEnabledTypes == null) {
            this.sfShoppingListsEnabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_SHOPPING_LIST_TYPES);
        }
        return this.sfShoppingListsEnabledTypes;
    }

    @Override
    public boolean isSfShoppingListsEnabled(final String customerType) {
        return getSfShoppingListsEnabledTypes().contains(customerType);
    }

    private Set<String> getSfB2BOrderLineRemarksEnabledTypes() {
        if (this.sfB2BOrderLineRemarksEnabledTypes == null) {
            this.sfB2BOrderLineRemarksEnabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_B2B_LINE_REMARKS_TYPES);
        }
        return this.sfB2BOrderLineRemarksEnabledTypes;
    }

    @Override
    public boolean isSfB2BOrderLineRemarksEnabled(final String customerType) {
        return getSfB2BOrderLineRemarksEnabledTypes().contains(customerType);
    }

    private Set<String> getSfB2BOrderFormEnabledTypes() {
        if (this.sfB2BOrderFormEnabledTypes == null) {
            this.sfB2BOrderFormEnabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_SF_B2B_ORDER_FORM_TYPES);
        }
        return this.sfB2BOrderFormEnabledTypes;
    }

    @Override
    public boolean isSfB2BOrderFormEnabled(final String customerType) {
        return getSfB2BOrderFormEnabledTypes().contains(customerType);
    }

    private Set<String> getSfRFQEnabledTypes() {
        if (this.sfRFQEnabledTypes == null) {
            this.sfRFQEnabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_RFQ_CUSTOMER_TYPES);
        }
        return this.sfRFQEnabledTypes;
    }

    @Override
    public boolean isSfRFQEnabled(final String customerType) {
        return getSfRFQEnabledTypes().contains(customerType);
    }

    @Override
    public boolean isSfPromoCouponsEnabled(final String customerType) {
        return isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS);
    }

    @Override
    public boolean isSfOrderMessageEnabled(final String customerType) {
        return isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG);
    }


    private Set<String> getSfAddressBookDisabledTypes() {
        if (this.sfAddressBookDisabledTypes == null) {
            this.sfAddressBookDisabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES);
        }
        return this.sfAddressBookDisabledTypes;
    }

    @Override
    public boolean isSfAddressBookEnabled(final String customerType) {
        return !getSfAddressBookDisabledTypes().contains(customerType);
    }


    private Set<String> getSfShowTaxInfoTypes() {
        if (this.sfShowTaxInfoTypes == null) {
            this.sfShowTaxInfoTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO);
        }
        return this.sfShowTaxInfoTypes;
    }

    @Override
    public boolean isSfShowTaxInfo(final String customerType) {
        return getSfShowTaxInfoTypes().contains(customerType);
    }


    private Set<String> getSfShowTaxNetTypes() {
        if (this.sfShowTaxNetTypes == null) {
            this.sfShowTaxNetTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET);
        }
        return this.sfShowTaxNetTypes;
    }


    @Override
    public boolean isSfShowTaxNet(final String customerType) {
        return getSfShowTaxNetTypes().contains(customerType);
    }


    private Set<String> getSfShowTaxAmountTypes() {
        if (this.sfShowTaxAmountTypes == null) {
            this.sfShowTaxAmountTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT);
        }
        return this.sfShowTaxAmountTypes;
    }


    @Override
    public boolean isSfShowTaxAmount(final String customerType) {
        return getSfShowTaxAmountTypes().contains(customerType);
    }


    private Set<String> getSfShowTaxOptionsTypes() {
        if (this.sfShowTaxOptionsTypes == null) {
            this.sfShowTaxOptionsTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES);
        }
        return this.sfShowTaxOptionsTypes;
    }

    @Override
    public boolean isSfShowTaxOptions(final String customerType) {
        return getSfShowTaxOptionsTypes().contains(customerType);
    }

    private Set<String> getSfShowSameBillingAddressDisabledTypes() {
        if (this.sfShowSameBillingAddressDisabledTypes == null) {
            this.sfShowSameBillingAddressDisabledTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_DELIVERY_ONE_ADDRESS_DISABLE);
        }
        return this.sfShowSameBillingAddressDisabledTypes;
    }

    @Override
    public boolean isSfShowSameBillingAddressDisabledTypes(final String customerType) {
        return getSfShowSameBillingAddressDisabledTypes().contains(customerType);
    }

    private Set<String> getSfHidePricesTypes() {
        if (this.sfHidePricesTypes == null) {
            this.sfHidePricesTypes = getCsvValuesTrimmedAsSet(AttributeNamesKeys.Shop.SHOP_PRODUCT_HIDE_PRICES);
        }
        return this.sfHidePricesTypes;
    }

    @Override
    public boolean isSfHidePricesTypes(final String customerType) {
        return getSfHidePricesTypes().contains(customerType);
    }

    @Override
    public String getDefaultShopUrl() {
        return getDefaultShopUrlWithProtocol("http://");
    }

    @Override
    public String getDefaultShopSecureUrl() {
        return getDefaultShopUrlWithProtocol("https://");
    }

    protected String getDefaultShopUrlWithProtocol(final String protocol) {
        for (ShopUrl shopUrl : getShopUrl()) {
            if (shopUrl.isPrimary()) {
                return protocol + shopUrl.getUrl();
            }
        }
        for (ShopUrl shopUrl : getShopUrl()) {
            if (shopUrl.getUrl().endsWith("localhost") || shopUrl.getUrl().contains("127.0.0.1")) {
                continue;
            }
            return protocol + shopUrl.getUrl();
        }
        return "";
    }

    protected Set<Long> getCsvValuesTrimmedAsSetLong(final String attributeKey) {
        final List<String> csv = getCsvValuesTrimmedAsListRaw(getAttributeValueByCode(attributeKey));
        if (CollectionUtils.isNotEmpty(csv)) {
            final Set<Long> set = new HashSet<>();
            for (final String item : csv) {
                set.add(NumberUtils.toLong(item));
            }
            return Collections.unmodifiableSet(set);
        }
        return Collections.emptySet();
    }

    protected Set<String> getCsvValuesTrimmedAsSet(final String attributeKey) {
        final List<String> csv = getCsvValuesTrimmedAsList(attributeKey);
        if (CollectionUtils.isNotEmpty(csv)) {
            return Collections.unmodifiableSet(new HashSet<>(csv));
        }
        return Collections.emptySet();
    }

    protected List<String> getCsvValuesTrimmedAsList(final String attributeKey) {
        return getCsvValuesTrimmedAsListRaw(getAttributeValueByCode(attributeKey));
    }


    protected List<String> getCsvValuesTrimmedAsListRaw(final String raw) {
        if (raw != null) {
            final String[] split = StringUtils.split(raw, ',');
            final List<String> csv = new ArrayList<>(split.length);
            for (final String value : split) {
                if (StringUtils.isNotBlank(value)) {
                    csv.add(value.trim());
                }
            }
            return Collections.unmodifiableList(csv);
        }
        return Collections.emptyList();
    }


    @Override
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    @Override
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }


    /** {@inheritDoc} */
    public int hashCode() {
        return (int) (shopId ^ (shopId >>> 32));
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getShopId();
    }

}


