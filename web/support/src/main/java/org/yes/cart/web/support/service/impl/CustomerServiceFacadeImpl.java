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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-25
 * Time: 7:03 PM
 */
public class CustomerServiceFacadeImpl implements CustomerServiceFacade {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceFacadeImpl.class);

    private static final String GUEST_TYPE = AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST;
    private static final String EMAIL_TYPE = AttributeNamesKeys.Cart.CUSTOMER_TYPE_EMAIL;

    private static final List<String> CORE_CUSTOMER_ATTRIBUTES = Arrays.asList(
            "login", "email", "phone", "customertype", "pricingpolicy", "b2bsubshop",
            "salutation", "firstname", "lastname", "middlename",
            "password", "confirmPassword",
            "companyname1", "companyname2", "companydepartment");

    private final CustomerService customerService;
    private final CustomerRemoveService customerRemoveService;
    private final CustomerWishListService customerWishListService;
    private final AttributeService attributeService;
    private final PassPhraseGenerator phraseGenerator;
    private final CustomerCustomisationSupport customerCustomisationSupport;
    private final AddressCustomisationSupport addressCustomisationSupport;

    public CustomerServiceFacadeImpl(final CustomerService customerService,
                                     final CustomerRemoveService customerRemoveService,
                                     final CustomerWishListService customerWishListService,
                                     final AttributeService attributeService,
                                     final PassPhraseGenerator phraseGenerator,
                                     final CustomerCustomisationSupport customerCustomisationSupport,
                                     final AddressCustomisationSupport addressCustomisationSupport) {
        this.customerService = customerService;
        this.customerRemoveService = customerRemoveService;
        this.customerWishListService = customerWishListService;
        this.attributeService = attributeService;
        this.phraseGenerator = phraseGenerator;
        this.customerCustomisationSupport = customerCustomisationSupport;
        this.addressCustomisationSupport = addressCustomisationSupport;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCustomerRegistered(final Shop shop, final String login) {
        return customerService.isCustomerExists(login, shop, true);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByLogin(final Shop shop, final String login) {
        return customerService.getCustomerByLogin(login, shop);
    }

    /** {@inheritDoc} */
    @Override
    public Customer findCustomerByLogin(final Shop shop, final String login, final boolean includeDisabled) {
        return customerService.findCustomersByLogin(login, shop, includeDisabled);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getGuestByCart(final Shop shop, final ShoppingCart cart) {
        return customerService.getCustomerByLogin(cart.getGuid(), shop);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCheckoutCustomer(final Shop shop, final ShoppingCart cart) {
        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
            return getCustomerByLogin(shop, cart.getCustomerLogin());
        }
        return getGuestByCart(shop, cart);
    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerWishList> getCustomerWishList(final Shop shop, final String type, final String login, final String visibility, final String... tags) {

        final List<CustomerWishList> filtered = new ArrayList<>();
        final Customer customer = customerService.getCustomerByLogin(login, shop);
        if (customer != null) {
            final List<CustomerWishList> allItems = customerWishListService.findWishListByCustomerId(customer.getCustomerId());

            for (final CustomerWishList item : allItems) {

                if (visibility != null && !visibility.equals(item.getVisibility())) {
                    continue;
                }

                if (type != null && !type.equals(item.getWlType())) {
                    continue;
                }

                if (tags != null && tags.length > 0) {
                    final String itemTagStr = item.getTag();
                    if (StringUtils.isNotBlank(itemTagStr)) {
                        boolean noTag = true;
                        final List<String> itemTags = Arrays.asList(StringUtils.split(itemTagStr, ' '));
                        for (final String tag : tags) {
                            if (itemTags.contains(tag)) {
                                noTag = false;
                                break;
                            }
                        }
                        if (tags.length == 1 && tags[0].equals(itemTagStr)) {
                            noTag = false; // managed lists and shopping lists tags
                        }
                        if (noTag) {
                            continue;
                        }
                    }
                } else if (CustomerWishList.SHARED.equals(visibility)) {
                    continue; // Do not allow shared lists without tag
                }

                filtered.add(item);

            }
        }
        return filtered;
    }

    /** {@inheritDoc} */
    @Override
    public void resetPassword(final Shop shop, final Customer customer) {
        customerService.resetPassword(customer, shop, null);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAccount(final Shop shop, final Customer customer) {
        if (customer != null && !shop.isSfDeleteAccountDisabled(customer.getCustomerType())) {
            customerRemoveService.deleteAccount(customer, shop, null);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateActivate(final Shop shop, final Customer customer) {
        if (customer != null) {
            customerService.updateActivate(customer, shop, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RegistrationResult registerCustomer(final Shop registrationShop, final Map<String, Object> registrationData) {

        final Object customerTypeData = registrationData.get("customerType");
        final String customerType = customerTypeData != null ? String.valueOf(customerTypeData) : null;

        final Shop configShop = registrationShop.getMaster() != null ? registrationShop.getMaster() : registrationShop;
        final Set<String> types = customerCustomisationSupport.getCustomerTypes(configShop, false);
        if (!types.contains(customerType)) {
            LOG.warn(Markers.alert(),
                    "SHOP_CUSTOMER_TYPES does not contain '{}' customer type or registrationData does not have 'customerType' in {}",
                    customerType, configShop.getCode());
            return new RegistrationResultImpl(false, "INVALID_CUSTOMER_TYPE", null, null);
        }

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setCustomerType(customerType);

        final Map<String, String> coreFormParamKeys = mapCoreFormParameters(customer, customerType, configShop, registrationData);
        final Map<String, String> addressFormParamKeys = mapAddressFormParameters(customer, customerType, configShop, registrationData);

        // Allow fallback, so that we do not need to repeat fields if we include address form
        customer.setLogin(getFallbackParameter(registrationData, coreFormParamKeys.get("login"), null));
        customer.setEmail(getFallbackParameter(registrationData, coreFormParamKeys.get("email"), addressFormParamKeys.get("email1")));
        customer.setPhone(getFallbackParameter(registrationData, coreFormParamKeys.get("phone"), addressFormParamKeys.get("phone1")));
        customer.setSalutation(getFallbackParameter(registrationData, coreFormParamKeys.get("salutation"), addressFormParamKeys.get("salutation")));
        customer.setFirstname(getFallbackParameter(registrationData, coreFormParamKeys.get("firstname"), addressFormParamKeys.get("firstname")));
        customer.setLastname(getFallbackParameter(registrationData, coreFormParamKeys.get("lastname"), addressFormParamKeys.get("lastname")));
        customer.setMiddlename(getFallbackParameter(registrationData, coreFormParamKeys.get("middlename"), addressFormParamKeys.get("middlename")));
        customer.setCompanyName1(getFallbackParameter(registrationData, coreFormParamKeys.get("companyname1"), addressFormParamKeys.get("companyName1")));
        customer.setCompanyName2(getFallbackParameter(registrationData, coreFormParamKeys.get("companyname2"), addressFormParamKeys.get("companyName2")));
        customer.setCompanyDepartment(getFallbackParameter(registrationData, coreFormParamKeys.get("companydepartment"), addressFormParamKeys.get("companyDepartment")));

        ensureLoginIsSet(customer, registrationData, addressFormParamKeys);

        if (StringUtils.isBlank(customer.getLogin()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname()) ||
                StringUtils.isBlank(customer.getCustomerType())) {
            LOG.warn(Markers.alert(), "Missing required registration data for '{}', please check that registration details have sufficient data in {}", customerType, configShop.getCode());
            return new RegistrationResultImpl(false, "INCOMPLETE_DATA", null, null);
        }

        if (isCustomerRegistered(configShop, customer.getLogin())) {  // Do not allow changing username even when duplicate is disabled A/C
            return new RegistrationResultImpl(true, "DUPLICATE_LOGIN", customer, null);
        }

        final String userPassword = getFallbackParameter(registrationData, coreFormParamKeys.get("password"), null);
        final String password = StringUtils.isNotBlank(userPassword) ? userPassword : phraseGenerator.getNextPassPhrase();
        customer.setPassword(password); // aspect will create hash but we need to generate password to be able to auto-login
        customer.setPasswordExpiry(null); // TODO: YC-906 Create password expiry flow for customers

        registerCustomerAddress(customer, customerType, configShop, registrationData, addressFormParamKeys);

        registerCustomerCustomAttributes(customer, customerType, configShop, registrationData, coreFormParamKeys, addressFormParamKeys);

        customerService.create(customer, registrationShop);

        return new RegistrationResultImpl(false, null, customer, password); // email is sent via RegistrationAspect
    }

    private void ensureLoginIsSet(final Customer customer, final Map<String, Object> registrationData, final Map<String, String> addressFormParamKeys) {
        if (StringUtils.isBlank(customer.getLogin())) { // if no explicit login is set
            if (StringUtils.isNotBlank(customer.getEmail())) {
                // 1. try email
                customer.setLogin(customer.getEmail());
            } else if (StringUtils.isNotBlank(customer.getPhone())) {
                // 2. try customer phone attribute
                customer.setLogin(customer.getPhone());
            }
        }
    }

    private String getFallbackParameter(final Map<String, Object> registrationData, final String key1, final String key2) {
        final String paramValue = key1 != null ? (String) registrationData.get(key1) : null;
        if (StringUtils.isNotBlank(paramValue)) {
            return paramValue;
        }
        return key2 != null ? (String) registrationData.get(key2) : null;
    }

    private Map<String, String> mapCoreFormParameters(final Customer customer, final String customerType, final Shop configShop, final Map<String, Object> registrationData) {

        final List<AttrValueWithAttribute> config = customerCustomisationSupport.getRegistrationAttributes(configShop, customerType);
        if (CollectionUtils.isNotEmpty(config)) {

            final Map<String, String> biDirectionalMap = new HashMap<>();
            // remap defaults
            for (final String prop : CORE_CUSTOMER_ATTRIBUTES) {
                biDirectionalMap.put(prop, prop);
            }

            // remap by value
            for (final AttrValueWithAttribute attValue : config) {
                if (StringUtils.isNotBlank(attValue.getAttribute().getVal()) && CORE_CUSTOMER_ATTRIBUTES.contains(attValue.getAttribute().getVal())) {
                    final String prop = attValue.getAttribute().getVal();
                    biDirectionalMap.put(attValue.getAttributeCode(), prop);
                    biDirectionalMap.put(prop, attValue.getAttributeCode());
                }
            }

            // Allows to detect core attributes that have customised codes
            return Collections.unmodifiableMap(biDirectionalMap);

        }

        return Collections.emptyMap();


    }

    private Map<String, String> mapAddressFormParameters(final Customer customer, final String customerType, final Shop configShop, final Map<String, Object> registrationData) {

        final String addressType = Address.ADDR_TYPE_SHIPPING;
        final List<AttrValueWithAttribute> config = addressCustomisationSupport.getShopCustomerAddressAttributes(customer, configShop, addressType);
        if (CollectionUtils.isNotEmpty(config)) {

            // remap by value
            final Map<String, String> biDirectionalMap = new HashMap<>();
            for (final AttrValueWithAttribute attValue : config) {
                final String param = "regAddressForm.".concat(attValue.getAttributeCode());
                if (registrationData.containsKey(param)) { // only map submitted parameters
                    final String prop = attValue.getAttribute().getVal();
                    biDirectionalMap.put(param, prop);
                    biDirectionalMap.put(prop, param);
                }
            }

            // if this map is not empty then address form was submitted
            return Collections.unmodifiableMap(biDirectionalMap);

        }

        return Collections.emptyMap();

    }

    private void registerCustomerAddress(final Customer customer,
                                         final String customerType,
                                         final Shop configShop,
                                         final Map<String, Object> registrationData,
                                         final Map<String, String> addressFormParamKeys) {

        if (!addressFormParamKeys.isEmpty()) {

            for (final String addressType : Arrays.asList(Address.ADDR_TYPE_SHIPPING, Address.ADDR_TYPE_BILLING)) {

                // create address instance
                final Address regAddress = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
                regAddress.setCustomer(customer);
                regAddress.setAddressType(addressType);
                regAddress.setDefaultAddress(true);
                customer.getAddress().add(regAddress);

                final Map<String, Object> attrData = new HashMap<>(registrationData);
                for (final String key : attrData.keySet()) {
                    if (addressFormParamKeys.containsKey(key)) {
                        final String prop = addressFormParamKeys.get(key);
                        final Object val = attrData.get(key);
                        try {
                            PropertyUtils.setProperty(regAddress, prop, val);
                        } catch (Exception e) {
                            LOG.error("Unable to set address property {}, val {}", prop, val);
                        }
                    }
                }

            }

    }

    }

    private void registerCustomerCustomAttributes(final Customer customer,
                                                  final String customerType,
                                                  final Shop configShop,
                                                  final Map<String, Object> registrationData,
                                                  final Map<String, String> coreFormParamKeys,
                                                  final Map<String, String> addressFormParamKeys) {

        final Map<String, Object> attrData = new HashMap<>(registrationData);
        // remove core fields attributes
        for (final Map.Entry<String, String> mapping : coreFormParamKeys.entrySet()) {
            attrData.remove(mapping.getKey());
            attrData.remove(mapping.getValue());
        }
        // remove customerType
        attrData.remove("customerType");
        // remove included address form parameters
        attrData.entrySet().removeIf(entry -> entry.getKey().startsWith("regAddressForm."));

        final List<String> allowed = customerCustomisationSupport.getSupportedRegistrationFormAttributesAsList(configShop, customerType);
        final List<String> allowedFull = new ArrayList<>();
        allowedFull.addAll(allowed);
        allowedFull.add(AttributeNamesKeys.Customer.REGISTRATION_MANAGER_EMAIL);
        allowedFull.add(AttributeNamesKeys.Customer.REGISTRATION_MANAGER_NAME);

        for (final Map.Entry<String, Object> attrVal : attrData.entrySet()) {

            if (attrVal.getValue() != null &&
                    attrVal.getValue() instanceof String && StringUtils.isNotBlank((String) attrVal.getValue())) {

                if (allowedFull.contains(attrVal.getKey())) {

                    final Attribute attribute = attributeService.findByAttributeCode(attrVal.getKey());

                    if (attribute != null) {

                        final AttrValueCustomer attrValueCustomer = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                        attrValueCustomer.setCustomer(customer);
                        attrValueCustomer.setVal(String.valueOf(attrVal.getValue()));
                        attrValueCustomer.setAttributeCode(attribute.getCode());

                        customer.getAttributes().add(attrValueCustomer);

                    } else {

                        LOG.warn(Markers.alert(),
                                "Registration data contains unknown attribute: {} in {} for '{}' form",
                                attrVal.getKey(), configShop.getCode(), customerType);

                    }

                } else {

                    LOG.warn(Markers.alert(),
                            "Registration data contains attribute that is not allowed: {} in {} for '{}' form",
                            attrVal.getKey(), configShop.getCode(), customerType);

                }

            }

        }
    }


    /** {@inheritDoc} */
    @Override
    public RegistrationResult registerGuest(final Shop registrationShop, final Map<String, Object> registrationData) {

        final String customerType = GUEST_TYPE;

        final Set<String> types = customerCustomisationSupport.getCustomerTypes(registrationShop, true);
        if (!types.contains(customerType)) {
            LOG.warn(Markers.alert(), "SHOP_CHECKOUT_ENABLE_GUEST is not enabled in {}", registrationShop.getCode());
            return new RegistrationResultImpl(false, "SHOP_CHECKOUT_ENABLE_GUEST", null, null);
        }

        final Shop configShop = registrationShop.getMaster() != null ? registrationShop.getMaster() : registrationShop;

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);

        final Map<String, String> coreFormParamKeys = mapCoreFormParameters(customer, customerType, configShop, registrationData);
        final Map<String, String> addressFormParamKeys = mapAddressFormParameters(customer, customerType, configShop, registrationData);

        // Allow fallback, so that we do not need to repeat fields if we include address form
        customer.setGuest(true);
        customer.setLogin((String) registrationData.get("cartGuid"));
        customer.setEmail(getFallbackParameter(registrationData, coreFormParamKeys.get("email"), addressFormParamKeys.get("email1")));
        customer.setPhone(getFallbackParameter(registrationData, coreFormParamKeys.get("phone"), addressFormParamKeys.get("phone1")));
        customer.setSalutation(getFallbackParameter(registrationData, coreFormParamKeys.get("salutation"), addressFormParamKeys.get("salutation")));
        customer.setFirstname(getFallbackParameter(registrationData, coreFormParamKeys.get("firstname"), addressFormParamKeys.get("firstname")));
        customer.setLastname(getFallbackParameter(registrationData, coreFormParamKeys.get("lastname"), addressFormParamKeys.get("lastname")));
        customer.setMiddlename(getFallbackParameter(registrationData, coreFormParamKeys.get("middlename"), addressFormParamKeys.get("middlename")));
        customer.setCompanyName1(getFallbackParameter(registrationData, coreFormParamKeys.get("companyname1"), addressFormParamKeys.get("companyName1")));
        customer.setCompanyName2(getFallbackParameter(registrationData, coreFormParamKeys.get("companyname2"), addressFormParamKeys.get("companyName2")));
        customer.setCompanyDepartment(getFallbackParameter(registrationData, coreFormParamKeys.get("companydepartment"), addressFormParamKeys.get("companyDepartment")));
        customer.setCustomerType(customerType);

        if (StringUtils.isBlank(customer.getLogin()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname())) {
            LOG.warn(Markers.alert(),
                    "Missing required guest data, please check that registration details have sufficient data in {}",
                    registrationShop.getCode());
            return new RegistrationResultImpl(false, "INCOMPLETE_DATA", null, null);
        }

        final Customer existingGuest = customerService.getCustomerByLogin(customer.getLogin(), registrationShop);
        if (existingGuest != null) {
            // All existing guests will be cleaned up by cron job
            existingGuest.setLogin(UUID.randomUUID().toString() + "-expired");
            customerService.update(existingGuest);
        }

        customer.setPassword(UUID.randomUUID().toString()); // make sure we have complex value

        /*
            Below code is for custom attributes, which in theory will not have any effect as this account
            will be deleted after the order is placed.

            However custom implementations may need some custom attributes in checkout process
            so will leave this.
         */

        registerCustomerAddress(customer, customerType, configShop, registrationData, addressFormParamKeys);

        registerCustomerCustomAttributes(customer, customerType, registrationShop, registrationData, coreFormParamKeys, addressFormParamKeys);

        customerService.create(customer, registrationShop);

        return new RegistrationResultImpl(false, null, customer, null);
    }


    /** {@inheritDoc} */
    @Override
    public RegistrationResult registerNewsletter(final Shop registrationShop,
                                                 final Map<String, Object> registrationData) {

        final String customerType = EMAIL_TYPE;

        final List<String> allowed = customerCustomisationSupport.getSupportedRegistrationFormAttributesAsList(registrationShop, customerType);
        if (allowed.size() >= 2) {
            LOG.warn(Markers.alert(),
                    "Newsletter sign up cannot be completed. EMAIL registration must have email and consent fields in {} form in {}",
                    customerType, registrationShop);
            return new RegistrationResultImpl(false, "CONSENT_ATTRIBUTES", null, null);
        }

        final Shop configShop = registrationShop.getMaster() != null ? registrationShop.getMaster() : registrationShop;

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);

        final Map<String, String> coreFormParamKeys = mapCoreFormParameters(customer, customerType, configShop, registrationData);

        customer.setLogin(UUID.randomUUID().toString());
        customer.setEmail(getFallbackParameter(registrationData, coreFormParamKeys.get("email"), null));
        customer.setFirstname("");
        customer.setLastname("");
        customer.setCustomerType(customerType);

        if (StringUtils.isBlank(customer.getLogin()) ||
                StringUtils.isBlank(customer.getEmail()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname())) {
            LOG.warn(Markers.alert(),
                    "Missing required newsletter data, please check that registration details have sufficient data in {}",
                    registrationShop.getCode());
            return new RegistrationResultImpl(false, "INCOMPLETE_DATA", null, null);
        }

        customer.setPassword(UUID.randomUUID().toString()); // make sure we have complex value

        // ensure consent attributes are set to true
        final Map<String, Object> registrationFullData = new HashMap<>(registrationData);
        for (final String param : allowed) {
            if (!param.equals(coreFormParamKeys.get("email"))) {
                registrationFullData.put(param, true);
            }
        }

        registerCustomerCustomAttributes(customer, customerType, registrationShop, registrationFullData, coreFormParamKeys, Collections.emptyMap());

        customerService.create(customer, registrationShop);

        return new RegistrationResultImpl(false, null, customer, null);
    }

    /** {@inheritDoc} */
    @Override
    public String notifyManagedListCreated(final Shop shop,
                                           final Map<String, Object> listData) {
        return (String) listData.get("email"); // do nothing, email is sent via ManagedListAspect
    }

    /** {@inheritDoc} */
    @Override
    public String notifyManagedListRejected(final Shop shop,
                                            final Map<String, Object> listData) {
        return (String) listData.get("email"); // do nothing, email is sent via ManagedListAspect
    }

    /** {@inheritDoc} */
    @Override
    public String contactUsEmailRequest(final Shop registrationShop,
                                        final Map<String, Object> registrationData) {
        return (String) registrationData.get("email"); // do nothing, email is sent via ContactFormAspect
    }

    /** {@inheritDoc} */
    @Override
    public List<Pair<String, I18NModel>> getShopSupportedCustomerTypes(final Shop shop) {

        return customerCustomisationSupport.getSupportedCustomerTypes(shop);

        }

    /** {@inheritDoc} */
    @Override
    public boolean isShopGuestCheckoutSupported(final Shop shop) {

        return customerCustomisationSupport.isGuestCheckoutSupported(shop);

    }

    /** {@inheritDoc} */
    @Override
    public boolean isShopCustomerTypeSupported(final Shop shop, final String customerType) {

        return customerCustomisationSupport.isCustomerTypeSupported(shop, customerType);

    }

    /** {@inheritDoc} */
    @Override
    public AttrValueWithAttribute getShopEmailAttribute(final Shop shop) {

        final List<AttrValueWithAttribute> emailAttrs =
                customerCustomisationSupport.getRegistrationAttributes(shop, AttributeNamesKeys.Cart.CUSTOMER_TYPE_EMAIL, true);

        for (final AttrValueWithAttribute attr : emailAttrs) {
            if ("email".equals(attr.getAttribute().getVal())) {
                return attr;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<AttrValueWithAttribute> getShopRegistrationAttributes(final Shop shop, final String customerType) {

        return customerCustomisationSupport.getRegistrationAttributes(shop, customerType);
    }

    /** {@inheritDoc} */
    @Override
    public List<AttrValueWithAttribute> getShopRegistrationAttributes(final Shop shop, final String customerType, final boolean force) {

        return customerCustomisationSupport.getRegistrationAttributes(shop, customerType, force);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<AttrValueWithAttribute, Boolean>> getCustomerProfileAttributes(final Shop shop, final Customer customer) {

        return customerCustomisationSupport.getProfileAttributes(shop, customer);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCustomerAttributes(final Shop profileShop, final Customer customer, final Map<String, String> values) {

        final Customer existing = customerService.findById(customer.getCustomerId());

        final List<Pair<AttrValueWithAttribute, Boolean>> currentValues = customerCustomisationSupport.getProfileAttributes(profileShop, existing);

        if (StringUtils.isNotBlank(values.get("newLogin"))) {
            final String username = values.get("newLogin");
            if (customerService.isPasswordValid(existing.getLogin(), profileShop, values.get("password"))) {
                if (!isCustomerRegistered(profileShop, username)) { // Do not allow changing username even when duplicate is disabled A/C
                    existing.setLogin(username);
                }
            }
        }

        final Map<String, String> update = new HashMap<>(values);
        update.remove("newLogin");
        update.remove("password");
        for (final Pair<AttrValueWithAttribute, Boolean> av : currentValues) {
            if (av.getSecond() != null && !av.getSecond()) {

                final String code = av.getFirst().getAttributeCode();
                final String prop = av.getFirst().getAttribute() != null ? av.getFirst().getAttribute().getVal() : null;
                final String value = update.get(code);

                if (StringUtils.isNotBlank(value)) {
                    if (StringUtils.isNotBlank(prop)) {
                        switch (prop) {
                            case "email":
                                existing.setEmail(value);
                                continue;
                            case "phone":
                                existing.setPhone(value);
                                continue;
                            case "salutation":
                                existing.setSalutation(value);
                                continue;
                            case "firstname":
                                existing.setFirstname(value);
                                continue;
                            case "middlename":
                                existing.setMiddlename(value);
                                continue;
                            case "lastname":
                                existing.setLastname(value);
                                continue;
                            case "companyname1":
                                existing.setCompanyName1(value);
                                continue;
                            case "companyname2":
                                existing.setCompanyName2(value);
                                continue;
                            case "companydepartment":
                                existing.setCompanyDepartment(value);
                                continue;
                        }
                    } else {

                        AttrValueCustomer attrVal = customer.getAttributeByCode(code);
                        if (attrVal != null) {
                            attrVal.setVal(value);
                        } else {
                            final Attribute attr = attributeService.findByAttributeCode(code);
                            if (attr != null) {
                                attrVal = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                                attrVal.setVal(value);
                                attrVal.setAttributeCode(attr.getCode());
                                attrVal.setCustomer(customer);
                                customer.getAttributes().add(attrVal);
                            }
                        }

                    }
                }

            }
        }
        if (!update.isEmpty()) {
            LOG.warn("Profile data contains read only or not allowed attribute(s) that were skipped: {}", update.keySet());
        }

        customerService.update(existing);
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerPublicKey(final Shop profileShop, final Customer customer) {
        if (customerService.isCustomerExists(customer.getLogin(), profileShop, false)) {
            if (StringUtils.isBlank(customer.getPublicKey())) {
                final String phrase = phraseGenerator.getNextPassPhrase();
                customer.setPublicKey(phrase);
                customerService.update(customer);
            }
            return customer.getPublicKey().concat("-").concat(customer.getLastname());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByPublicKey(final Shop profileShop, final String publicKey) {
        if (StringUtils.isNotBlank(publicKey)) {
            int lastDashPos = publicKey.lastIndexOf('-');
            final String key = publicKey.substring(0, lastDashPos);
            final String lastName = publicKey.substring(lastDashPos + 1);
            final Customer customer = customerService.getCustomerByPublicKey(key, lastName);
            if (customer != null && customerService.isCustomerExists(customer.getLogin(), profileShop, false)) {
                return customer;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByToken(final Shop profileShop, final String token) {
        final Customer customer = customerService.getCustomerByToken(token);
        if (customer != null && customerService.isCustomerExists(customer.getLogin(), profileShop, false)) {
            return customer;
        }
        return null;
    }

    public static class RegistrationResultImpl implements RegistrationResult {

        private final boolean duplicate;
        private final boolean success;
        private final String errorCode;
        private final Customer customer;
        private final String rawPassword;

        public RegistrationResultImpl(final boolean duplicate, final String errorCode, final Customer customer, final String rawPassword) {
            this.duplicate = duplicate;
            this.rawPassword = rawPassword;
            this.success = !duplicate && StringUtils.isBlank(errorCode);
            this.errorCode = errorCode;
            this.customer = customer;
        }

        @Override
        public boolean isDuplicate() {
            return duplicate;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public Customer getCustomer() {
            return customer;
        }

        @Override
        public String getRawPassword() {
            return rawPassword;
        }
    }

}
