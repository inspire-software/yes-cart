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

package org.yes.cart.web.service.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.ro.AuthenticationResultRO;
import org.yes.cart.domain.ro.LoginRO;
import org.yes.cart.domain.ro.RegisterRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 14:46
 */
@Controller
public class AuthenticationController extends AbstractApiController  {

    @Autowired
    private CustomerServiceFacade customerServiceFacade;
    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    @RequestMapping(value = "/login", method = RequestMethod.PUT)
    public @ResponseBody AuthenticationResultRO login(final @RequestBody LoginRO loginRO,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        final Customer customer = customerServiceFacade.getCustomerByEmail(loginRO.getUsername());

        if (customer != null) {
            if (this.customerServiceFacade.authenticate(loginRO.getUsername(), loginRO.getPassword())) {

                executeLoginCommand(customer);

                persistShoppingCart(request, response);

                return new AuthenticationResultRO(
                        customer.getFirstname() + " " + customer.getLastname(),
                        persistShoppingCart(request, response));
            }

            return new AuthenticationResultRO("AUTH_FAILED");

        }

        return new AuthenticationResultRO("USER_FAILED");
    }


    @RequestMapping(value = "/logout")
    public @ResponseBody AuthenticationResultRO logout(final HttpServletRequest request,
                                                       final HttpServletResponse response) {

        final ShoppingCart cart = getCurrentCart();

        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {

            executeLogoutCommand();
            persistShoppingCart(request, response);

        }

        return new AuthenticationResultRO("LOGOUT_SUCCESS");

    }


    private static final Pattern EMAIL =
            Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)",
            Pattern.CASE_INSENSITIVE);

    @RequestMapping(value = "/register", method = RequestMethod.PUT)
    public @ResponseBody AuthenticationResultRO register(final @RequestBody RegisterRO registerRO,
                                                         final HttpServletRequest request,
                                                         final HttpServletResponse response) {

        if (StringUtils.isBlank(registerRO.getEmail())
                || registerRO.getEmail().length() < 6
                || registerRO.getEmail().length() > 256
                || !EMAIL.matcher(registerRO.getEmail()).matches()) {

            return new AuthenticationResultRO("EMAIL_FAILED");

        }

        if (StringUtils.isBlank(registerRO.getFirstname())) {

            return new AuthenticationResultRO("FIRSTNAME_FAILED");

        }

        if (StringUtils.isBlank(registerRO.getLastname())) {

            return new AuthenticationResultRO("LASTNAME_FAILED");

        }

        if (StringUtils.isBlank(registerRO.getPhone())
                || registerRO.getPhone().length() < 4
                || registerRO.getPhone().length() > 13) {

            return new AuthenticationResultRO("PHONE_FAILED");

        }

        if (customerServiceFacade.isCustomerRegistered(registerRO.getEmail())) {

            return new AuthenticationResultRO("USER_FAILED");

        }

        final String password = customerServiceFacade.registerCustomer(
                ApplicationDirector.getCurrentShop(),
                registerRO.getEmail(), new HashMap<String, Object>() {{
            put("firstname", registerRO.getFirstname());
            put("lastname", registerRO.getLastname());
            put("phone", registerRO.getPhone());
        }}
        );

        final LoginRO loginRO = new LoginRO();
        loginRO.setUsername(registerRO.getEmail());
        loginRO.setPassword(password);

        return login(loginRO, request, response);

    }

    /**
     * Execute login command.
     *
     * @param customer     customer.
     */
    protected void executeLoginCommand(final Customer customer) {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, getCurrentCart(),
                new HashMap<String, Object>() {{
                    put("email", customer.getEmail());
                    put("name", customer.getFirstname() + " " + customer.getLastname());
                    put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
                }}
        );
    }

    /**
     * Execute logout command.
     */
    protected void executeLogoutCommand() {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, getCurrentCart(),
                new HashMap<String, Object>() {{
                    put(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                }}
        );
    }


}
