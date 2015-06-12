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

import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PasswordResetCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    private final CustomerService customerService;
    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerService customer service
     * @param shopService shop service
     */
    public PasswordResetCommandImpl(final ShoppingCartCommandRegistry registry,
                                    final CustomerService customerService,
                                    final ShopService shopService) {
        super(registry);
        this.customerService = customerService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_RESET_PASSWORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final String token = (String) parameters.get(CMD_RESET_PASSWORD);

            final Customer customer = customerService.getCustomerByToken(token);
            if (customer == null) {
                throw new BadCredentialsException(Constants.PASSWORD_RESET_AUTH_TOKEN_INVALID);
            }

            final Shop shop = shopService.findById(shoppingCart.getShoppingContext().getShopId());

            customerService.resetPassword(customer, shop, token);

        }
    }

}
