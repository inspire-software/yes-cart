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

import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.MutableShoppingContext;

/**
 * User: denispavlov
 * Date: 03/02/2017
 * Time: 11:15
 */
public class ShoppingCartCommandConfigurationDefaultAddressVisitorImpl extends ShoppingCartCommandConfigurationCustomerTypeVisitorImpl {

    public ShoppingCartCommandConfigurationDefaultAddressVisitorImpl(final CustomerService customerService,
                                                                     final ShopService shopService) {
        super(customerService, shopService);
    }

    @Override
    public void visit(final MutableShoppingCart cart, final Object... args) {

        final Customer customer = determineCustomer(cart);
        final Shop shop = determineShop(cart);

        if (customer == null || shop == null) {
            return;
        }

        final Shop customerShop = determineCustomerShop(cart);
        final Address delivery = getCustomerDefaultAddress(customerShop, customer, Address.ADDR_TYPE_SHIPPING);
        final Address billing = getCustomerDefaultAddress(customerShop, customer, Address.ADDR_TYPE_BILLING);

        Address pricingAddress = null;
        final MutableOrderInfo info = cart.getOrderInfo();
        final MutableShoppingContext ctx = cart.getShoppingContext();

        final String customerType = ensureCustomerType(cart);
        final boolean forceSeparateAddresses = customerShop.isSfShowSameBillingAddressDisabledTypes(customerType);
        info.setSeparateBillingAddressEnabled(forceSeparateAddresses);
        if (forceSeparateAddresses) {
            info.setSeparateBillingAddress(true);
        }

        if (!info.isDeliveryAddressNotRequired() && delivery != null &&
                shop.getSupportedShippingCountriesAsList().contains(delivery.getCountryCode())) {
            info.setDeliveryAddressId(delivery.getAddressId());
            pricingAddress = delivery;
        }
        if (!info.isBillingAddressNotRequired() && (delivery != null || billing != null)) {
            if (billing != null &&
                    shop.getSupportedBillingCountriesAsList().contains(billing.getCountryCode())) {
                info.setBillingAddressId(billing.getAddressId());
                pricingAddress = billing;
            } else if (delivery != null &&
                    shop.getSupportedBillingCountriesAsList().contains(delivery.getCountryCode())) {
                info.setBillingAddressId(delivery.getAddressId());
            }
        }

        if (pricingAddress != null) {
            ctx.setCountryCode(pricingAddress.getCountryCode());
            ctx.setStateCode(pricingAddress.getStateCode());
        } else {
            ctx.setCountryCode(null);
            ctx.setStateCode(null);
        }

    }


    /**
     * Get default customer address for this shop.
     *
     * @param customerShop shop
     * @param customer customer
     * @param addrType type of address
     *
     * @return address to use
     */
    protected Address getCustomerDefaultAddress(final Shop customerShop, final Customer customer, final String addrType) {
        if (customer == null || customerShop == null) {
            return null;
        }
        return customer.getDefaultAddress(addrType);
    }


}
