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
import java.util.Map;
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


    /**
     * Interface: GET /yes-api/rest/auth/check
     * <p>
     * <p>
     * Check interface that allows to check authentication state of user. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     *
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : true,
     *    "greeting" : "Bob Doe",
     *    "tokenRO" : {
     *        "uuid" : "1db8def2-21e0-44d2-aeb0-56baae761129"
     *    },
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>SESSION_EXPIRED</td><td>user session expired</td></tr>
     *     <tr><td>INACTIVE_FOR_SHOP</td><td>user is inactive for shop</td></tr>
     *     <tr><td>AUTH_FAILED</td><td>user exists but credentials are not valid</td></tr>
     * </table>
     *
     *
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/check",
            method = RequestMethod.GET,
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
                final AuthenticationResultRO authExpired = new AuthenticationResultRO(
                        cart.getCustomerName(),
                        new TokenRO(cart.getGuid()));
                authExpired.setAuthenticated(false);
                authExpired.setCode("SESSION_EXPIRED");
                return authExpired;
            case ShoppingCart.INACTIVE_FOR_SHOP:
                final AuthenticationResultRO authInactive = new AuthenticationResultRO(
                        cart.getCustomerName(),
                        new TokenRO(cart.getGuid()));
                authInactive.setAuthenticated(false);
                authInactive.setCode("INACTIVE_FOR_SHOP");
                return authInactive;
            case ShoppingCart.NOT_LOGGED:
            default:
                return new AuthenticationResultRO("AUTH_FAILED");

        }

    }

    /**
     * Interface: PUT /yes-api/rest/auth/login
     * <p>
     * <p>
     * Login interface that allows to authenticate user cart. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for login PUT operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "username": "bob11@bob.com",
     *    "password": "bBuyM-6-",
     *    "activate": true
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;login&gt;
     *    &lt;username&gt;bob11@bob.com&lt;/username&gt;
     *    &lt;password&gt;bBuyM-6-&lt;/password&gt;
     *    &lt;activate&gt;true&lt;/activate&gt;
     * &lt;/login&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     *
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : true,
     *    "greeting" : "Bob Doe",
     *    "tokenRO" : {
     *        "uuid" : "1db8def2-21e0-44d2-aeb0-56baae761129"
     *    },
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>USER_FAILED</td><td>user does not exist</td></tr>
     *     <tr><td>AUTH_FAILED</td><td>user exists but credentials are not valid</td></tr>
     *     <tr><td>INACTIVE_FOR_SHOP</td><td>user exists but profile is active for given shop, use activate=true to force activation on login</td></tr>
     * </table>
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

            do {

                executeLoginCommand(loginRO.getUsername(), loginRO.getPassword());

                final TokenRO token = persistShoppingCart(request, response);

                ShoppingCart cart = getCurrentCart();
                final int logOnState = cart.getLogonState();
                if (logOnState == ShoppingCart.LOGGED_IN) {

                    return new AuthenticationResultRO(cart.getCustomerName(), token);

                } else if (logOnState == ShoppingCart.INACTIVE_FOR_SHOP) {

                    if (loginRO.isActivate()) {
                        // Login again with inactive state adds customer to shop
                        continue;
                    }

                    return new AuthenticationResultRO("INACTIVE_FOR_SHOP");

                } else {

                    // any other state should break to AUTH_FAILED
                    break;

                }

            } while (true);

            return new AuthenticationResultRO("AUTH_FAILED");

        }

        return new AuthenticationResultRO("USER_FAILED");
    }


    /**
     * Interface: GET /yes-api/rest/auth/logout
     * <p>
     * <p>
     * Logout interface that allows to de-authenticate user cart. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for logout operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     *
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : true,
     *    "greeting" : null,
     *    "tokenRO" : null,
     *    "error" : 'LOGOUT_SUCCESS'
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>LOGOUT_SUCCESS</td><td>if logout was successful</td></tr>
     * </table>
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


    /**
     * Interface: PUT /yes-api/rest/auth/register
     * <p>
     * <p>
     * Login interface that allows to authenticate user cart. The token for the authenticated cart is
     * returned back as response header and also as a cookie.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for register PUT operation</h3><p>
     * <p>
     * <p>
     * <table border="1">
     *     <tr><td>JSON example:</td><td>
     * <pre><code>
     * {
     *    "email" : "bobdoe@yes-cart.org",
     *    "firstname" : "Bob",
     *    "lastname" : "Doe",
     *    "phone" : "123123123123",
     *    "custom" : {
     *        "attr1": "value1",
     *        "attr2": "value2",
     *        ...
     *        "attrN": "valueN"
     *    }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example:</td><td>
     * <pre><code>
     * &lt;login&gt;
     *    &lt;email&gt;bobdoe@yes-cart.org&lt;/email&gt;
     *    &lt;firstname&gt;Bob&lt;/firstname&gt;
     *    &lt;lastname&gt;Doe&lt;/lastname&gt;
     *    &lt;phone&gt;123123123123&lt;/phone&gt;
     *    &lt;custom&gt;
     *        &lt;entry key="attr1"&gt;value1&lt;/entry&gt;
     *        &lt;entry key="attr2"&gt;value2&lt;/entry&gt;
     *        ...
     *        &lt;entry key="attrN"&gt;valueN&lt;/entry&gt;
     *    &lt;/custom&gt;
     * &lt;/login&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : true,
     *    "greeting" : "Bob Doe",
     *    "tokenRO" : {
     *        "uuid" : "1db8def2-21e0-44d2-aeb0-56baae761129"
     *    },
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting&gt;Bob Doe&lt;/greeting&gt;
     *    &lt;success&gt;true&lt;/success&gt;
     *    &lt;token&gt;
     *       &lt;uuid&gt;1db8def2-21e0-44d2-aeb0-56baae761129&lt;/uuid&gt;
     *    &lt;/token&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>EMAIL_FAILED</td><td>email must be more than 6 and less than 256 chars (^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*((\.[A-Za-z]{2,}){1}$)) </td></tr>
     *     <tr><td>FIRSTNAME_FAILED</td><td>must be not blank</td></tr>
     *     <tr><td>LASTNAME_FAILED</td><td>must be not blank</td></tr>
     *     <tr><td>PHONE_FAILED</td><td>phone must be more than 4 and less than 13 chars</td></tr>
     *     <tr><td>USER_FAILED</td><td>email must not be already registered</td></tr>
     * </table>
     *
     *
     * @param registerRO register parameters (see examples above)
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
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

        final Map<String, Object> data = new HashMap<String, Object>();
        if (registerRO.getCustom() != null) {
            data.putAll(registerRO.getCustom());
        }
        data.put("firstname", registerRO.getFirstname());
        data.put("lastname", registerRO.getLastname());
        data.put("phone", registerRO.getPhone());


        final String password = customerServiceFacade.registerCustomer(
                ApplicationDirector.getCurrentShop(),
                registerRO.getEmail(), data
        );

        final LoginRO loginRO = new LoginRO();
        loginRO.setUsername(registerRO.getEmail());
        loginRO.setPassword(password);

        return login(loginRO, request, response);

    }

    /**
     * Execute login command.
     *
     * @param email     customer.
     * @param password  password.
     */
    protected void executeLoginCommand(final String email, final String password) {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, getCurrentCart(),
                new HashMap<String, Object>() {{
                    put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, email);
                    put(ShoppingCartCommand.CMD_LOGIN_P_PASS, password);
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
