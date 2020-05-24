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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.CustomerResolver;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandConfigurationVisitor;

/**
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:50
 */
public abstract class AbstractShoppingCartCommandConfigurationVisitorImpl implements ShoppingCartCommandConfigurationVisitor<MutableShoppingCart> {

    private final CustomerResolver customerResolver;
    private final ShopService shopService;

    private String visitorId;

    protected AbstractShoppingCartCommandConfigurationVisitorImpl(final CustomerResolver customerResolver,
                                                                  final ShopService shopService) {
        this.customerResolver = customerResolver;
        this.shopService = shopService;
    }


    @Override
    public String getId() {
        return visitorId;
    }

    public void setVisitorId(final String visitorId) {
        this.visitorId = visitorId;
    }

    /**
     * Determine customer for current cart.
     *
     * @param cart cart
     *
     * @return customer object or null for anonymous
     */
    protected Customer determineCustomer(final MutableShoppingCart cart) {
        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
            return customerResolver.getCustomerByEmail(cart.getCustomerEmail(), determineCustomerShop(cart));
        }
        return null;
    }

    /**
     * Determine manager for current cart.
     *
     * @param cart cart
     *
     * @return customer object or null for anonymous
     */
    protected Customer determineManager(final MutableShoppingCart cart) {
        if (customerResolver.isManagerLoginEnabled(determineCustomerShop(cart))) {
            if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
                if (cart.getShoppingContext().isManagedCart()) {
                    return customerResolver.getCustomerByEmail(cart.getShoppingContext().getManagerEmail(), determineCustomerShop(cart));
                }
                final Customer customer = customerResolver.getCustomerByEmail(cart.getCustomerEmail(), determineCustomerShop(cart));
                if (customer != null && AttributeNamesKeys.Cart.CUSTOMER_TYPE_MANAGER.equals(customer.getCustomerType())) {
                    return customer;
                }
            }
        }
        return null;
    }

    /**
     * Determine shop for current cart.
     *
     * @param cart cart
     *
     * @return current shop
     */
    protected Shop determineShop(final MutableShoppingCart cart) {
        return shopService.getById(cart.getShoppingContext().getShopId());
    }

    /**
     * Determine shop for current cart.
     *
     * @param cart cart
     *
     * @return current shop
     */
    protected Shop determineCustomerShop(final MutableShoppingCart cart) {
        return shopService.getById(cart.getShoppingContext().getCustomerShopId());
    }

}
