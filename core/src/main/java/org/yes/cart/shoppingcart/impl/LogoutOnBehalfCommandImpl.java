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

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 18/10/2019
 * Time: 08:02
 */
public class LogoutOnBehalfCommandImpl extends AbstractRecalculatePriceCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20191018L;

    private final CustomerResolver customerResolver;
    private final ShopService shopService;

    /**
     * Construct command.
     * @param registry              shopping cart command registry
     * @param customerResolver      customer service
     * @param shopService           shop service
     * @param priceResolver         price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService        product service
     */
    public LogoutOnBehalfCommandImpl(final ShoppingCartCommandRegistry registry,
                                     final CustomerResolver customerResolver,
                                     final ShopService shopService,
                                     final PriceResolver priceResolver,
                                     final PricingPolicyProvider pricingPolicyProvider,
                                     final ProductService productService) {
        super(registry, priceResolver, pricingPolicyProvider, productService, shopService);
        this.customerResolver = customerResolver;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_LOGOUT_ON_BEHALF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey()) && shoppingCart.getLogonState() == ShoppingCart.LOGGED_IN
                && shoppingCart.getShoppingContext().isManagedCart()) {

            final String login = shoppingCart.getShoppingContext().getManagerLogin();

            final long shopId = shoppingCart.getShoppingContext().getShopId();
            final Shop current = shopService.getById(shopId);

            final Customer manager = customerResolver.getCustomerByLogin(login, current);
            if (authorise(shoppingCart, manager)) {

                final MutableShoppingContext ctx = shoppingCart.getShoppingContext();
                final MutableOrderInfo info = shoppingCart.getOrderInfo();
                if (current != null && verify(login, current)) {

                    final Customer customer = customerResolver.getCustomerByLogin(login, current);
                    final List<String> customerShops = new ArrayList<>();
                    // set default shop
                    shoppingCart.getShoppingContext().setCustomerShopId(shoppingCart.getShoppingContext().getShopId());
                    shoppingCart.getShoppingContext().setCustomerShopCode(shoppingCart.getShoppingContext().getShopCode());
                    // set accessible shops
                    for (final Shop shop : customerResolver.getCustomerShops(customer)) {
                        customerShops.add(shop.getCode());
                        if (shop.getMaster() != null && shop.getMaster().getShopId() == shopId) {
                            // set customer shop is registered in a sub of a master
                            shoppingCart.getShoppingContext().setCustomerShopId(shop.getShopId());
                            shoppingCart.getShoppingContext().setCustomerShopCode(shop.getCode());
                        }
                    }

                    ctx.setManagerLogin(null);
                    ctx.setManagerName(null);
                    ctx.setCustomerLogin(customer.getLogin());
                    ctx.setCustomerName(customerResolver.formatNameFor(customer, current));
                    ctx.setCustomerShops(customerShops);
                    setDefaultCustomerOptions(shoppingCart);
                    setDefaultAddressesIfNecessary(current, customer, shoppingCart);
                    setDefaultTaxOptions(shoppingCart);

                    recalculatePricesInCart(shoppingCart);
                    recalculate(shoppingCart);
                    markDirty(shoppingCart);
                } else {
                    ctx.clearContext();
                    info.clearInfo();
                    setDefaultCustomerOptions(shoppingCart);
                    setDefaultTaxOptions(shoppingCart);
                    markDirty(shoppingCart);
                }
            }
        }
    }

    private void setDefaultAddressesIfNecessary(final Shop shop, final Customer customer, final MutableShoppingCart shoppingCart) {
        if (!shoppingCart.getOrderInfo().isBillingAddressNotRequired()
                || !shoppingCart.getOrderInfo().isDeliveryAddressNotRequired()) {

            setDefaultAddressesIfPossible(shoppingCart);

        }
    }


    protected void setDefaultTaxOptions(final MutableShoppingCart shoppingCart) {

        setTaxOptions(shoppingCart, null, null, null);

    }

    private void setDefaultCustomerOptions(final MutableShoppingCart shoppingCart) {

        setCustomerOptions(shoppingCart);

    }

    /**
     * Verify hook.
     *
     * @param username username
     * @param shop     shop
     *
     * @return true if credentials are correct for given shop
     */
    protected boolean verify(final String username, final Shop shop) {
        return customerResolver.getCustomerByLogin(username, shop) != null;
    }

    /**
     * Authorise hook.
     *
     * @param shoppingCart cart
     * @param manager      manager
     *
     * @return true if has authorisation to login on behalf
     */
    protected boolean authorise(final MutableShoppingCart shoppingCart, final Customer manager) {
        return shoppingCart.getShoppingContext().isManagedCart()
                && manager.isAttributeValueByCodeTrue("ROLE_SMCALLCENTERLOGINONBEHALF");
    }

}
