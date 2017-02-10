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

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingCart;

/**
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:49
 */
public class ShoppingCartCommandConfigurationCheckoutVisitorImpl extends ShoppingCartCommandConfigurationCustomerTypeVisitorImpl {

    public ShoppingCartCommandConfigurationCheckoutVisitorImpl(final CustomerService customerService,
                                                               final ShopService shopService) {
        super(customerService, shopService);
    }


    /**
     * {@inheritDoc}
     */
    public void visit(final MutableShoppingCart cart, final Object... args) {

        final MutableOrderInfo info = cart.getOrderInfo();

        boolean blockCheckout = info.isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT_TYPE) ||
                info.isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT_CUSTOMER);
        boolean orderRequiresApproval = info.isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER_TYPE) ||
                info.isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER_CUSTOMER);

        info.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER, String.valueOf(orderRequiresApproval));
        info.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT, String.valueOf(blockCheckout));

    }

}
