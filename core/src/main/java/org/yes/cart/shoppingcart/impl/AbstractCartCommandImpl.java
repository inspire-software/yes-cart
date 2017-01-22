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
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.*;

import java.util.Arrays;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 13:16
 */
public abstract class AbstractCartCommandImpl implements ShoppingCartCommand {

    private int priority = 0;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    protected AbstractCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        registry.registerCommand(this);
    }

    public final void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        // OOTB we only have mutable cart
        execute((MutableShoppingCart) shoppingCart, parameters);
    }

    /**
     * Internal hook to switch to mutable cart.
     *
     * @param shoppingCart mutable cart
     * @param parameters   parameters
     */
    public abstract void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters);

    /**
     * Recalculate shopping cart.
     *
     * @param shoppingCart current cart
     */
    protected void recalculate(final MutableShoppingCart shoppingCart) {
        shoppingCart.recalculate();
    }

    /**
     * Mark shopping cart dirty and thus eligible for persistence.
     *
     * @param shoppingCart current cart
     */
    protected void markDirty(final MutableShoppingCart shoppingCart) {
        shoppingCart.markDirty();
    }


    /**
     * Default customer type determination.
     *
     * anonymous customers (null) - B2G
     * customers with undefined customer type - B2C
     * else as specified by customer.customerType
     *
     * @param customer customer or null for anonymous
     *
     * @return type (not null)
     */
    protected String getCurrentCustomerType(final Customer customer) {
        return customer == null ? "B2G" : (StringUtils.isBlank(customer.getCustomerType()) ? "B2C" : customer.getCustomerType());
    }

    /**
     * Set customer tax options.
     *
     * @param shop             current shop
     * @param customer         optional customer (can be null for anonymous)
     * @param showTaxOption    optional flag to show tax or not
     * @param showNetOption    optional flag to display net or gross prices
     * @param showAmountOption optional flag to show amount ot percent of tax
     * @param ctx              shopping cart context where to set these options
     */
    protected void setTaxOptions(final Shop shop,
                                 final Customer customer,
                                 final Boolean showTaxOption,
                                 final Boolean showNetOption,
                                 final Boolean showAmountOption,
                                 final MutableShoppingContext ctx) {

        // Resolve type. Anonymous type is B2G, blank is B2C
        final String customerType = getCurrentCustomerType(customer);
        boolean showTax = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO);
        if (showTax) {
            // If types limit is set then only enable showTax option for given types. Anonymous type is B2G, blank is B2C
            final String types = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CUSTOMER_TYPES);
            showTax = StringUtils.isBlank(types) || Arrays.asList(StringUtils.split(types, ',')).contains(customerType);
        }
        boolean showTaxNet = showTax && shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET);
        boolean showTaxAmount = showTax && shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT);

        ctx.setTaxInfoChangeViewEnabled(false);
        final String typesThatCanChangeView = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES);
        // Ensure change view is allowed for anonymous
        if (StringUtils.isNotBlank(typesThatCanChangeView)) {
            final String[] customerTypesThatCanChangeView = StringUtils.split(typesThatCanChangeView, ',');
            if (Arrays.asList(customerTypesThatCanChangeView).contains(customerType)) {
                ctx.setTaxInfoChangeViewEnabled(true);
            }
        }

        if (ctx.isTaxInfoChangeViewEnabled()) {
            showTax = showTaxOption != null ? showTaxOption : showTax;
            showTaxNet = showNetOption == null ? showTax && showTaxNet : showTax && showNetOption;
            showTaxAmount = showAmountOption == null ? showTax && showTaxAmount : showTax && showAmountOption;
        }

        ctx.setTaxInfoEnabled(showTax);
        ctx.setTaxInfoUseNet(showTaxNet);
        ctx.setTaxInfoShowAmount(showTaxAmount);

    }

    /**
     * Set default customer information that contributes to order info object.
     *
     * @param shop             current shop
     * @param customer         optional customer (can be null for anonymous)
     * @param info             order info object to populate
     */
    protected void setCustomerOptions(final Shop shop, final Customer customer, final MutableOrderInfo info) {

        final String customerType = getCurrentCustomerType(customer);

        boolean blockCheckout = shop.isSfBlockCustomerCheckout(customerType);
        boolean orderRequiresApproval = shop.isSfRequireCustomerOrderApproval(customerType);

        info.putDetail("customerType", customerType);
        info.putDetail("b2bRequireApprove", String.valueOf(orderRequiresApproval));
        info.putDetail("blockCheckout", String.valueOf(blockCheckout));

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
            if (!orderRequiresApproval) {
                final AttrValue orderRequiresApprovalCustomer =
                        customer.getAttributeByCode(AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE);
                info.putDetail("b2bRequireApprove", Boolean.valueOf(orderRequiresApprovalCustomer != null ? orderRequiresApprovalCustomer.getVal() : Boolean.FALSE.toString()).toString());
            }

            // Customer level block checkout
            if (!blockCheckout) {
                final AttrValue blockCheckoutCustomer =
                        customer.getAttributeByCode(AttributeNamesKeys.Customer.BLOCK_CHECKOUT);
                info.putDetail("blockCheckout", Boolean.valueOf(blockCheckoutCustomer != null ? blockCheckoutCustomer.getVal() : Boolean.FALSE.toString()).toString());
            }

        } else {

            info.putDetail("b2bRef", null);
            info.putDetail("b2bEmployeeId", null);
            info.putDetail("b2bChargeId", null);

        }

        // Ensure approved information is removed
        info.putDetail("b2bApprovedBy", null);
        info.putDetail("b2bApprovedDate", null);

    }

    /**
     * Set default customer address information if possible.
     *
     * Since customers could have defaults set to country which is not supported by shop billing/delivery address
     * a check is made for compatibility first.
     *
     * @param shop             current shop
     * @param customer         customer
     * @param cart             current cart
     */
    protected void setDefaultAddressesIfPossible(final Shop shop, final Customer customer, final MutableShoppingCart cart) {

        final Address delivery = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPPING);
        final Address billing = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);

        Address pricingAddress = null;
        final MutableOrderInfo info = cart.getOrderInfo();
        final MutableShoppingContext ctx = cart.getShoppingContext();

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



    /** {@inheritDoc} */
    public int getPriority() {
        return priority;
    }

    /**
     * IoC priority for command.
     *
     * @param priority see {@link #getPriority()}
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

}
