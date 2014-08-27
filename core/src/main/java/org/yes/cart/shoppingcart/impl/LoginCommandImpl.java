/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LoginCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    private final CustomerService customerService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerService customer service
     */
    public LoginCommandImpl(final ShoppingCartCommandRegistry registry,
                            final CustomerService customerService) {
        super(registry);
        this.customerService = customerService;
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
    public void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String email = (String) parameters.get(CMD_LOGIN_P_EMAIL);
            final String passw = (String) parameters.get(CMD_LOGIN_P_PASS);
            if (authenticate(email, passw)) {
                final Customer customer = customerService.getCustomerByEmail(email);
                shoppingCart.getShoppingContext().setCustomerEmail(customer.getEmail());
                shoppingCart.getShoppingContext().setCustomerName(customer.getFirstname() + " " + customer.getLastname());
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            } else {
                shoppingCart.getShoppingContext().clearContext();
            }
        }
    }

    private boolean authenticate(final String username, final String password) {
        return customerService.isCustomerExists(username) &&
                customerService.isPasswordValid(username, password);
    }

}
