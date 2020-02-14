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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueWithAttributeAdapter;
import org.yes.cart.service.domain.*;

import java.util.*;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 12:27
 */
public class ShopAddressCustomisationSupportImpl implements AddressCustomisationSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ShopAddressCustomisationSupportImpl.class);

    private final AddressService addressService;
    private final CountryService countryService;
    private final StateService stateService;
    private final ShopService shopService;
    private final AttributeService attributeService;


    private static final List<String> DEFAULT_FIELDS = Arrays.asList(
            "firstname", "lastname",
            "addrline1", "addrline2",
            "city", "postcode",
            "stateCode", "countryCode",
            "phone1"
    );

    private static final List<String> OPTIONAL_FIELDS = Arrays.asList(
            "name",
            "salutation", "middlename",
            "addrline2",
            "stateCode",
            "phone2", "mobile1", "mobile2",
            "email1", "email2",
            "companyName1", "companyName2", "companyDepartment",
            "custom0", "custom1", "custom2", "custom3", "custom4",
            "custom5", "custom6", "custom7", "custom8", "custom9"
    );

    public ShopAddressCustomisationSupportImpl(final AddressService addressService,
                                               final CountryService countryService,
                                               final StateService stateService,
                                               final ShopService shopService,
                                               final AttributeService attributeService) {
        this.addressService = addressService;
        this.countryService = countryService;
        this.stateService = stateService;
        this.shopService = shopService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public List<AttrValueWithAttribute> getShopCustomerAddressAttributes(final Customer customer, final Shop shop, final String addressType) {

        final List<String> addressFormAttributes = getAddressFormAttributeList(customer, shop, addressType);

        List<Attribute> formFieldsConfig = attributeService.findAttributesByCodes(AttributeGroupNames.ADDRESS, addressFormAttributes);
        if (formFieldsConfig.isEmpty()) {
            formFieldsConfig = createDefaultFormFieldConfig();
        }

        final Map<String, Attribute> formFieldsConfigMap = new HashMap<>();
        for (final Attribute attribute : formFieldsConfig) {
            formFieldsConfigMap.put(attribute.getCode(), attribute);
        }

        final List<AttrValueWithAttribute> addressAttributes = new ArrayList<>();
        for (final String formFieldConfigName : addressFormAttributes) {
            final Attribute formFieldConfig = formFieldsConfigMap.get(formFieldConfigName);
            if (formFieldConfig != null) {
                final AttrValue av = attributeService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                addressAttributes.add(new AttrValueWithAttributeAdapter(av, formFieldConfig));
            }
        }

        return addressAttributes;
    }

    private List<Attribute> createDefaultFormFieldConfig() {

        final List<Attribute> attributes = new ArrayList<>();
        for (final String addressFormAttribute : DEFAULT_FIELDS) {
            final Attribute attr = attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
            attr.setVal(addressFormAttribute);
            attr.setCode(addressFormAttribute);
            attr.setEtype(Etype.STRING_BUSINESS_TYPE);
            attr.setMandatory(!OPTIONAL_FIELDS.contains(addressFormAttribute));
            attributes.add(attr);
        }
        return attributes;
    }

    private List<String> getAddressFormAttributeList(final Customer customer, final Shop shop, final String addressType) {

        List<String> addressFormAttributes = new ArrayList<>(DEFAULT_FIELDS);

        final List<String> formConfigsToTry = new ArrayList<>();

        appendAddressFormAttributeDefinitions(customer, addressType, formConfigsToTry, shop.getCode() + "__");
        appendAddressFormAttributeDefinitions(customer, addressType, formConfigsToTry, "");

        final List<Attribute> formConfigs = attributeService.findAttributesByCodes(AttributeGroupNames.ADDRESS, formConfigsToTry);

        final Map<String, Attribute> formConfigsMap = new HashMap<>();
        for (final Attribute attr : formConfigs) {
            formConfigsMap.put(attr.getCode(), attr);
        }

        for (final String configToTry : formConfigsToTry) {
            if (formConfigsMap.containsKey(configToTry)) {
                if (StringUtils.isNotBlank(formConfigsMap.get(configToTry).getVal())) {
                    final List<String> formAttrs = new ArrayList<>();
                    for (final String formAttr : StringUtils.split(formConfigsMap.get(configToTry).getVal(), ',')) {
                        if (StringUtils.isNotBlank(formAttr)) {
                            formAttrs.add(formAttr.trim());
                        }
                    }
                    if (!formAttrs.isEmpty()) {
                        addressFormAttributes = formAttrs;
                        break;
                    }
                }
            }
        }

        return addressFormAttributes;
    }

    private void appendAddressFormAttributeDefinitions(final Customer customer, final String addressType, final List<String> formConfigsToTry, final String prefix) {

        final String type = customer.getCustomerType();
        if (StringUtils.isNotBlank(type)) {
            formConfigsToTry.add(prefix + type + "_addressform_" + addressType);
            formConfigsToTry.add(prefix + type + "_addressform");
        }
        formConfigsToTry.add(prefix + "default_addressform_" + addressType);
        formConfigsToTry.add(prefix + "default_addressform");

    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "addressBookService-allCountries")
    public List<Country> getAllCountries(final String shopCode, final String addressType) {
        final Shop shop = shopService.getShopByCode(shopCode);
        final List<String> supported;
        if (Address.ADDR_TYPE_SHIPPING.equals(addressType)) {
            supported = shop.getSupportedShippingCountriesAsList();
        } else {
            supported = shop.getSupportedBillingCountriesAsList();
        }
        if (supported.isEmpty()) {
            LOG.warn("No '{}' countries configured for shop {}", addressType, shopCode);
            return Collections.emptyList();
        }
        return countryService.findByCriteria(" where e.countryCode in (?1)", supported);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "addressBookService-statesByCountry")
    public List<State> getStatesByCountry(final String countryCode) {
        return stateService.findByCountry(countryCode);
    }


    /** {@inheritDoc} */
    @Override
    public String formatAddressFor(final Address address, final Shop shop, final Customer customer, final String lang) {
        return addressService.formatAddressFor(address, shop, customer, lang);
    }

}
