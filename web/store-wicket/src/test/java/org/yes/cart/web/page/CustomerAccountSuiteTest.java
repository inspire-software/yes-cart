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

package org.yes.cart.web.page;

import org.hamcrest.CustomMatchers;
import org.hamcrest.core.StringContains;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.CustomMockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: inspiresoftware
 * Date: 22/10/2020
 * Time: 19:41
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/main/webapp")
public class CustomerAccountSuiteTest extends AbstractSuiteTest {

    @Autowired
    private ShoppingCartStateService shoppingCartStateService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HashHelper hashHelper;

    private MockHttpSession mockSession;

    @Test
    public void testCustomer() throws Exception {

        reindex();

        final MvcResult start =
        mockMvc.perform(get("/registration")
                .accept(MediaType.TEXT_HTML)
                .locale(LOCALE))
            .andDo(print()).andDo((res) -> {
                mockSession = (MockHttpSession) res.getRequest().getSession();
            })
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string(X_CW_TOKEN, CustomMatchers.isNotBlank()))
            .andReturn();

        final String xCwToken = start.getResponse().getHeader(X_CW_TOKEN);
        assertNotNull(xCwToken);

        final MvcResult registrationPage =
        mockMvc.perform(get(redirectFromPrevious(start))
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
            .andExpect(content().string(StringContains.containsString("registerForm")))
            .andExpect(content().string(StringContains.containsString("name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
            .andExpect(content().string(StringContains.containsString("name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
            .andExpect(content().string(StringContains.containsString("name=\"fields:3:editor:edit\" placeholder=\"Middle name\"")))
            .andExpect(content().string(StringContains.containsString("name=\"fields:4:editor:edit\" placeholder=\"Last name\"")))
            .andExpect(content().string(StringContains.containsString("name=\"fields:5:editor:edit\" placeholder=\"Customer phone\"")))
            .andExpect(content().string(StringContains.containsString("name=\"registerBtn\"")))
            .andExpect(header().string(X_CW_TOKEN, nullValue()))
            .andReturn();

        final Document registrationPageHTML = Jsoup.parse(registrationPage.getResponse().getContentAsString());

        final String email = "bob.doe@account-wicket.com";

        assertFalse("Customer not yet registered", hasEmails(email));

        final MvcResult registrationPageSubmitInvalid =
        mockMvc.perform(post(formAction(registrationPageHTML, "registerForm1"))
                .param("fields:1:editor:edit", email)
                .param("fields:4:editor:edit", "Doe")
                .param("fields:5:editor:edit", "123123123")
                .param("registerBtn", "Register")
                .session(mockSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string(X_CW_TOKEN, xCwToken))
            .andReturn();

        final MvcResult registrationPage2 =
            mockMvc.perform(get(redirectFromPrevious(registrationPageSubmitInvalid))
                    .session(mockSession)
                    .accept(MediaType.TEXT_HTML)
                    .header(X_CW_TOKEN, xCwToken)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("feedbackPanelERROR")))
                .andExpect(content().string(StringContains.containsString("registerForm")))
                .andExpect(header().string(X_CW_TOKEN, nullValue()))
                .andReturn();

        final Document registrationPage2HTML = Jsoup.parse(registrationPage2.getResponse().getContentAsString());

        final MvcResult registrationPageSubmit =
                mockMvc.perform(post(formAction(registrationPage2HTML, "registerForm1"))
                        .param("fields:1:editor:edit", email)
                        .param("fields:2:editor:edit", "Bob")
                        .param("fields:4:editor:edit", "Doe")
                        .param("fields:5:editor:edit", "123123123")
                        .param("registerBtn", "Register")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePage =
                mockMvc.perform(get(redirectFromPrevious(registrationPageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageView =
                mockMvc.perform(get(redirectFromPrevious(profilePage))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("name=\"logoffCmd\"")))
                    .andExpect(content().string(StringContains.containsString("summaryView-summaryForm")))
                    .andExpect(content().string(StringContains.containsString("<p style=\"margin-top: 7px\">bob.doe@account-wicket.com</p>")))
                    .andExpect(content().string(StringContains.containsString("value=\"Change login\"")))
                    .andExpect(content().string(StringContains.containsString("attributesView-form")))
                    .andExpect(content().string(StringContains.containsString("<form class=\"form-horizontal\" id=\"form7\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"bob.doe@account-wicket.com\" name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Middle name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:4:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"123123123\" name=\"fields:5:editor:edit\" placeholder=\"Customer phone\"")))
                    .andExpect(content().string(StringContains.containsString("passwordView-passwordForm")))
                    .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                    .andExpect(content().string(StringContains.containsString("shippingAddressesView-selectAddressForm")))
                    .andExpect(content().string(StringContains.containsString("billingAddressesView-selectAddressForm")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();


        final Mail registration = getEmailBySubject("Registration email", email);
        assertNotNull("Registration email sent", registration);
        assertTrue(registration.getHtmlVersion().contains("Bob registered"));

        final Document profilePageViewHTML = Jsoup.parse(profilePageView.getResponse().getContentAsString());

        final MvcResult profilePageSubmitAttributes =
                mockMvc.perform(post(formAction(profilePageViewHTML, "form7"))
                        .param("fields:1:editor:edit", email)
                        .param("fields:2:editor:edit", "John")
                        .param("fields:3:editor:edit", "Arthur")
                        .param("fields:4:editor:edit", "Doe")
                        .param("fields:5:editor:edit", "4444444444")
                        .param("saveLink", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageView2 =
                mockMvc.perform(get(redirectFromPrevious(profilePageSubmitAttributes))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("summaryView-summaryForm")))
                    .andExpect(content().string(StringContains.containsString("<p style=\"margin-top: 7px\">bob.doe@account-wicket.com</p>")))
                    .andExpect(content().string(StringContains.containsString("value=\"Change login\"")))
                    .andExpect(content().string(StringContains.containsString("attributesView-form")))
                    .andExpect(content().string(StringContains.containsString("<form class=\"form-horizontal\" id=\"form7\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"bob.doe@account-wicket.com\" name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"John\" name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Arthur\" name=\"fields:3:editor:edit\" placeholder=\"Middle name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:4:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"4444444444\" name=\"fields:5:editor:edit\" placeholder=\"Customer phone\"")))
                    .andExpect(content().string(StringContains.containsString("passwordView-passwordForm")))
                    .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                    .andExpect(content().string(StringContains.containsString("shippingAddressesView-selectAddressForm")))
                    .andExpect(content().string(StringContains.containsString("billingAddressesView-selectAddressForm")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document profilePageView2HTML = Jsoup.parse(profilePageView2.getResponse().getContentAsString());

        final MvcResult profilePageView2CreateShippingAddress =
                mockMvc.perform(post(formAction(profilePageView2HTML, "selectAddressForma"))
                        .param("addressRadioGroup:createLink", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


        final MvcResult shippingAddressCreatePage =
                mockMvc.perform(get(redirectFromPrevious(profilePageView2CreateShippingAddress))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


        final MvcResult shippingAddressCreatePageView =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePage))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Please provide delivery address</span>")))
                    .andExpect(content().string(StringContains.containsString("addressForm10")))
                    .andExpect(content().string(StringContains.containsString("value=\"John\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"UA\">Ukraine</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"4444444444\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();


        final Document shippingAddressCreatePageViewHTML = Jsoup.parse(shippingAddressCreatePageView.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmitSelectCountry =
                mockMvc.perform(post(formSelect(shippingAddressCreatePageViewHTML, "addressForm10", "fields:8:editor:country"))
                        .param("fields:1:editor:edit", "John")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:8:editor:country", "UA")
                        .param("fields:9:editor:edit", "4444444444")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


        final MvcResult shippingAddressCreatePageView2 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmitSelectCountry))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Please provide delivery address</span>")))
                    .andExpect(content().string(StringContains.containsString("addressForm10")))
                    .andExpect(content().string(StringContains.containsString("value=\"John\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"In the middle of\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Nowhere\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"0001\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:7:editor:state\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"UA-UA\">Ukraine</option>")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"UA\">Ukraine</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"4444444444\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document shippingAddressCreatePageView2HTML = Jsoup.parse(shippingAddressCreatePageView2.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmit =
                mockMvc.perform(post(formAction(shippingAddressCreatePageView2HTML, "addressForm10"))
                        .param("fields:1:editor:edit", "John")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:7:editor:state", "UA-UA")
                        .param("fields:8:editor:country", "UA")
                        .param("fields:9:editor:edit", "4444444444")
                        .param("addAddress", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageView3 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


            mockMvc.perform(get(redirectFromPrevious(profilePageView3))
                    .session(mockSession)
                    .accept(MediaType.TEXT_HTML)
                    .header(X_CW_TOKEN, xCwToken)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("summaryView-summaryForm")))
                .andExpect(content().string(StringContains.containsString("<p style=\"margin-top: 7px\">bob.doe@account-wicket.com</p>")))
                .andExpect(content().string(StringContains.containsString("value=\"Change login\"")))
                .andExpect(content().string(StringContains.containsString("attributesView-form")))
                .andExpect(content().string(StringContains.containsString("value=\"bob.doe@account-wicket.com\" name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
                .andExpect(content().string(StringContains.containsString("value=\"John\" name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
                .andExpect(content().string(StringContains.containsString("value=\"Arthur\" name=\"fields:3:editor:edit\" placeholder=\"Middle name\"")))
                .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:4:editor:edit\" placeholder=\"Last name\"")))
                .andExpect(content().string(StringContains.containsString("value=\"4444444444\" name=\"fields:5:editor:edit\" placeholder=\"Customer phone\"")))
                .andExpect(content().string(StringContains.containsString("passwordView-passwordForm")))
                .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                .andExpect(content().string(StringContains.containsString("shippingAddressesView-selectAddressForm")))
                .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 col-sm-10 single-address-row\">")))
                .andExpect(content().string(StringContains.containsString("<span>In the middle of  0001 Nowhere UA UA-UA,  John Arthur Doe, 4444444444  bob.doe@account-wicket.com</span>")))
                .andExpect(content().string(StringContains.containsString("billingAddressesView-selectAddressForm")))
                .andExpect(header().string(X_CW_TOKEN, nullValue()))
                .andReturn();


        final MvcResult wishlistPageView =
                mockMvc.perform(get("/wishlist")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<h2 id=\"wishlist\" class=\"profile-title\">Wish list</h2>")))
                    .andExpect(content().string(StringContains.containsString("There are no items in your wish list.")))
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult addSkuToWishList =
                mockMvc.perform(get("/wishlistadd?fc=WAREHOUSE_2&addToWishListCmd=BENDER-ua&supplier=WAREHOUSE_2&type=W")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult skuPage =
                mockMvc.perform(get(redirectFromPrevious(addSkuToWishList))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("Product BENDER-ua added to wish list. You can view wish list in your profile.")))
                    .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\" itemprop=\"productID\">BENDER-ua</span>")))
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


        final MvcResult wishlistPageView2 =
                mockMvc.perform(get("/wishlist")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<h2 id=\"wishlist\" class=\"profile-title\">Wish list</h2>")))
                    .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 col-sm-4 col-md-3 wishlist jsWishlist\" data-visibility=\"P\" data-type=\"W\" data-sku=\"BENDER-ua\" data-fc=\"WAREHOUSE_2\" data-qty=\"1.00\">")))
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final Document wishlistPageView2HTML = Jsoup.parse(wishlistPageView2.getResponse().getContentAsString());

        final Elements logoffCmdBtnElems = wishlistPageView2HTML.select("button[name=\"logoffCmd\"]");
        assertTrue(!logoffCmdBtnElems.isEmpty());
        final String logOffAction = relativeToAbsoluteUrl(logoffCmdBtnElems.get(0).parent().attr("action"));

        final MvcResult logOffSubmit =
            mockMvc.perform(post(logOffAction)
                        .param("logoffCmd", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult homePage =
                mockMvc.perform(get(redirectFromPrevious(logOffSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        assertNull(getCustomerToken(email));
        final String pwHash = getCustomerPasswordHash(email);
        assertNotNull(pwHash);

        final MvcResult loginPage =
                mockMvc.perform(get("/login")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult loginPage2 =
                mockMvc.perform(get(redirectFromPrevious(loginPage))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"login\" placeholder=\"Login\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"password\" placeholder=\"Password\"")))
                    .andExpect(content().string(StringContains.containsString("tryRestorePasswordBtn")))
                    .andExpect(content().string(StringContains.containsString("value=\"Forgot password?\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document loginPage2HTML = Jsoup.parse(loginPage2.getResponse().getContentAsString());

        final MvcResult loginPage3TryRestore =
                mockMvc.perform(post(formAction(loginPage2HTML, "loginForm1"))
                        .param("restorePassword", "")
                        .param("tryRestorePasswordBtn", "Forgot password?")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult loginPage3TryRestoreForm =
                mockMvc.perform(get(redirectFromPrevious(loginPage3TryRestore))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"login\" placeholder=\"Login\"")))
                    .andExpect(content().string(StringContains.containsString("restorePasswordBtn")))
                    .andExpect(content().string(StringContains.containsString("value=\"Restore password\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document loginPage3TryRestoreFormHTML = Jsoup.parse(loginPage3TryRestoreForm.getResponse().getContentAsString());

        final MvcResult loginPage3TryRestoreFormSubmit =
                mockMvc.perform(post(formAction(loginPage3TryRestoreFormHTML, "loginForm1"))
                        .param("login", email)
                        .param("restorePasswordBtn", "Restore password")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult loginPagePasswordNotificationEmailed =
                mockMvc.perform(get(redirectFromPrevious(loginPage3TryRestoreFormSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(content().string(StringContains.containsString("New password request was sent to " + email + ". Check email please.")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final String resetTokenP1 = getCustomerToken(email);
        assertNotNull(resetTokenP1);
        final String pwHashResetP1 = getCustomerPasswordHash(email);
        assertEquals("Phase 1 password not yet chaged, we just sent token via email", pwHash, pwHashResetP1);

        final Mail passReset = getEmailBySubject("Password has been changed", email);
        assertNotNull("Password reset email sent", passReset);
        assertTrue(passReset.getHtmlVersion().contains("You or somebody else requested new password"));
        assertTrue(passReset.getHtmlVersion().contains("https://www.gadget.yescart.org/resetPasswordCmd/"));

        final MvcResult linkFromEmail =
                mockMvc.perform(get("/resetPasswordCmd/" + resetTokenP1)
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(content().string(StringContains.containsString("New password was sent. Check email please.")))
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final String resetTokenP2 = getCustomerToken(email);
        assertNull(resetTokenP2);
        final String pwHashResetP2 = getCustomerPasswordHash(email);
        assertFalse("Phase 2 password is now changed", pwHash.equals(pwHashResetP2));

        final String password = "1234567";
        setCustomerPassword(email, password);

        final MvcResult profilePage5 =
                mockMvc.perform(get("/profile")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final MvcResult loginNewPasswordPage =
                mockMvc.perform(get(redirectFromPrevious(profilePage5))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult loginPageNewPassword2 =
                mockMvc.perform(get(redirectFromPrevious(loginNewPasswordPage))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"login\" placeholder=\"Login\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"password\" placeholder=\"Password\"")))
                    .andExpect(content().string(StringContains.containsString("loginBtn")))
                    .andExpect(content().string(StringContains.containsString("value=\"Sign In\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document loginPageNewPassword2HTML = Jsoup.parse(loginPageNewPassword2.getResponse().getContentAsString());

        final MvcResult loginPage3NewPasswordSubmit =
                mockMvc.perform(post(formAction(loginPageNewPassword2HTML, "loginForm7"))
                        .param("login", email)
                        .param("password", password)
                        .param("loginBtn", "Sign In")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageAfterLogin =
                mockMvc.perform(get(redirectFromPrevious(loginPage3NewPasswordSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageView5 =
                mockMvc.perform(get(redirectFromPrevious(profilePageAfterLogin))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final Document profilePageView5HTML = Jsoup.parse(profilePageView5.getResponse().getContentAsString());

        final MvcResult profilePageDeleteAccount =
                mockMvc.perform(post(formAction(profilePageView5HTML, "accountDeleteForm11"))
                        .param("saveLink", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageDeleteAccountEmailSent =
                mockMvc.perform(get(redirectFromPrevious(profilePageDeleteAccount))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                    .andExpect(content().string(StringContains.containsString("Account deletion request was sent to " + email + ". Check email please.")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();

        final String deleteTokenP1 = getCustomerToken(email);
        assertNotNull(deleteTokenP1);

        final Mail accDelete = getEmailBySubject("Account deletion", email);
        assertNotNull("Account delete email sent", accDelete);
        assertTrue(accDelete.getHtmlVersion().contains("You requested your account (and all data) to be deleted"));
        assertTrue(accDelete.getHtmlVersion().contains("https://www.gadget.yescart.org/deleteAccountCmd/"));


        final MvcResult profilePageDeleteEmailLink =
                mockMvc.perform(get("/deleteAccountCmd/" + deleteTokenP1)
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageDeletePage =
                mockMvc.perform(get(redirectFromPrevious(profilePageDeleteEmailLink))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();

        final MvcResult profilePageDeletePageView =
                mockMvc.perform(get(redirectFromPrevious(profilePageDeletePage))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("authView-deleteForm")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"password\"")))
                    .andExpect(content().string(StringContains.containsString("name=\"deleteBtn\" ")))
                    .andExpect(content().string(StringContains.containsString("value=\"Delete account\"")))
                    .andExpect(header().string(X_CW_TOKEN, nullValue()))
                    .andReturn();


        final Document profilePageDeletePageViewHTML = Jsoup.parse(profilePageDeletePageView.getResponse().getContentAsString());


        final MvcResult profilePageDeleteSubmit =
                mockMvc.perform(post(formAction(profilePageDeletePageViewHTML, "deleteForm1c"))
                        .param("password", password)
                        .param("deleteBtn", "Delete account")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(X_CW_TOKEN, xCwToken))
                    .andReturn();


        assertNull("Account removed", getCustomer(email));


    }



    private String getCustomerToken(final String email) {

        final Customer customer = getCustomer(email);
        if (customer == null) {
            return null;
        }

        return customer.getAuthToken();

    }

    private String getCustomerPasswordHash(final String email) {

        final Customer customer = getCustomer(email);
        if (customer == null) {
            return null;
        }

        return customer.getPassword();

    }

    private Customer getCustomer(final String email) {

        final List<Customer> customers = this.customerService.findByCriteria(" where e.email = ?1", email);
        if (customers.isEmpty()) {
            return null;
        }

        return customers.get(0);

    }

    private void setCustomerPassword(final String email, final String password) throws Exception {

        final List<Customer> customers = this.customerService.findByCriteria(" where e.email = ?1", email);
        if (!customers.isEmpty()) {

            final Customer customer = customers.get(0);
            customer.setPassword(hashHelper.getHash(password));

            this.customerService.update(customer);

        }

    }

}
