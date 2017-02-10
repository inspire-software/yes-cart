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

package org.yes.cart.web.service.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 14:46
 */
@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private CustomerServiceFacade customerServiceFacade;
    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;


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
     *    "token" : {
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

        final ShoppingCart cart = cartMixin.getCurrentCart();
        cartMixin.persistShoppingCart(request, response);

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
     *    "token" : {
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

        final Customer customer = customerServiceFacade.getCustomerByEmail(cartMixin.getCurrentShop(), loginRO.getUsername());

        if (customer != null) {

            executeLoginCommand(loginRO.getUsername(), loginRO.getPassword());

            final TokenRO token = cartMixin.persistShoppingCart(request, response);

            ShoppingCart cart = cartMixin.getCurrentCart();
            final int logOnState = cart.getLogonState();
            if (logOnState == ShoppingCart.LOGGED_IN) {

                return new AuthenticationResultRO(cart.getCustomerName(), token);

            }

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

        final ShoppingCart cart = cartMixin.getCurrentCart();

        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {

            executeLogoutCommand();
            cartMixin.persistShoppingCart(request, response);

        }

        return new AuthenticationResultRO("LOGOUT_SUCCESS");

    }


    /**
     * Interface: GET /yes-api/rest/auth/register
     * <p>
     * <p>
     * Interface to list all attributes required for registration.
     * <p>
     * If customerType is not specified returns form with two custom attributes: 1) customerType - containing customer type choices
     * and 2) guestCheckoutEnabled - containing true/false flag.
     * <p>
     * If customerType is specified returns custom attributes that represent form fields.
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
     * <h3>Parameters for register GET operation</h3><p>
     * <p>
     * <table border="1">
     *     <tr><td>customerType</td><td>B2B,B2C etc must be configured in SHOP_CUSTOMER_TYPES</td></tr>
     * </table>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *   "phone" : null,
     *   "custom" : [
     *     {
     *       "attrvalueId" : 0,
     *       "val" : null,
     *       "displayVals" : null,
     *       "attributeName" : "Marketing Opt in",
     *       "attributeId" : 11051,
     *       "attributeDisplayNames" : {},
     *       "attributeDisplayChoices" : {},
     *       "customerId" : 0
     *     },
     *     {
     *       "attrvalueId" : 0,
     *       "val" : null,
     *       "displayVals" : null,
     *       "attributeName" : "Customer Type",
     *       "attributeId" : 1611,
     *       "attributeDisplayNames" : {
     *         "uk" : "тип пользователя",
     *         "en" : "Customer Type"
     *       },
     *       "attributeDisplayChoices" : {
     *         "uk" : "B-Покупатель,S-Продавец",
     *         "ru" : "B-Покупатель,S-Продавец",
     *         "en" : "B-Buyer,S-Seller"
     *       },
     *       "customerId" : 0
     *     }
     *   ],
     *   "lastname" : null,
     *   "firstname" : null,
     *   "email" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;register-form&gt;
     *     &lt;custom&gt;
     *         &lt;attribute attribute-id="11051" attrvalue-id="0" customer-id="0"&gt;
     *             &lt;attribute-display-choices/&gt;
     *             &lt;attribute-display-names/&gt;
     *             &lt;attribute-name&gt;Marketing Opt in&lt;/attribute-name&gt;
     *         &lt;/attribute&gt;
     *         &lt;attribute attribute-id="1611" attrvalue-id="0" customer-id="0"&gt;
     *             &lt;attribute-display-choices&gt;
     *                 &lt;entry lang="uk"&gt;B-Покупатель,S-Продавец&lt;/entry&gt;
     *                 &lt;entry lang="en"&gt;B-Buyer,S-Seller&lt;/entry&gt;
     *                 &lt;entry lang="ru"&gt;B-Покупатель,S-Продавец&lt;/entry&gt;
     *             &lt;/attribute-display-choices&gt;
     *             &lt;attribute-display-names&gt;
     *                 &lt;entry lang="uk"&gt;тип пользователя&lt;/entry&gt;
     *                 &lt;entry lang="en"&gt;Customer Type&lt;/entry&gt;
     *             &lt;/attribute-display-names&gt;
     *             &lt;attribute-name&gt;Customer Type&lt;/attribute-name&gt;
     *         &lt;/attribute&gt;
     *     &lt;/custom&gt;
     * &lt;/register-form&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     *
     * @param request request
     * @param response response
     *
     * @return registration data
     */
    @RequestMapping(
            value = "/register",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody RegisterFormRO register(final HttpServletRequest request,
                                                 final HttpServletResponse response,
                                                 final @RequestParam(value = "customerType", required = false) String customerType) {

        cartMixin.persistShoppingCart(request, response);
        final Shop shop = cartMixin.getCurrentShop();

        final List<Pair<String, I18NModel>> allowedTypes = customerServiceFacade.getShopSupportedCustomerTypes(shop);

        final RegisterFormRO formRO = new RegisterFormRO();

        if (StringUtils.isNotBlank(customerType)) {
            // Retrieve form fields for type

            formRO.setCustomerType(customerType);
            if (AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST.equals(customerType)) {
                formRO.setCustomerTypeSupported(customerServiceFacade.isShopGuestCheckoutSupported(shop));
            } else {
                formRO.setCustomerTypeSupported(customerServiceFacade.isShopCustomerTypeSupported(shop, customerType));
            }

            final List<AttrValueCustomer> avs = customerServiceFacade.getShopRegistrationAttributes(shop, customerType);

            formRO.setCustom(mappingMixin.map(avs, AttrValueCustomerRO.class, AttrValueCustomer.class));

        } else {
            // If no type then present form with allowed types.

            final AttrValueCustomerRO avroct = new AttrValueCustomerRO();
            avroct.setAttributeCode("customerType");

            final List<String> supported = languageService.getSupportedLanguages(shop.getCode());

            final Map<String, String> displayChoice = new HashMap<String, String>();
            for (final Pair<String, I18NModel> allowedType : allowedTypes) {
                for (final String lang : supported) {
                    final String i18n = allowedType.getSecond().getValue(lang);

                    String existing = displayChoice.get(lang);
                    if (existing != null) {
                        displayChoice.put(lang, existing + "," + allowedType.getFirst() + "-" + i18n);
                    } else {
                        displayChoice.put(lang, allowedType.getFirst() + "-" + i18n);
                    }
                }
            }
            avroct.setAttributeDisplayChoices(displayChoice);

            final AttrValueCustomerRO avrogc = new AttrValueCustomerRO();
            avrogc.setAttributeCode("guestCheckoutEnabled");
            avrogc.setVal(String.valueOf(customerServiceFacade.isShopGuestCheckoutSupported(shop)));

            formRO.setCustom(new ArrayList<AttrValueCustomerRO>(Arrays.asList(avrogc, avroct)));

        }

        return formRO;

    }

    private static final Pattern EMAIL =
            Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)",
            Pattern.CASE_INSENSITIVE);


    /**
     * Interface: PUT /yes-api/rest/auth/register
     * <p>
     * <p>
     * Register interface that allows to register user. The token for the authenticated cart is
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
     *    "customerType" : "B2C",
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
     *    "token" : {
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
     *     <tr><td>[ATTRIBUTE CODE]:FAILED</td><td>
     *         E.g. CUSTOMERTYPE_FAILED denoting that mandatory value was missing (could also happen if regex fails but there is no
     *         validation message specified on the {@link org.yes.cart.domain.entity.Attribute#getValidationFailedMessage()})
     *     </td></tr>
     *     <tr><td>[ATTRIBUTE CODE]:FAILED:[Message]</td><td>
     *         E.g. "CUSTOMERTYPE:FAILED:Please choose either Buyer or Seller (UK)" denoting that regex test failed.
     *         RegEx and Message come from {@link org.yes.cart.domain.entity.Attribute#getRegexp()} and
     *         {@link org.yes.cart.domain.entity.Attribute#getValidationFailedMessage()} respectively
     *     </td></tr>
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

        // No existing users, non-typed customers or guests allowed
        if (customerServiceFacade.isCustomerRegistered(cartMixin.getCurrentShop(), registerRO.getEmail())
                || StringUtils.isBlank(registerRO.getCustomerType()) || AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST.equals(registerRO.getCustomerType())
                || !customerServiceFacade.isShopCustomerTypeSupported(cartMixin.getCurrentShop(), registerRO.getCustomerType())) {

            return new AuthenticationResultRO("USER_FAILED");

        }

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        Shop registrationShop = shop;
        if (StringUtils.isNotBlank(registerRO.getOrganisation())) {
            registrationShop = shopService.getSubShopByNameAndMaster(registerRO.getOrganisation(), shop.getShopId());
            if (registrationShop == null) {
                return new AuthenticationResultRO("ORGANISATION_FAILED");
            }
        }


        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("customerType", registerRO.getCustomerType());

        if (registerRO.getCustom() != null) {

            for (final AttrValueCustomer av : customerServiceFacade.getShopRegistrationAttributes(shop, registerRO.getCustomerType())) {

                final Attribute attr = av.getAttribute();
                final String value = registerRO.getCustom().get(attr.getCode());

                if (attr.isMandatory() && StringUtils.isBlank(value)) {

                    return new AuthenticationResultRO(attr.getCode() + ":FAILED");

                } else if (StringUtils.isNotBlank(attr.getRegexp())
                        && !Pattern.compile(attr.getRegexp()).matcher(value).matches()) {

                    final String regexError = new FailoverStringI18NModel(
                            attr.getValidationFailedMessage(),
                            null).getValue(cart.getCurrentLocale());

                    if (StringUtils.isBlank(regexError)) {
                        return new AuthenticationResultRO(attr.getCode() + ":FAILED");
                    }
                    return new AuthenticationResultRO(attr.getCode() + ":FAILED:" + regexError);

                } else {

                    data.put(attr.getCode(), value);

                }
            }
        }

        final String password = customerServiceFacade.registerCustomer(registrationShop, registerRO.getEmail(), data);

        final LoginRO loginRO = new LoginRO();
        loginRO.setUsername(registerRO.getEmail());
        loginRO.setPassword(password);

        return login(loginRO, request, response);

    }


    /**
     * Interface: PUT /yes-api/rest/auth/guest
     * <p>
     * <p>
     * Guest interface that allows create guest user. The token for the authenticated cart is
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
     *    "customerType" : "B2G",
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
     *    "token" : {
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
     *     <tr><td>[ATTRIBUTE CODE]:FAILED</td><td>
     *         E.g. CUSTOMERTYPE_FAILED denoting that mandatory value was missing (could also happen if regex fails but there is no
     *         validation message specified on the {@link org.yes.cart.domain.entity.Attribute#getValidationFailedMessage()})
     *     </td></tr>
     *     <tr><td>[ATTRIBUTE CODE]:FAILED:[Message]</td><td>
     *         E.g. "CUSTOMERTYPE:FAILED:Please choose either Buyer or Seller (UK)" denoting that regex test failed.
     *         RegEx and Message come from {@link org.yes.cart.domain.entity.Attribute#getRegexp()} and
     *         {@link org.yes.cart.domain.entity.Attribute#getValidationFailedMessage()} respectively
     *     </td></tr>
     *     <tr><td>GUEST_FAILED</td><td>error creating guest account</td></tr>
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
            value = "/guest",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AuthenticationResultRO guest(final @RequestBody RegisterRO registerRO,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        if (StringUtils.isBlank(registerRO.getEmail())
                || registerRO.getEmail().length() < 6
                || registerRO.getEmail().length() > 256
                || !EMAIL.matcher(registerRO.getEmail()).matches()) {

            return new AuthenticationResultRO("EMAIL_FAILED");

        }

        // Only guests allowed
        if (StringUtils.isBlank(registerRO.getCustomerType()) || !AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST.equals(registerRO.getCustomerType())) {

            return new AuthenticationResultRO("GUEST_FAILED");

        }

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        if (!customerServiceFacade.isShopGuestCheckoutSupported(shop)) {

            return new AuthenticationResultRO("GUEST_FAILED");

        }

        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("customerType", registerRO.getCustomerType());
        data.put("cartGuid", cart.getGuid());

        if (registerRO.getCustom() != null) {

            for (final AttrValueCustomer av : customerServiceFacade.getShopRegistrationAttributes(shop, registerRO.getCustomerType())) {

                final Attribute attr = av.getAttribute();
                final String value = registerRO.getCustom().get(attr.getCode());

                if (attr.isMandatory() && StringUtils.isBlank(value)) {

                    return new AuthenticationResultRO(attr.getCode() + ":FAILED");

                } else if (StringUtils.isNotBlank(attr.getRegexp())
                        && !Pattern.compile(attr.getRegexp()).matcher(value).matches()) {

                    final String regexError = new FailoverStringI18NModel(
                            attr.getValidationFailedMessage(),
                            null).getValue(cart.getCurrentLocale());

                    if (StringUtils.isBlank(regexError)) {
                        return new AuthenticationResultRO(attr.getCode() + ":FAILED");
                    }
                    return new AuthenticationResultRO(attr.getCode() + ":FAILED:" + regexError);

                } else {

                    data.put(attr.getCode(), value);

                }
            }
        }

        final String guest = customerServiceFacade.registerGuest(shop, registerRO.getEmail(), data);

        if (StringUtils.isNotBlank(guest)) {

            final TokenRO token = cartMixin.persistShoppingCart(request, response);

            return new AuthenticationResultRO(null, token);
        }

        return new AuthenticationResultRO("GUEST_FAILED");

    }


    /**
     * Interface: POST /yes-api/rest/auth/resetpassword
     * <p>
     * <p>
     * Reset password interface that allows to request password to be reset and reset it.
     * Password reset is a two step process. First step is to provide valid email of registered
     * customer and generate authToken. Second step is to use authToken in order to confirm password
     * reset.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>email</td><td>
     *         E-mail of a registered customer. Supplying this parameter will trigger an email
     *         with authToken.
     *     </td></tr>
     *     <tr><td>authToken</td><td>
     *         Authentication token of a registered customer.  Supplying this parameter will trigger an
     *         email with new password.
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <p>
     * Output that does not have error code indicates successful processing.
     * <p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : false,
     *    "greeting" : null,
     *    "token" : null,
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting/&gt;
     *    &lt;success&gt;false&lt;/success&gt;
     *    &lt;token/&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>INVALID_PARAMETERS</td><td>if either "email" or "authToken" was not sent</td></tr>
     *     <tr><td>INVALID_TOKEN</td><td>Supplied authToken is not valid or expired</td></tr>
     *     <tr><td>INVALID_EMAIL</td><td>Supplied email does not belong to a registered customer</td></tr>
     * </table>
     *
     *
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/resetpassword",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AuthenticationResultRO resetPassword(@RequestParam(value = "email", required = false) final String email,
                                                              @RequestParam(value = "authToken", required = false) final String authToken,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        if (StringUtils.isNotBlank(authToken)) {

            if (executePasswordResetCommand(authToken)) {
                return new AuthenticationResultRO();
            }
            return new AuthenticationResultRO("INVALID_TOKEN");

        } else if (StringUtils.isNotBlank(email)) {

            final Shop shop = cartMixin.getCurrentShop();
            final Customer customer = customerServiceFacade.getCustomerByEmail(cartMixin.getCurrentShop(), email);
            if (customer == null) {
                return new AuthenticationResultRO("INVALID_EMAIL");
            }
            customerServiceFacade.resetPassword(shop, customer);
            return new AuthenticationResultRO();

        }
        return new AuthenticationResultRO("INVALID_PARAMETERS");
    }



    /**
     * Interface: POST /yes-api/rest/auth/signupnewsletter
     * <p>
     * <p>
     * Sign up for newsletter interface sends email request to shop administrator with provided
     * email.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>email</td><td>
     *         E-mail to be used for newsletters.
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <p>
     * Output that does not have error code indicates successful processing.
     * <p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : false,
     *    "greeting" : null,
     *    "token" : null,
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting/&gt;
     *    &lt;success&gt;false&lt;/success&gt;
     *    &lt;token/&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>INVALID_EMAIL</td><td>Supplied email is invalidr</td></tr>
     * </table>
     *
     *
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/signupnewsletter",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AuthenticationResultRO signUpNewsletter(@RequestParam(value = "email", required = false) final String email,
                                                                 final HttpServletRequest request,
                                                                 final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        if (StringUtils.isBlank(email)
                || email.length() < 6
                || email.length() > 256
                || !EMAIL.matcher(email).matches()) {

            return new AuthenticationResultRO("EMAIL_FAILED");

        }

        final Shop shop = cartMixin.getCurrentShop();
        customerServiceFacade.registerNewsletter(shop, email, new HashMap<String, Object>());

        return new AuthenticationResultRO();

    }

    /**
     * Interface: POST /yes-api/rest/auth/contactus
     * <p>
     * <p>
     * Contact Us interface sends email request to shop administrator with provided
     * email.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>email</td><td>
     *         E-mail to be used for newsletters.
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <p>
     * Output that does not have error code indicates successful processing.
     * <p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *    "success" : false,
     *    "greeting" : null,
     *    "token" : null,
     *    "error" : null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;authentication-result&gt;
     *    &lt;greeting/&gt;
     *    &lt;success&gt;false&lt;/success&gt;
     *    &lt;token/&gt;
     * &lt;/authentication-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>INVALID_EMAIL</td><td>Supplied email is invalid</td></tr>
     * </table>
     *
     *
     * @param request request
     * @param response response
     *
     * @return authentication result
     */
    @RequestMapping(
            value = "/contactus",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AuthenticationResultRO contactUs(@RequestParam(value = "email", required = false) final String email,
                                                          @RequestParam(value = "name", required = false) final String name,
                                                          @RequestParam(value = "phone", required = false) final String phone,
                                                          @RequestParam(value = "subject", required = false) final String subject,
                                                          @RequestParam(value = "message", required = false) final String message,
                                                          final HttpServletRequest request,
                                                          final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        if (StringUtils.isBlank(email)
                || email.length() < 6
                || email.length() > 256
                || !EMAIL.matcher(email).matches()) {

            return new AuthenticationResultRO("EMAIL_FAILED");

        }

        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("phone", phone);
        data.put("email", email);
        data.put("subject", subject);
        data.put("body", message);


        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            final String val = (String) entry.getValue();
            if (StringUtils.isBlank(val)) {

                return new AuthenticationResultRO(entry.getKey() + "_FAILED");

            }
        }


        final Shop shop = cartMixin.getCurrentShop();
        customerServiceFacade.registerEmailRequest(shop, email, data);

        return new AuthenticationResultRO();

    }



    /**
     * Execute login command.
     *
     * @param email     customer.
     * @param password  password.
     */
    protected void executeLoginCommand(final String email, final String password) {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, cartMixin.getCurrentCart(),
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
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, cartMixin.getCurrentCart(),
                new HashMap<String, Object>() {{
                    put(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                }}
        );
    }

    /**
     * Execute password reset command.
     */
    protected boolean executePasswordResetCommand(final String token) {
        try {
            shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_RESET_PASSWORD, cartMixin.getCurrentCart(),
                    new HashMap<String, Object>() {{
                        put(ShoppingCartCommand.CMD_RESET_PASSWORD, token);
                    }}
            );
            return true;
        } catch (BadCredentialsException bce) {
            return false;
        }
    }


}
