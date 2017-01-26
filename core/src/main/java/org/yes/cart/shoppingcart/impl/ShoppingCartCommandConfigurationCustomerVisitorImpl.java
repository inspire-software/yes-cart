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
public class ShoppingCartCommandConfigurationCustomerVisitorImpl extends ShoppingCartCommandConfigurationCustomerTypeVisitorImpl {

    public ShoppingCartCommandConfigurationCustomerVisitorImpl(final CustomerService customerService,
                                                               final ShopService shopService) {
        super(customerService, shopService);
    }


    /**
     * {@inheritDoc}
     */
    public void visit(final MutableShoppingCart cart, final Object... args) {

        final MutableOrderInfo info = cart.getOrderInfo();
        final Customer customer = determineCustomer(cart);

        final String customerType = ensureCustomerType(cart);
        final Shop shop = determineCustomerShop(cart);

        boolean blockCheckout = shop.isSfBlockCustomerCheckout(customerType);
        boolean orderRequiresApproval = shop.isSfRequireCustomerOrderApproval(customerType);

        info.putDetail("b2bRequireApproveType", String.valueOf(orderRequiresApproval));
        info.putDetail("blockCheckoutType", String.valueOf(blockCheckout));

        if (customer != null) {

            // default reference
            final AttrValue b2bRef = customer.getAttributeByCode(AttributeNamesKeys.Customer.B2B_REF);
            info.putDetail("b2bRef", b2bRef != null && StringUtils.isNotBlank(b2bRef.getVal()) ? b2bRef.getVal() : null);

            // Employee ID preset
            final AttrValue employeeId = customer.getAttributeByCode(AttributeNamesKeys.Customer.B2B_EMPLOYEE_ID);
            info.putDetail("b2bEmployeeId", employeeId != null && StringUtils.isNotBlank(employeeId.getVal()) ? employeeId.getVal() : null);

            // Charge ID preset
            final AttrValue b2bChargeId = customer.getAttributeByCode(AttributeNamesKeys.Customer.B2B_CHARGE_ID);
            info.putDetail("b2bChargeId", b2bChargeId != null && StringUtils.isNotBlank(b2bChargeId.getVal()) ? b2bChargeId.getVal() : null);

            // Customer level approve flag
            final AttrValue orderRequiresApprovalCustomer =
                    customer.getAttributeByCode(AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE);
            info.putDetail("b2bRequireApproveCustomer", Boolean.valueOf(orderRequiresApprovalCustomer != null ? orderRequiresApprovalCustomer.getVal() : Boolean.FALSE.toString()).toString());

            // Customer level block checkout
            final AttrValue blockCheckoutCustomer =
                    customer.getAttributeByCode(AttributeNamesKeys.Customer.BLOCK_CHECKOUT);
            info.putDetail("blockCheckoutCustomer", Boolean.valueOf(blockCheckoutCustomer != null ? blockCheckoutCustomer.getVal() : Boolean.FALSE.toString()).toString());

        } else {

            info.putDetail("b2bRef", null);
            info.putDetail("b2bEmployeeId", null);
            info.putDetail("b2bChargeId", null);

        }

        // Ensure approved information is removed
        info.putDetail("b2bApprovedBy", null);
        info.putDetail("b2bApprovedDate", null);


    }

}
