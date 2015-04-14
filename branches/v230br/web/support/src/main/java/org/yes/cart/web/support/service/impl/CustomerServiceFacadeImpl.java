/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.springframework.cache.annotation.CacheEvict;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.PassPhrazeGenerator;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-25
 * Time: 7:03 PM
 */
public class CustomerServiceFacadeImpl implements CustomerServiceFacade {

    private final CustomerService customerService;
    private final CustomerWishListService customerWishListService;
    private final AttributeService attributeService;
    private final PassPhrazeGenerator phrazeGenerator;

    public CustomerServiceFacadeImpl(final CustomerService customerService,
                                     final CustomerWishListService customerWishListService,
                                     final AttributeService attributeService,
                                     final PassPhrazeGenerator phrazeGenerator) {
        this.customerService = customerService;
        this.customerWishListService = customerWishListService;
        this.attributeService = attributeService;
        this.phrazeGenerator = phrazeGenerator;
    }

    /** {@inheritDoc} */
    public boolean isCustomerRegistered(String email) {
        return customerService.isCustomerExists(email);
    }

    /** {@inheritDoc} */
    public Customer getCustomerByEmail(final String email) {
        return customerService.getCustomerByEmail(email);
    }

    /** {@inheritDoc} */
    public List<CustomerWishList> getCustomerWishListByEmail(final String email, final String type, final String... tags) {

        final List<CustomerWishList> allItems = customerWishListService.getWishListByCustomerEmail(email);

        final List<CustomerWishList> filtered = new ArrayList<CustomerWishList>();
        for (final CustomerWishList item : allItems) {

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
            }

            filtered.add(item);

        }

        return filtered;
    }

    /** {@inheritDoc} */
    public void resetPassword(final Shop shop, final Customer customer) {
        customerService.resetPassword(customer, shop);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, key = "#email")
    public String registerCustomer(Shop registrationShop, String email, Map<String, Object> registrationData) {

        final String password = phrazeGenerator.getNextPassPhrase();

        final Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);

        customer.setEmail(email);
        customer.setFirstname((String) registrationData.get("firstname"));
        customer.setLastname((String) registrationData.get("lastname"));
        customer.setPassword(password); // aspect will create hash

        final Map<String, Object> attrData = new HashMap<String, Object>(registrationData);
        attrData.remove("firstname");
        attrData.remove("lastname");
        attrData.put(AttributeNamesKeys.CUSTOMER_PHONE, attrData.remove("phone"));

        for (final Map.Entry<String, Object> attrVal : attrData.entrySet()) {

            if (attrVal.getValue() != null ||
                    (attrVal.getValue() instanceof String && StringUtils.isNotBlank((String) attrVal.getValue()))) {

                final Attribute attribute = attributeService.findByAttributeCode(attrVal.getKey());

                if (attribute != null) {

                    final AttrValueCustomer attrValueCustomer = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                    attrValueCustomer.setCustomer(customer);
                    attrValueCustomer.setVal(String.valueOf(attrVal.getValue()));
                    attrValueCustomer.setAttribute(attribute);

                    customer.getAttributes().add(attrValueCustomer);

                } else {

                    ShopCodeContext.getLog(this).warn("Registration data contains unknown attribute: {}", attrVal.getKey());

                }

            }

        }

        customerService.create(customer, registrationShop);

        return password;
    }

    /** {@inheritDoc} */
    public List<? extends AttrValue> getShopRegistrationAttributes(final Shop shop) {

        final List<String> allowed = shop.getSupportedRegistrationFormAttributesAsList();
        if (CollectionUtils.isEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            return Collections.emptyList();
        }

        final List<? extends AttrValue> attrValueCollection = customerService.getRankedAttributeValues(null);
        if (CollectionUtils.isEmpty(attrValueCollection)) {
            return Collections.emptyList();
        }

        final List<AttrValue> registration = new ArrayList<AttrValue>();
        final Map<String, AttrValue> map = new HashMap<String, AttrValue>(attrValueCollection.size());
        for (final AttrValue av : attrValueCollection) {
            map.put(av.getAttribute().getCode(), av);
        }
        for (final String code : allowed) {
            final AttrValue av = map.get(code);
            if (av != null) {
                registration.add(av);
            }
        }

        return registration;  // CPOINT - possibly need to filter some out
    }


    /** {@inheritDoc} */
    public List<Pair<? extends AttrValue, Boolean>> getCustomerProfileAttributes(final Shop shop, final Customer customer) {

        final List<String> allowed = shop.getSupportedProfileFormAttributesAsList();
        if (CollectionUtils.isEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            return Collections.emptyList();
        }

        final List<String> readonly = shop.getSupportedProfileFormReadOnlyAttributesAsList();

        final List<? extends AttrValue> attrValueCollection = customerService.getRankedAttributeValues(customer);
        if (CollectionUtils.isEmpty(attrValueCollection)) {
            return Collections.emptyList();
        }


        final List<Pair<? extends AttrValue, Boolean>> profile = new ArrayList<Pair<? extends AttrValue, Boolean>>();
        final Map<String, AttrValue> map = new HashMap<String, AttrValue>(attrValueCollection.size());
        for (final AttrValue av : attrValueCollection) {
            map.put(av.getAttribute().getCode(), av);
        }
        for (final String code : allowed) {
            final AttrValue av = map.get(code);
            if (av != null) {
                profile.add(new Pair<AttrValue, Boolean>(av, readonly.contains(code)));
            }
        }

        return profile;  // CPOINT - possibly need to filter some out
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "web.addressBookFacade-customerHasAtLeastOneAddress"
    }, key = "#customer.email")
    public void updateCustomer(final Customer customer) {
        customerService.update(customer);
    }

    /** {@inheritDoc} */
    public void updateCustomerAttributes(final Customer customer, final Map<String, String> values) {

        for (final Map.Entry<String, String> entry : values.entrySet()) {

            customerService.addAttribute(customer, entry.getKey(), entry.getValue());

        }

        customerService.update(customer);
    }

    /** {@inheritDoc} */
    public boolean authenticate(final String username, final String password) {
        return customerService.isCustomerExists(username) &&
                customerService.isPasswordValid(username, password);
    }
}
