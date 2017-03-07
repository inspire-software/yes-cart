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
    private final CustomerWishListService customerWishListService;
    private final AttributeService attributeService;
    private final PassPhrazeGenerator phrazeGenerator;
    private final CustomerCustomisationSupport customerCustomisationSupport;

    public CustomerServiceFacadeImpl(final CustomerService customerService,
                                     final CustomerWishListService customerWishListService,
                                     final AttributeService attributeService,
                                     final PassPhrazeGenerator phrazeGenerator,
                                     final CustomerCustomisationSupport customerCustomisationSupport) {
        this.customerService = customerService;
        this.customerWishListService = customerWishListService;
        this.attributeService = attributeService;
        this.phrazeGenerator = phrazeGenerator;
        this.customerCustomisationSupport = customerCustomisationSupport;
    }

    /** {@inheritDoc} */
    public boolean isCustomerRegistered(final Shop shop, final String email) {
        return customerService.isCustomerExists(email, shop);
    }

    /** {@inheritDoc} */
    public Customer getCustomerByEmail(final Shop shop, final String email) {
        return customerService.getCustomerByEmail(email, shop);
    }

    /** {@inheritDoc} */
    public Customer getGuestByCart(final Shop shop, final ShoppingCart cart) {
        return customerService.getCustomerByEmail(cart.getGuid(), shop);
    }

    /** {@inheritDoc} */
    public Customer getCheckoutCustomer(final Shop shop, final ShoppingCart cart) {
        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
            return getCustomerByEmail(shop, cart.getCustomerEmail());
        }
        return getGuestByCart(shop, cart);
    }

    /** {@inheritDoc} */
    public List<CustomerWishList> getCustomerWishListByEmail(final Shop shop, final String type, final String email,final String visibility, final String... tags) {

        final List<CustomerWishList> filtered = new ArrayList<CustomerWishList>();
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
    public void resetPassword(final Shop shop, final Customer customer) {
        customerService.resetPassword(customer, shop, null);
    }

    /** {@inheritDoc} */
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
        customer.setSalutation((String) registrationData.get("salutation"));
        customer.setFirstname((String) registrationData.get("firstname"));
        customer.setLastname((String) registrationData.get("lastname"));
        customer.setMiddlename((String) registrationData.get("middlename"));
        customer.setCustomerType(customerType);

        if (StringUtils.isBlank(customer.getEmail()) ||
                StringUtils.isBlank(customer.getFirstname()) ||
                StringUtils.isBlank(customer.getLastname()) ||
                StringUtils.isBlank(customer.getCustomerType())) {
            LOG.warn("Missing required registration data, please check that registration details have sufficient data");
            return null;
        }

        final String password = phrazeGenerator.getNextPassPhrase();
        customer.setPassword(password); // aspect will create hash but we need to generate password to be able to auto-login

        registerCustomerCustomAttributes(customer, customerType, configShop, registrationData);

        customerService.create(customer, registrationShop);

        return password; // email is sent via RegistrationAspect
    }

    private void registerCustomerCustomAttributes(final Customer customer, final String customerType, final Shop configShop, final Map<String, Object> registrationData) {
        final Map<String, Object> attrData = new HashMap<String, Object>(registrationData);
        attrData.remove("salutation");
        attrData.remove("firstname");
        attrData.remove("lastname");
        attrData.remove("middlename");
        attrData.remove("customerType");
        if (attrData.containsKey("phone") && !attrData.containsKey(AttributeNamesKeys.Customer.CUSTOMER_PHONE)) {
            attrData.put(AttributeNamesKeys.Customer.CUSTOMER_PHONE, attrData.remove("phone"));
        }

        final List<String> allowed = customerCustomisationSupport.getSupportedRegistrationFormAttributesAsList(configShop, customerType);
        final List<String> allowedFull = new ArrayList<String>();
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
                        attrValueCustomer.setAttribute(attribute);

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

        registerCustomerCustomAttributes(customer, customerType, registrationShop, registrationData);

        customerService.create(customer, registrationShop);

        return customer.getEmail();
    }


    /** {@inheritDoc} */
    public String registerNewsletter(final Shop registrationShop,
                                     final String email,
                                     final Map<String, Object> registrationData) {
        return email; // do nothing, email is sent via NewsletterAspect
    }

    /** {@inheritDoc} */
    public String registerEmailRequest(final Shop registrationShop,
                                       final String email,
                                       final Map<String, Object> registrationData) {
        return email; // do nothing, email is sent via ContactFormAspect
    }

    /** {@inheritDoc} */
    public List<Pair<String, I18NModel>> getShopSupportedCustomerTypes(final Shop shop) {

        return customerCustomisationSupport.getSupportedCustomerTypes(shop);

    }

    /** {@inheritDoc} */
    public boolean isShopGuestCheckoutSupported(final Shop shop) {

        return customerCustomisationSupport.isGuestCheckoutSupported(shop);

    }

    /** {@inheritDoc} */
    public boolean isShopCustomerTypeSupported(final Shop shop, final String customerType) {

        return customerCustomisationSupport.isCustomerTypeSupported(shop, customerType);

    }

    /** {@inheritDoc} */
    public List<AttrValueCustomer> getShopRegistrationAttributes(final Shop shop, final String customerType) {

        final List<AttrValueCustomer> registration = customerCustomisationSupport.getRegistrationAttributes(shop, customerType);

        return registration;  // CPOINT - possibly need to filter some out
    }


    /** {@inheritDoc} */
    public List<Pair<AttrValueCustomer, Boolean>> getCustomerProfileAttributes(final Shop shop, final Customer customer) {

        final List<Pair<AttrValueCustomer, Boolean>> profile = customerCustomisationSupport.getProfileAttributes(shop, customer);

        return profile;  // CPOINT - possibly need to filter some out
    }

    /** {@inheritDoc} */
    public void updateCustomer(final Shop shop, final Customer customer) {
        customerService.update(customer);
    }

    /** {@inheritDoc} */
    public void updateCustomerAttributes(final Shop profileShop, final Customer customer, final Map<String, String> values) {

        final List<String> allowed = customerCustomisationSupport.getSupportedProfileFormAttributesAsList(profileShop, customer.getCustomerType());

        if (CollectionUtils.isNotEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            final List<String> readonly = new ArrayList<String>(customerCustomisationSupport.getSupportedProfileFormReadOnlyAttributesAsList(profileShop, customer.getCustomerType()));
            // Ensure dummy attributes are not updated
            readonly.addAll(Arrays.asList("salutation", "firstname", "middlename", "lastname"));

            for (final Map.Entry<String, String> entry : values.entrySet()) {

                if (allowed.contains(entry.getKey())) {

                    if (readonly.contains(entry.getKey())) {

                        LOG.warn("Profile data contains attribute that is read only: {}", entry.getKey());

                    } else {

                        customerService.addAttribute(customer, entry.getKey(), entry.getValue());

                    }

                } else {

                    LOG.warn("Profile data contains attribute that is not allowed: {}", entry.getKey());

                }

            }
        }

        customerService.update(customer);
    }

    /** {@inheritDoc} */
    public String getCustomerPublicKey(final Customer customer) {
        if (StringUtils.isBlank(customer.getPublicKey())) {
            final String phrase = phrazeGenerator.getNextPassPhrase();
            customer.setPublicKey(phrase);
            customerService.update(customer);
        }
        return customer.getPublicKey().concat("-").concat(customer.getLastname());
    }

    /** {@inheritDoc} */
    public Customer getCustomerByPublicKey(final String publicKey) {
        if (StringUtils.isNotBlank(publicKey)) {
            int lastDashPos = publicKey.lastIndexOf('-');
            final String key = publicKey.substring(0, lastDashPos);
            final String lastName = publicKey.substring(lastDashPos + 1);
            return customerService.getCustomerByPublicKey(key, lastName);
        }
        return null;
    }
}
