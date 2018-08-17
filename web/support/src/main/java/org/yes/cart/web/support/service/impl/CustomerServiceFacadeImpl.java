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
    public boolean isCustomerRegistered(final Shop shop, final String email) {
        return customerService.isCustomerExists(email, shop);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByEmail(final Shop shop, final String email) {
        return customerService.getCustomerByEmail(email, shop);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getGuestByCart(final Shop shop, final ShoppingCart cart) {
        return customerService.getCustomerByEmail(cart.getGuid(), shop);
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCheckoutCustomer(final Shop shop, final ShoppingCart cart) {
        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
            return getCustomerByEmail(shop, cart.getCustomerEmail());
        }
        return getGuestByCart(shop, cart);
    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerWishList> getCustomerWishListByEmail(final Shop shop, final String type, final String email, final String visibility, final String... tags) {

        final List<CustomerWishList> filtered = new ArrayList<>();
        final Customer customer = customerService.getCustomerByEmail(email, shop);
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
    public String registerCustomer(Shop registrationShop, String email, Map<String, Object> registrationData) {

        final Object customerTypeData = registrationData.get("customerType");
        final String customerType = customerTypeData != null ? String.valueOf(customerTypeData) : null;

        final Shop configShop = registrationShop.getMaster() != null ? registrationShop.getMaster() : registrationShop;
        final Set<String> types = customerCustomisationSupport.getCustomerTypes(configShop, false);
        if (!types.contains(customerType)) {
            LOG.warn("SHOP_CUSTOMER_TYPES does not contain '{}' customer type or registrationData does not have 'customerType'", customerType);
            return null;
        }

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(email);
        customer.setCustomerType(customerType);

        final Map<String, String> addressFormParamKeys = mapAddressFormParameters(customer, customerType, configShop, registrationData);

        // Allow fallback, so that we do not need to repeat fields if we include address form
        customer.setSalutation(getFallbackParameter(registrationData, "salutation", addressFormParamKeys.get("salutation")));
        customer.setFirstname(getFallbackParameter(registrationData, "firstname", addressFormParamKeys.get("firstname")));
        customer.setLastname(getFallbackParameter(registrationData, "lastname", addressFormParamKeys.get("lastname")));
        customer.setMiddlename(getFallbackParameter(registrationData, "middlename", addressFormParamKeys.get("middlename")));
        customer.setCompanyName1(getFallbackParameter(registrationData, "companyname1", addressFormParamKeys.get("companyName1")));
        customer.setCompanyName2(getFallbackParameter(registrationData, "companyname2", addressFormParamKeys.get("companyName2")));
        customer.setCompanyDepartment(getFallbackParameter(registrationData, "companydepartment", addressFormParamKeys.get("companyDepartment")));

        if (StringUtils.isBlank(customer.getEmail()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname()) ||
                StringUtils.isBlank(customer.getCustomerType())) {
            LOG.warn("Missing required registration data, please check that registration details have sufficient data");
            return null;
        }

        final String userPassword = (String) registrationData.get("password");
        final String password = StringUtils.isNotBlank(userPassword) ? userPassword : phraseGenerator.getNextPassPhrase();
        customer.setPassword(password); // aspect will create hash but we need to generate password to be able to auto-login
        customer.setPasswordExpiry(null); // TODO: YC-906 Create password expiry flow for customers

        registerCustomerAddress(customer, customerType, configShop, registrationData, addressFormParamKeys);

        registerCustomerCustomAttributes(customer, customerType, configShop, registrationData, addressFormParamKeys);

        customerService.create(customer, registrationShop);

        return password; // email is sent via RegistrationAspect
    }

    private String getFallbackParameter(final Map<String, Object> registrationData, final String key1, final String key2) {
        final String paramValue = (String) registrationData.get(key1);
        if (StringUtils.isNotBlank(paramValue)) {
            return paramValue;
        }
        return (String) registrationData.get(key2);
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

            final String addressType = Address.ADDR_TYPE_SHIPPING;

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

    private void registerCustomerCustomAttributes(final Customer customer,
                                                  final String customerType,
                                                  final Shop configShop,
                                                  final Map<String, Object> registrationData,
                                                  final Map<String, String> addressFormParamKeys) {

        final Map<String, Object> attrData = new HashMap<>(registrationData);
        attrData.remove("email");
        attrData.remove("salutation");
        attrData.remove("firstname");
        attrData.remove("lastname");
        attrData.remove("middlename");
        attrData.remove("customerType");
        attrData.remove("password");
        attrData.remove("confirmPassword");
        attrData.remove("companyname1");
        attrData.remove("companyname2");
        attrData.remove("companydepartment");
        final String phone = getFallbackParameter(attrData, "phone", addressFormParamKeys.get("phone1"));
        if (StringUtils.isNotBlank(phone) && !attrData.containsKey(AttributeNamesKeys.Customer.CUSTOMER_PHONE)) {
            attrData.put(AttributeNamesKeys.Customer.CUSTOMER_PHONE, phone);
            attrData.remove("phone");
        }
        // remove included address form parameters
        attrData.entrySet().removeIf(entry -> entry.getKey().startsWith("regAddressForm."));

        final List<String> allowed = customerCustomisationSupport.getSupportedRegistrationFormAttributesAsList(configShop, customerType);
        final List<String> allowedFull = new ArrayList<>();
        allowedFull.addAll(allowed);
        allowedFull.add(AttributeNamesKeys.Customer.CUSTOMER_PHONE);

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

                        LOG.warn("Registration data contains unknown attribute: {}", attrVal.getKey());

                    }

                } else {

                    LOG.warn("Registration data contains attribute that is not allowed: {}", attrVal.getKey());

                }

            }

        }
    }


    /** {@inheritDoc} */
    @Override
    public String registerGuest(Shop registrationShop, String email, Map<String, Object> registrationData) {

        final String customerType = GUEST_TYPE;

        final Set<String> types = customerCustomisationSupport.getCustomerTypes(registrationShop, true);
        if (!types.contains(customerType)) {
            LOG.warn("SHOP_CHECKOUT_ENABLE_GUEST is not enabled");
            return null;
        }

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);

        customer.setEmail((String) registrationData.get("cartGuid"));
        customer.setGuest(true);
        customer.setGuestEmail(email);
        customer.setSalutation((String) registrationData.get("salutation"));
        customer.setFirstname((String) registrationData.get("firstname"));
        customer.setLastname((String) registrationData.get("lastname"));
        customer.setMiddlename((String) registrationData.get("middlename"));
        customer.setCompanyName1((String) registrationData.get("companyname1"));
        customer.setCompanyName2((String) registrationData.get("companyname2"));
        customer.setCompanyDepartment((String) registrationData.get("companydepartment"));
        customer.setCustomerType(customerType);

        if (StringUtils.isBlank(customer.getEmail()) ||
                StringUtils.isBlank(customer.getGuestEmail()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname())) {
            LOG.warn("Missing required guest data, please check that registration details have sufficient data");
            return null;
        }

        final Customer existingGuest = customerService.getCustomerByEmail(customer.getEmail(), registrationShop);
        if (existingGuest != null) {
            // All existing guests will be cleaned up by cron job
            existingGuest.setEmail(UUID.randomUUID().toString() + "-expired");
            customerService.update(existingGuest);
        }

        customer.setPassword(UUID.randomUUID().toString()); // make sure we have complex value

        /*
            Below code is for custom attributes, which in theory will not have any effect as this account
            will be deleted after the order is placed.

            However custom implementations may need some custom attributes in checkout process
            so will leave this.
         */

        registerCustomerCustomAttributes(customer, customerType, registrationShop, registrationData, Collections.emptyMap());

        customerService.create(customer, registrationShop);

        return customer.getEmail();
    }


    /** {@inheritDoc} */
    @Override
    public String registerNewsletter(final Shop registrationShop,
                                     final String email,
                                     final Map<String, Object> registrationData) {
        return email; // do nothing, email is sent via NewsletterAspect
    }

    /** {@inheritDoc} */
    @Override
    public String registerEmailRequest(final Shop registrationShop,
                                       final String email,
                                       final Map<String, Object> registrationData) {
        return email; // do nothing, email is sent via ContactFormAspect
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
    public void updateCustomer(final Shop shop, final Customer customer) {
        customerService.update(customer);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCustomerAttributes(final Shop profileShop, final Customer customer, final Map<String, String> values) {

        final List<String> allowed = customerCustomisationSupport.getSupportedProfileFormAttributesAsList(profileShop, customer.getCustomerType());

        if (CollectionUtils.isNotEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            final List<String> readonly = new ArrayList<>(customerCustomisationSupport.getSupportedProfileFormReadOnlyAttributesAsList(profileShop, customer.getCustomerType()));
            // Ensure dummy attributes are not updated
            readonly.addAll(Arrays.asList("salutation", "firstname", "middlename", "lastname", "companyname1", "companyname2", "companydepartment"));

            for (final Map.Entry<String, String> entry : values.entrySet()) {

                if (allowed.contains(entry.getKey())) {

                    if (readonly.contains(entry.getKey())) {

                        LOG.warn("Profile data contains attribute that is read only: {}", entry.getKey());

                    } else {

                        if (StringUtils.isNotBlank(entry.getValue())) {
                            AttrValueCustomer attrVal = customer.getAttributeByCode(entry.getKey());
                            if (attrVal != null) {
                                attrVal.setVal(entry.getValue());
                            } else {
                                final Attribute attr = attributeService.findByAttributeCode(entry.getKey());
                                if (attr != null) {
                                    attrVal = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                                    attrVal.setVal(entry.getValue());
                                    attrVal.setAttributeCode(attr.getCode());
                                    attrVal.setCustomer(customer);
                                    customer.getAttributes().add(attrVal);
                                }
                            }
                        }

                    }

                } else {

                    LOG.warn("Profile data contains attribute that is not allowed: {}", entry.getKey());

                }

            }
        }

        customerService.update(customer);
    }

    /** {@inheritDoc} */
    @Override
    public String getCustomerPublicKey(final Customer customer) {
        if (StringUtils.isBlank(customer.getPublicKey())) {
            final String phrase = phraseGenerator.getNextPassPhrase();
            customer.setPublicKey(phrase);
            customerService.update(customer);
        }
        return customer.getPublicKey().concat("-").concat(customer.getLastname());
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByPublicKey(final String publicKey) {
        if (StringUtils.isNotBlank(publicKey)) {
            int lastDashPos = publicKey.lastIndexOf('-');
            final String key = publicKey.substring(0, lastDashPos);
            final String lastName = publicKey.substring(lastDashPos + 1);
            return customerService.getCustomerByPublicKey(key, lastName);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Customer getCustomerByToken(final String token) {
        return customerService.getCustomerByToken(token);
    }
}
