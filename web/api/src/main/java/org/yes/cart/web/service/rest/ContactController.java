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

package org.yes.cart.web.service.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.ro.ContactResultRO;
import org.yes.cart.utils.RegExUtils;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 12/05/2020
 * Time: 21:25
 */
@Controller
@Api(value = "Contact", description = "Contact controller", tags = "contacts")
public class ContactController {

    private static final Pattern EMAIL =
            Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)",
                    Pattern.CASE_INSENSITIVE);


    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private CartMixin cartMixin;


    /**
     * Interface: POST /api/rest/contacts/signupnewsletter
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
    @ApiOperation(value = "Sign up for newsletter interface sends email request to shop administrator with provided email.")
    @RequestMapping(
            value = "/contacts/signupnewsletter",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody
    ContactResultRO signUpNewsletter(final @ApiParam(value = "Email to sing up") @RequestParam(value = "email", required = false) String email,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        final Shop shop = cartMixin.getCurrentShop();
        final ContactResultRO result = checkValidEmail(email, shop);
        if (result != null) {
            return result;
        }

        customerServiceFacade.registerNewsletter(shop, Collections.singletonMap("email", email));

        return new ContactResultRO();

    }

    private ContactResultRO checkValidEmail(final String email, final Shop shop) {

        final AttrValueWithAttribute emailConfig = customerServiceFacade.getShopEmailAttribute(shop);
        if (emailConfig != null) {

            final ContactResultRO result = checkValid(emailConfig.getAttribute(), email, cartMixin.getCurrentCart().getCurrentLocale());
            if (result != null) {

                return result;

            }

        } else if (StringUtils.isBlank(email)
                || email.length() < 6
                || email.length() > 256
                || !EMAIL.matcher(email).matches()) {

            return new ContactResultRO("EMAIL_FAILED");

        }
        return null;
    }

    /**
     * Interface: POST /api/rest/contacts/contactus
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
    @ApiOperation(value = "Contact Us interface sends email request to shop administrator with provided email.")
    @RequestMapping(
            value = "/contacts/contactus",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ContactResultRO contactUs(final @ApiParam(value = "Your email") @RequestParam(value = "email", required = false) String email,
                                                   final @ApiParam(value = "Name") @RequestParam(value = "name", required = false) String name,
                                                   final @ApiParam(value = "Phone") @RequestParam(value = "phone", required = false) String phone,
                                                   final @ApiParam(value = "Message subject") @RequestParam(value = "subject", required = false) String subject,
                                                   final @ApiParam(value = "Message body") @RequestParam(value = "message", required = false) String message,
                                                   final HttpServletRequest request,
                                                   final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        final Shop shop = cartMixin.getCurrentShop();
        final ContactResultRO result = checkValidEmail(email, shop);
        if (result != null) {
            return result;
        }

        final Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("phone", phone);
        data.put("email", email);
        data.put("subject", subject);
        data.put("body", message);


        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            final String val = (String) entry.getValue();
            if (StringUtils.isBlank(val)) {

                return new ContactResultRO(entry.getKey() + "_FAILED");

            }
        }


        customerServiceFacade.contactUsEmailRequest(shop, data);

        return new ContactResultRO();

    }


    protected ContactResultRO checkValid(final Attribute attr, final String value, final String lang) {
        if (attr.isMandatory() && StringUtils.isBlank(value)) {

            return new ContactResultRO(attr.getCode() + ":FAILED");

        } else if (StringUtils.isNotBlank(attr.getRegexp())
                && !RegExUtils.getInstance(attr.getRegexp()).matches(value)) {

            final String regexError = new FailoverStringI18NModel(
                    attr.getValidationFailedMessage(),
                    null).getValue(lang);

            if (StringUtils.isBlank(regexError)) {
                return new ContactResultRO(attr.getCode() + ":FAILED");
            }
            return new ContactResultRO(attr.getCode() + ":FAILED:" + regexError);

        }

        return null;

    }


}
