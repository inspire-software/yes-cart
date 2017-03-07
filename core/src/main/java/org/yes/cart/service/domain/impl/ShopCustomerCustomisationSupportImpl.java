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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerCustomisationSupport;
import org.yes.cart.service.domain.CustomerService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 12:40
 */
public class ShopCustomerCustomisationSupportImpl implements CustomerCustomisationSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCustomerCustomisationSupportImpl.class);

    private static final String GUEST_TYPE = AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST;

    private final CustomerService customerService;

    public ShopCustomerCustomisationSupportImpl(final CustomerService customerService) {
        this.customerService = customerService;
    }


    /** {@inheritDoc} */
    public String getGuestCustomerType(final Shop shop) {
        return GUEST_TYPE;
    }

    /** {@inheritDoc} */
    public Set<String> getCustomerTypes(final Shop shop, final boolean includeGuest) {

        final AttrValue types = getShopCustomerTypes(shop);
        final Set<String> codes = new HashSet<String>();
        if (types != null && StringUtils.isNotBlank(types.getVal())) {
            for (final String code : StringUtils.split(types.getVal(), ',')) {
                codes.add(code.trim());
            }
        }

        if (includeGuest && isGuestCheckoutSupported(shop)) {
            codes.add(getGuestCustomerType(shop));
        }
        return codes;

    }

    private AttrValue getShopCustomerTypes(Shop shop) {
        return shop.getAttributeByCode(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES);
    }


    /** {@inheritDoc} */
    public List<Pair<String, I18NModel>> getSupportedCustomerTypes(final Shop shop) {

        final AttrValue av = getShopCustomerTypes(shop);
        if (av != null && StringUtils.isNotBlank(av.getVal())) {

            final String[] types = StringUtils.split(av.getVal(), ',');

            final I18NModel model = new StringI18NModel(av.getDisplayVal());
            final Map<String, String[]> values = new HashMap<String, String[]>();
            for (final Map.Entry<String, String> displayValues : model.getAllValues().entrySet()) {
                values.put(displayValues.getKey(), StringUtils.split(displayValues.getValue(), ','));
            }

            final List<Pair<String, I18NModel>> out = new ArrayList<Pair<String, I18NModel>>(types.length);
            for (int i = 0; i < types.length; i++) {
                final String type = types[i].trim();
                final Map<String, String> names = new HashMap<String, String>();
                for (final Map.Entry<String, String[]> entry : values.entrySet()) {
                    if (entry.getValue().length > i) {
                        names.put(entry.getKey(), entry.getValue()[i]);
                    }
                }
                out.add(new Pair<String, I18NModel>(type, new FailoverStringI18NModel(names, type)));
            }
            return out;
        }
        return Collections.emptyList();
    }



    /** {@inheritDoc} */
    public boolean isGuestCheckoutSupported(final Shop shop) {
        final String val = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_CHECKOUT_ENABLE_GUEST);
        return val != null && Boolean.valueOf(val);
    }

    /** {@inheritDoc} */
    public boolean isCustomerTypeSupported(final Shop shop, final String customerType) {
        return getCustomerTypes(shop, false).contains(customerType);
    }

    /** {@inheritDoc} */
    public List<String> getSupportedRegistrationFormAttributesAsList(final Shop shop, final String customerType) {
        return shop.getSupportedRegistrationFormAttributesAsList(customerType);
    }

    /** {@inheritDoc} */
    public List<AttrValueCustomer> getRegistrationAttributes(final Shop shop, final String customerType) {

        final Set<String> types = getCustomerTypes(shop, true);
        if (!types.contains(customerType)) {
            LOG.warn("SHOP_CUSTOMER_TYPES does not contain '{}' customer type", customerType);
            return Collections.emptyList();
        }

        final List<String> allowed = getSupportedRegistrationFormAttributesAsList(shop, customerType);
        if (CollectionUtils.isEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            return Collections.emptyList();
        }

        final List<AttrValueCustomer> attrValueCollection = customerService.getRankedAttributeValues(null);
        if (CollectionUtils.isEmpty(attrValueCollection)) {
            return Collections.emptyList();
        }

        final List<AttrValueCustomer> registration = new ArrayList<AttrValueCustomer>();
        final Map<String, AttrValueCustomer> map = new HashMap<String, AttrValueCustomer>(attrValueCollection.size());
        for (final AttrValueCustomer av : attrValueCollection) {
            map.put(av.getAttribute().getCode(), av);
        }
        for (final String code : allowed) {
            final AttrValueCustomer av = map.get(code);
            if (av != null) {
                registration.add(av);
            }
        }

        return registration;
    }

    /** {@inheritDoc} */
    public List<String> getSupportedProfileFormAttributesAsList(final Shop shop, final String customerType) {
        return shop.getSupportedProfileFormAttributesAsList(customerType);
    }

    /** {@inheritDoc} */
    public List<String> getSupportedProfileFormReadOnlyAttributesAsList(final Shop shop, final String customerType) {
        return shop.getSupportedProfileFormReadOnlyAttributesAsList(customerType);
    }

    /** {@inheritDoc} */
    public List<Pair<AttrValueCustomer, Boolean>> getProfileAttributes(final Shop shop, final Customer customer) {

        final List<String> allowed = getSupportedProfileFormAttributesAsList(shop, customer.getCustomerType());
        if (CollectionUtils.isEmpty(allowed)) {
            // must explicitly configure to avoid exposing personal data
            return Collections.emptyList();
        }

        final List<String> readonly = getSupportedProfileFormReadOnlyAttributesAsList(shop, customer.getCustomerType());

        final List<AttrValueCustomer> attrValueCollection = customerService.getRankedAttributeValues(customer);
        if (CollectionUtils.isEmpty(attrValueCollection)) {
            return Collections.emptyList();
        }


        final List<Pair<AttrValueCustomer, Boolean>> profile = new ArrayList<Pair<AttrValueCustomer, Boolean>>();
        final Map<String, AttrValueCustomer> map = new HashMap<String, AttrValueCustomer>(attrValueCollection.size());
        for (final AttrValueCustomer av : attrValueCollection) {
            map.put(av.getAttribute().getCode(), av);
            if ("salutation".equals(av.getAttribute().getCode())) {
                av.setVal(customer.getSalutation());
            } else if ("firstname".equals(av.getAttribute().getCode())) {
                av.setVal(customer.getFirstname());
            } else if ("middlename".equals(av.getAttribute().getCode())) {
                av.setVal(customer.getMiddlename());
            } else if ("lastname".equals(av.getAttribute().getCode())) {
                av.setVal(customer.getLastname());
            }
        }
        for (final String code : allowed) {
            final AttrValueCustomer av = map.get(code);
            if (av != null) {
                profile.add(new Pair<AttrValueCustomer, Boolean>(av, readonly.contains(code)));
            }
        }

        return profile;
    }

}
