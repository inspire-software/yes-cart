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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.PricingPolicyProvider;

/**
 * User: denispavlov
 * Date: 07/06/2016
 * Time: 18:39
 */
public class PricingPolicyProviderImpl implements PricingPolicyProvider {

    private static final PricingPolicy DEFAULT = new PricingPolicyImpl(null, PricingPolicy.Type.DEFAULT);

    private final CustomerService customerService;
    private final ShopService shopService;

    public PricingPolicyProviderImpl(final CustomerService customerService,
                                     final ShopService shopService) {
        this.customerService = customerService;
        this.shopService = shopService;
    }

    private static class PricingPolicyImpl implements PricingPolicy {

        private final String id;
        private final Type type;

        public PricingPolicyImpl(final String id, final Type type) {
            this.id = id;
            this.type = type;
        }

        /** {@inheritDoc} */
        public String getID() {
            return id;
        }

        /** {@inheritDoc} */
        public Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return "PricingPolicyImpl{" +
                    "id='" + id + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    /** {@inheritDoc} */
    @Cacheable(value = "priceService-determinePricingPolicy")
    public PricingPolicy determinePricingPolicy(final String shopCode, final String currency, final String customerEmail, String countryCode, String stateCode) {

        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {

            if (StringUtils.isNotBlank(customerEmail)) {
                final Customer customer = customerService.getCustomerByEmail(customerEmail, shop);
                if (customer != null && StringUtils.isNotBlank(customer.getPricingPolicy())) {
                    return new PricingPolicyImpl(customer.getPricingPolicy(), PricingPolicy.Type.CUSTOMER);
                }
            }

            if (StringUtils.isNotBlank(countryCode)) {

                if (StringUtils.isNotBlank(stateCode)) {

                    final String statePolicy = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_REGIONAL_PRICING_PREFIX + countryCode + "_" + stateCode);
                    if (StringUtils.isNotBlank(statePolicy)) {
                        return new PricingPolicyImpl(statePolicy, PricingPolicy.Type.STATE);
                    }

                }

                final String countryPolicy = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_REGIONAL_PRICING_PREFIX + countryCode);
                if (StringUtils.isNotBlank(countryPolicy)) {
                    return new PricingPolicyImpl(countryPolicy, PricingPolicy.Type.COUNTRY);
                }

            }

        }
        return DEFAULT;
    }

}
