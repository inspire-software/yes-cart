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
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LoginCommandImpl extends AbstractRecalculatePriceCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    private final CustomerService customerService;
    private final ShopService shopService;

    /**
     * Construct command.
     * @param registry shopping cart command registry
     * @param customerService customer service
     * @param shopService shop service
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     */
    public LoginCommandImpl(final ShoppingCartCommandRegistry registry,
                            final CustomerService customerService,
                            final ShopService shopService,
                            final PriceService priceService,
                            final PricingPolicyProvider pricingPolicyProvider,
                            final ProductService productService) {
        super(registry, priceService, pricingPolicyProvider, productService, shopService);
        this.customerService = customerService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_LOGIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String email = (String) parameters.get(CMD_LOGIN_P_EMAIL);
            final String passw = (String) parameters.get(CMD_LOGIN_P_PASS);

            final String shopCode = shoppingCart.getShoppingContext().getShopCode();
            final Shop current = shopService.getShopByCode(shopCode);

            final MutableShoppingContext ctx = shoppingCart.getShoppingContext();
            final MutableOrderInfo info = shoppingCart.getOrderInfo();
            if (current != null && authenticate(email, current, passw)) {
                final Customer customer = customerService.getCustomerByEmail(email, current);
                final List<String> customerShops = new ArrayList<String>();
                for (final Shop shop : customerService.getCustomerShops(customer)) {
                    customerShops.add(shop.getCode());
                }

                ctx.setCustomerEmail(customer.getEmail());
                ctx.setCustomerName(customerService.formatNameFor(customer, current));
                ctx.setCustomerShops(customerShops);
                setDefaultAddressesIfNecessary(shoppingCart, customer);
                setDefaultTaxOptions(current, customer, ctx);
                setDefaultB2BOptions(current, customer, info);

                recalculatePricesInCart(shoppingCart);
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            } else {
                shoppingCart.getShoppingContext().clearContext();
                setDefaultTaxOptions(current, null, ctx);
                setDefaultB2BOptions(current, null, info);
                markDirty(shoppingCart);
            }
        }
    }

    private void setDefaultAddressesIfNecessary(final MutableShoppingCart shoppingCart, final Customer customer) {
        if (!shoppingCart.getOrderInfo().isBillingAddressNotRequired()
                || !shoppingCart.getOrderInfo().isDeliveryAddressNotRequired()) {

            final Address delivery = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPPING);
            final Address billing = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);

            if (!shoppingCart.getOrderInfo().isDeliveryAddressNotRequired() && delivery != null) {
                shoppingCart.getOrderInfo().setDeliveryAddressId(delivery.getAddressId());
            }
            if (!shoppingCart.getOrderInfo().isDeliveryAddressNotRequired() && (delivery != null || billing != null)) {
                if (billing != null) {
                    shoppingCart.getOrderInfo().setBillingAddressId(billing.getAddressId());
                    shoppingCart.getShoppingContext().setCountryCode(billing.getCountryCode());
                    shoppingCart.getShoppingContext().setStateCode(billing.getStateCode());
                } else {
                    shoppingCart.getOrderInfo().setBillingAddressId(delivery.getAddressId());
                    shoppingCart.getShoppingContext().setCountryCode(delivery.getCountryCode());
                    shoppingCart.getShoppingContext().setStateCode(delivery.getStateCode());
                }
            } else {
                shoppingCart.getShoppingContext().setCountryCode(null);
                shoppingCart.getShoppingContext().setStateCode(null);
            }
        }
    }


    protected void setDefaultTaxOptions(final Shop shop, final Customer customer, final MutableShoppingContext ctx) {

        // If types limit is set then only enable showTax option for given types. Anonymous type is B2G, blank is B2C
        final String customerType = getCurrentCustomerType(customer);

        boolean showTax = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO);
        if (showTax) {
            final String types = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CUSTOMER_TYPES);
            showTax = StringUtils.isBlank(types) || Arrays.asList(StringUtils.split(types, ',')).contains(customerType);
        }
        final boolean showTaxNet = showTax && shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET);
        final boolean showTaxAmount = showTax && shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT);

        ctx.setTaxInfoChangeViewEnabled(false);
        final String typesThatCanChangeView = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES);
        // Ensure change view is allowed for anonymous
        if (StringUtils.isNotBlank(typesThatCanChangeView)) {
            final String[] customerTypesThatCanChangeView = StringUtils.split(typesThatCanChangeView, ',');
            if (Arrays.asList(customerTypesThatCanChangeView).contains(customerType)) {
                ctx.setTaxInfoChangeViewEnabled(true);
            }
        }

        ctx.setTaxInfoEnabled(showTax);
        ctx.setTaxInfoUseNet(showTaxNet);
        ctx.setTaxInfoShowAmount(showTaxAmount);

    }

    private void setDefaultB2BOptions(final Shop shop, final Customer customer, final MutableOrderInfo info) {

        final String customerType = getCurrentCustomerType(customer);

        boolean blockCheckout = shop.isSfBlockCustomerCheckout(customerType);
        boolean orderRequiresApproval = shop.isSfRequireCustomerOrderApproval(customerType);

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
     * Default customer type determination.
     *
     * @param customer customer or null for anonymous
     *
     * @return type (not null)
     */
    protected String getCurrentCustomerType(final Customer customer) {
        return customer == null ? "B2G" : (StringUtils.isBlank(customer.getCustomerType()) ? "B2C" : customer.getCustomerType());
    }

    private boolean authenticate(final String username, final Shop shop, final String password) {
        return customerService.isCustomerExists(username, shop) &&
                customerService.isPasswordValid(username, shop, password);
    }

}
