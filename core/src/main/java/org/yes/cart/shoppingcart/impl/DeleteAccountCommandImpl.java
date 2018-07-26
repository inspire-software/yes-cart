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
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerRemoveService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/07/2018
 * Time: 21:04
 */
public class DeleteAccountCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20180724L;

    private final CustomerService customerService;
    private final CustomerRemoveService customerRemoveService;
    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerService customer service
     * @param customerRemoveService customer service
     * @param shopService shop service
     */
    protected DeleteAccountCommandImpl(final ShoppingCartCommandRegistry registry,
                                       final CustomerService customerService,
                                       final CustomerRemoveService customerRemoveService,
                                       final ShopService shopService) {
        super(registry);
        this.customerService = customerService;
        this.customerRemoveService = customerRemoveService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_DELETE_ACCOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final String token = (String) parameters.get(CMD_DELETE_ACCOUNT);

            final Customer customer = customerService.getCustomerByToken(token);
            if (customer == null) {
                throw new BadCredentialsException(Constants.PASSWORD_RESET_AUTH_TOKEN_INVALID);
            }

            final Shop shop = shopService.findById(shoppingCart.getShoppingContext().getShopId());
            if (shop.isSfDeleteAccountDisabled(customer.getCustomerType())) {
                throw new BadCredentialsException(Constants.PASSWORD_RESET_AUTH_TOKEN_INVALID);
            }

            final String pw = (String) parameters.get(CMD_DELETE_ACCOUNT_PW);
            if (StringUtils.isBlank(pw) || !customerService.isPasswordValid(customer.getEmail(), shop, pw)) {
                // Invalid password provided
                throw new BadCredentialsException(Constants.DELETE_ACCOUNT_PASSWORD_INVALID);
            } else {
                // Full removal, customer will get password via email
                customerRemoveService.deleteAccount(customer, shop, token);
            }


        }
    }

}
