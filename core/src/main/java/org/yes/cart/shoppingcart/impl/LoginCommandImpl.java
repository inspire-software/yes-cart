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
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.ArrayList;
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

            if (current != null && authenticate(email, current, passw)) {
                final Customer customer = customerService.getCustomerByEmail(email, current);
                final List<String> customerShops = new ArrayList<String>();
                for (final Shop shop : customerService.getCustomerShops(customer)) {
                    customerShops.add(shop.getCode());
                }
                shoppingCart.getShoppingContext().setCustomerEmail(customer.getEmail());
                shoppingCart.getShoppingContext().setCustomerName(customerService.formatNameFor(customer, current));
                shoppingCart.getShoppingContext().setCustomerShops(customerShops);
                setDefaultAddressesIfNecessary(shoppingCart, customer);
                recalculatePricesInCart(shoppingCart);
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            } else {
                shoppingCart.getShoppingContext().clearContext();
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

    private boolean authenticate(final String username, final Shop shop, final String password) {
        return customerService.isCustomerExists(username, shop) &&
                customerService.isPasswordValid(username, shop, password);
    }

}
