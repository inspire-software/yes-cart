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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.ro.AuthenticationResultRO;
import org.yes.cart.domain.ro.LoginRO;
import org.yes.cart.domain.ro.RegisterRO;
import org.yes.cart.domain.ro.TokenRO;
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
@RequestMapping("/auth")
public class AuthenticationController extends AbstractApiController  {

    @Autowired
    private CustomerServiceFacade customerServiceFacade;
    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    @RequestMapping(
            value = "/check",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AuthenticationResultRO check(final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        final ShoppingCart cart = getCurrentCart();

        switch (cart.getLogonState()) {
            case ShoppingCart.LOGGED_IN:
                return new AuthenticationResultRO(
                    cart.getCustomerName(),
                    new TokenRO(cart.getGuid()));
            case ShoppingCart.SESSION_EXPIRED:
                final AuthenticationResultRO auth = new AuthenticationResultRO(
                        cart.getCustomerName(),
                        new TokenRO(cart.getGuid()));
                auth.setAuthenticated(false);
                auth.setCode("SESSION_EXPIRED");
                return auth;
            case ShoppingCart.NOT_LOGGED:
            default:
                return new AuthenticationResultRO("AUTH_FAILED");

        }

    }

    /**
     * Interface: PUT /yes-api/rest/login
     *
     *
     * Login interface that allows to authenticate user cart. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     *
     *
     * Parameters for login PUT operation
     * ==================================
     * JSON example:
     * {
     *    "username": "bob11@bob.com",
     *    "password": "bBuyM-6-"
     * }
     *
     * XML example:
     * &lt;login&gt;
     *    &lt;username&gt;bob11@bob.com&lt;/username&gt;
     *    &lt;password&gt;bBuyM-6-&lt;/password&gt;
     * &lt;/login&gt;
     *
     * Output
     * ======
     *
     * JSON example (Accept=application/json):
     * {
     *    "success" : true,
     *    "greeting" : "Bob Doe",
     *    "tokenRO" : {
     *        "uuid" : "1db8def2-21e0-44d2-aeb0-56baae761129"
     *    },
     *    "error" : null
     * }
     *
     * XML example (Accept=application/xml):
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     *
     * Error codes
     * ===========
     * USER_FAILED      - user does not exist
     * AUTH_FAILED      - user exists but credentials are not valid
     *
     *
     * @param loginRO login parameters (see examples above)
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/login",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
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


    /**
     * Interface: GET /yes-api/rest/logout
     *
     *
     * Logout interface that allows to de-authenticate user cart. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     *
     *
     * Parameters for login PUT operation
     * ==================================
     * NONE
     *
     * Output
     * ======
     *
     * JSON example (Accept=application/json):
     * {
     *    "success" : true,
     *    "greeting" : "Bob Doe",
     *    "tokenRO" : {
     *        "uuid" : "1db8def2-21e0-44d2-aeb0-56baae761129"
     *    },
     *    "error" : null
     * }
     *
     * XML example (Accept=application/xml):
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     *
     * Error codes
     * ===========
     * LOGOUT_SUCCESS   - if logout was successful
     *
     *
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/logout",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
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

    @RequestMapping(
            value = "/register",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
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
