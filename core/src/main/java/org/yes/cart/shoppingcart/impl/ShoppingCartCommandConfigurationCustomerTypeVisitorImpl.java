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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:49
 */
public class ShoppingCartCommandConfigurationCustomerTypeVisitorImpl extends AbstractShoppingCartCommandConfigurationVisitorImpl {

    public ShoppingCartCommandConfigurationCustomerTypeVisitorImpl(final CustomerService customerService,
                                                                   final ShopService shopService) {
        super(customerService, shopService);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final MutableShoppingCart cart, final Object... args) {

        final Customer customer = determineCustomer(cart);
        final String customerType = determineType(customer);
        final String tags = determineTags(customer);
        cart.getOrderInfo().putDetail(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TYPE, customerType);
        cart.getOrderInfo().putDetail(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TAGS, tags);
        cart.getShoppingContext().setHidePrices(determineShop(cart).isSfHidePricesTypes(customerType));
    }

    /**
     * Extension hook for customer type.
     *
     * @param customer customer or null (anonymous)
     *
     * @return non null type
     */
    protected String determineType(final Customer customer) {
        return customer == null ?
                AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST :
                (StringUtils.isBlank(customer.getCustomerType()) ? AttributeNamesKeys.Cart.CUSTOMER_TYPE_REGULAR : customer.getCustomerType());
    }

    /**
     * Extension hook for customer tags.
     *
     * @param customer customer or null (anonymous)
     *
     * @return null or string of space separated tags
     */
    protected String determineTags(final Customer customer) {
        return customer == null || StringUtils.isBlank(customer.getTag()) ? null : customer.getTag();
    }


    /**
     * Get customer type from cart.
     *
     * @param cart shopping cart.
     *
     * @return customer type if set, null otherwise
     */
    protected String getCustomerType(final MutableShoppingCart cart) {
        return cart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TYPE);
    }


    /**
     * Get customer type from cart.
     *
     * @param cart shopping cart.
     *
     * @return customer type if set, null otherwise
     */
    protected String ensureCustomerType(final MutableShoppingCart cart) {
        String customerType = getCustomerType(cart);
        if (customerType == null) {

            customerType = determineType(determineCustomer(cart));
            cart.getOrderInfo().putDetail(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TYPE, customerType);

        }
        return customerType;
    }

}
