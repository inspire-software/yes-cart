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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.service.AddressBookFacade;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-15
 * Time: 12:31 AM
 */
public class AddressBookFacadeImpl implements AddressBookFacade {

    private final CustomerService customerService;
    private final AddressService addressService;
    private final CountryService countryService;
    private final StateService stateService;
    private final ShopService shopService;
    private final AttributeService attributeService;

    public AddressBookFacadeImpl(final CustomerService customerService,
                                 final AddressService addressService,
                                 final CountryService countryService,
                                 final StateService stateService,
                                 final ShopService shopService,
                                 final AttributeService attributeService) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.countryService = countryService;
        this.stateService = stateService;
        this.shopService = shopService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    public boolean customerHasAtLeastOneAddress(final String email, final Shop shop) {

        if (StringUtils.isNotBlank(email)) {

            final Customer customer = customerService.getCustomerByEmail(email, shop);
            if (customer != null) {
                return
                        !getAddresses(customer, shop, Address.ADDR_TYPE_BILLING).isEmpty() ||
                        !getAddresses(customer, shop, Address.ADDR_TYPE_SHIPPING).isEmpty();
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public List<Address> getAddresses(final Customer customer, final Shop shop, final String addressType) {

        final List<Address> allowed = new ArrayList<Address>();
        if (customer != null) {
            final List<Address> allAvailable = customer.getAddresses(addressType);
            final List<String> allowedCountries = Address.ADDR_TYPE_BILLING.equals(addressType) ?
                    shop.getSupportedBillingCountriesAsList() : shop.getSupportedShippingCountriesAsList();

            for (final Address address : allAvailable) {
                if (allowedCountries.contains(address.getCountryCode())) {
                    allowed.add(address);
                }
            }
        }
        return allowed;
    }

    /** {@inheritDoc} */
    public Address getAddress(final Customer customer, final String addrId, final String addressType) {
        long pk;
        try {
            pk = NumberUtils.createLong(addrId);
        } catch (NumberFormatException nfe) {
            pk = 0;
        }
        Address rez = null;
        for (Address addr : customer.getAddress()) {
            if (addr.getAddressId() == pk) {
                rez = addr;
                break;
            }
        }
        if (rez == null) {
            rez = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
            rez.setCustomer(customer);
            rez.setAddressType(addressType);
            // customer.getAddress().add(rez); Must do this when we create address only!

            final AttrValueCustomer attrValue = customer.getAttributeByCode(AttributeNamesKeys.CUSTOMER_PHONE);
            rez.setSalutation(customer.getSalutation());
            rez.setFirstname(customer.getFirstname());
            rez.setMiddlename(customer.getMiddlename());
            rez.setLastname(customer.getLastname());
            rez.setPhone1(attrValue == null ? StringUtils.EMPTY : attrValue.getVal());
        }
        return rez;
    }

    private static final List<String> DEFAULT_FIELDS = Arrays.asList(
            "firstname", "lastname",
            "addrline1", "addrline2",
            "city", "postcode",
            "stateCode", "countryCode",
            "phone1"
    );

    private static final List<String> OPTIONAL_FIELDS = Arrays.asList(
            "salutation", "middlename",
            "addrline2",
            "stateCode",
            "phone2", "mobile1", "mobile2",
            "email1", "email2",
            "custom1", "custom2", "custom3", "custom4"
    );

    /** {@inheritDoc} */
    public List<AttrValue> getShopCustomerAddressAttributes(final Customer customer, final Shop shop) {

        final String customerTypeAttribute = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.ADDRESS_PREFIX);
        final List<String> addressFormAttributes = getAddressFormAttributeList(customer, customerTypeAttribute);

        List<Attribute> formFieldsConfig = attributeService.findAttributesByCodes(AttributeGroupNames.ADDRESS, addressFormAttributes);
        if (formFieldsConfig.isEmpty()) {
            formFieldsConfig = createDefaultFormFieldConfig();
        }

        final Map<String, Attribute> formFieldsConfigMap = new HashMap<String, Attribute>();
        for (final Attribute attribute : formFieldsConfig) {
            formFieldsConfigMap.put(attribute.getCode(), attribute);
        }

        final List<AttrValue> addressAttributes = new ArrayList<AttrValue>();
        for (final String formFieldConfigName : addressFormAttributes) {
            final Attribute formFieldConfig = formFieldsConfigMap.get(formFieldConfigName);
            if (formFieldConfig != null) {
                final AttrValue av = attributeService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                av.setAttribute(formFieldConfig);
                addressAttributes.add(av);
            }
        }

        return addressAttributes;
    }

    private List<Attribute> createDefaultFormFieldConfig() {

        final Etype etype = attributeService.getGenericDao().getEntityFactory().getByIface(Etype.class);
        etype.setBusinesstype("String");
        etype.setJavatype("java.lang.String");

        final List<Attribute> attributes = new ArrayList<Attribute>();
        for (final String addressFormAttribute : DEFAULT_FIELDS) {
            final Attribute attr = attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
            attr.setVal(addressFormAttribute);
            attr.setEtype(etype);
            attr.setMandatory(!OPTIONAL_FIELDS.contains(addressFormAttribute));
            attributes.add(attr);
        }
        return attributes;
    }

    private List<String> getAddressFormAttributeList(final Customer customer, final String customerTypeAttribute) {

        List<String> addressFormAttributes = new ArrayList<String>(DEFAULT_FIELDS);

        final List<String> formConfigsToTry;
        if (StringUtils.isNotBlank(customerTypeAttribute)) {

            final AttrValueCustomer av = customer.getAttributeByCode(customerTypeAttribute);
            if (av != null && StringUtils.isNotBlank(av.getVal())) {
                formConfigsToTry = new ArrayList<String>(Arrays.asList(av.getVal() + "_addressform", "default_addressform"));
            } else {
                formConfigsToTry = new ArrayList<String>(Arrays.asList("default_addressform"));
            }

        } else {

            formConfigsToTry = new ArrayList<String>(Arrays.asList("default_addressform"));

        }

        final List<Attribute> formConfigs = attributeService.findAttributesByCodes(AttributeGroupNames.ADDRESS, formConfigsToTry);

        final Map<String, Attribute> formConfigsMap = new HashMap<String, Attribute>();
        for (final Attribute attr : formConfigs) {
            formConfigsMap.put(attr.getCode(), attr);
        }

        for (final String configToTry : formConfigsToTry) {
            if (formConfigsMap.containsKey(configToTry)) {
                if (StringUtils.isNotBlank(formConfigsMap.get(configToTry).getVal())) {
                    final List<String> formAttrs = new ArrayList<String>();
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

    /** {@inheritDoc} */
    @Cacheable(value = "web.addressBookFacade-allCountries")
    public List<Country> getAllCountries(final String shopCode, final String addressType) {
        final Shop shop = shopService.getShopByCode(shopCode);
        final List<String> supported;
        if ("S".equals(addressType)) {
            supported = shop.getSupportedShippingCountriesAsList();
        } else {
            supported = shop.getSupportedBillingCountriesAsList();
        }
        if (supported.isEmpty()) {
            ShopCodeContext.getLog(this).warn("No '{}' countries configured for shop {}", addressType, shopCode);
            return Collections.emptyList();
        }
        return countryService.findByCriteria(Restrictions.in("countryCode", supported));
    }

    /** {@inheritDoc} */
    @Cacheable(value = "web.addressBookFacade-statesByCountry")
    public List<State> getStatesByCountry(final String countryCode) {
        return stateService.findByCountry(countryCode);
    }

    /** {@inheritDoc} */
    public void createOrUpdate(final Address address) {
        if (address.getAddressId() == 0) {
            // Need to add address to customer only just before the creation
            address.getCustomer().getAddress().add(address);
            addressService.create(address);
        } else {
            addressService.update(address);
        }
    }

    /** {@inheritDoc} */
    public void remove(Address address) {

        final boolean isDefault = address.isDefaultAddress();
        addressService.delete(address);

        if (isDefault) {
            // set new default address in case if default address was deleted
            final List<Address> restOfAddresses = addressService.getAddressesByCustomerId(
                    address.getCustomer().getCustomerId(),
                    address.getAddressType());
            if (!restOfAddresses.isEmpty()) {
                addressService.updateSetDefault(restOfAddresses.get(0));
            }
        }

    }

    /** {@inheritDoc} */
    public Address useAsDefault(Address address) {
        return addressService.updateSetDefault(address);
    }

    /** {@inheritDoc} */
    public String formatAddressFor(final Address address, final Shop shop) {
        return addressService.formatAddressFor(address, shop);
    }
}
