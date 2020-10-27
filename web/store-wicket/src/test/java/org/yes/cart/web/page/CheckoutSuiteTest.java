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

import org.hamcrest.core.StringContains;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.Mail;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.CustomMockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: inspiresoftware
 * Date: 26/10/2020
 * Time: 16:57
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/main/webapp")
public class CheckoutSuiteTest extends AbstractSuiteTest {

    private MockHttpSession mockSession;

    @Test
    public void testCheckout() throws Exception {

        reindex();

        final MvcResult start =
        mockMvc.perform(get("/registration")
                .accept(MediaType.TEXT_HTML)
                .locale(LOCALE))
            .andDo(print()).andDo((res) -> {
                mockSession = (MockHttpSession) res.getRequest().getSession();
            })
            .andExpect(status().is3xxRedirection())
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
            .andReturn();

        final Document registrationPageHTML = Jsoup.parse(registrationPage.getResponse().getContentAsString());

        final String email = "bob.doe@checkout-wicket.com";

        assertFalse("Customer not yet registered", hasEmails(email));

        final MvcResult registrationPageSubmit =
        mockMvc.perform(post(formAction(registrationPageHTML, "registerForm1"))
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
                    .andReturn();

        final MvcResult profilePage =
                mockMvc.perform(get(redirectFromPrevious(registrationPageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
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
                    .andExpect(content().string(StringContains.containsString("<p style=\"margin-top: 7px\">bob.doe@checkout-wicket.com</p>")))
                    .andExpect(content().string(StringContains.containsString("value=\"Change login\"")))
                    .andExpect(content().string(StringContains.containsString("attributesView-form")))
                    .andExpect(content().string(StringContains.containsString("<form class=\"form-horizontal\" id=\"form7\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"bob.doe@checkout-wicket.com\" name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Middle name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:4:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"123123123\" name=\"fields:5:editor:edit\" placeholder=\"Customer phone\"")))
                    .andExpect(content().string(StringContains.containsString("passwordView-passwordForm")))
                    .andExpect(content().string(StringContains.containsString("deleteView-accountDeleteForm")))
                    .andExpect(content().string(StringContains.containsString("shippingAddressesView-selectAddressForm")))
                    .andExpect(content().string(StringContains.containsString("billingAddressesView-selectAddressForm")))
                    .andReturn();


        final Mail registration = getEmailBySubject("Registration email", email);
        assertNotNull("Registration email sent", registration);
        assertTrue(registration.getHtmlVersion().contains("Bob registered"));

        mockMvc.perform(get("/category/104/fc/WAREHOUSE_2/product/bender-bending-rodriguez")
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("product-detail-holder\" itemscope itemtype=\"http://schema.org/Product\">")))
            .andExpect(content().string(StringContains.containsString("product-detail-img-holder js-product-image-view")))
            .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\" itemprop=\"productID\">BENDER-ua</span>")))
            .andExpect(content().string(StringContains.containsString("js-buy\" rel=\"nofollow\" href=\"./bender-bending-rodriguez/addToCartCmd/BENDER-ua/supplier/WAREHOUSE_2\"")))
            .andExpect(content().string(StringContains.containsString("<span class=\"regular-price-whole\">99</span><span class=\"regular-price-dot\">.</span><span class=\"regular-price-decimal\">99</span>")))
            .andExpect(content().string(StringContains.containsString("<div class=\"product-detail-tabs\">")))
            .andExpect(content().string(StringContains.containsString("<div class=\"attr-head\">DVD Players view group</div>")))
            .andExpect(content().string(StringContains.containsString("<span>Weight</span>")))
            .andExpect(content().string(StringContains.containsString("<span class=\"pull-right\">1.15</span>")));

        final MvcResult addItemToCart =
        mockMvc.perform(get("/category/104/fc/WAREHOUSE_2/product/bender-bending-rodriguez/addToCartCmd/BENDER-ua/supplier/WAREHOUSE_2")
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart orange\"></span>")))
            .andExpect(content().string(StringContains.containsString("<span>(1)</span>")))
            .andReturn();

        mockMvc.perform(get("/cart")
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart orange\"></span>")))
            .andExpect(content().string(StringContains.containsString("<span>(1)</span>")))
            .andExpect(content().string(StringContains.containsString("<div class=\"shopping-cart-details\">")))
            .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\">BENDER-ua</span>")))
            .andReturn();

        final MvcResult checkoutStart =
        mockMvc.perform(get("/checkout")
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andReturn();

        final MvcResult checkoutStep2Address =
        mockMvc.perform(get(redirectFromPrevious(checkoutStart))
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andReturn();


        final MvcResult shippingAddressCreatePageView =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep2Address))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Please provide delivery address</span>")))
                    .andExpect(content().string(StringContains.containsString("addressForm18")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"123123123\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andReturn();


        final Document shippingAddressCreatePageViewHTML = Jsoup.parse(shippingAddressCreatePageView.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmitSelectCountry =
                mockMvc.perform(post(formSelect(shippingAddressCreatePageViewHTML, "addressForm18", "fields:8:editor:country"))
                        .param("fields:1:editor:edit", "Bob")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:8:editor:country", "GB")
                        .param("fields:9:editor:edit", "123123123")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult shippingAddressCreatePageView2 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmitSelectCountry))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("addressForm18")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"In the middle of\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Nowhere\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"0001\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:7:editor:state\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"GB-GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"123123123\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andReturn();

        final Document shippingAddressCreatePageView2HTML = Jsoup.parse(shippingAddressCreatePageView2.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmit =
                mockMvc.perform(post(formAction(shippingAddressCreatePageView2HTML, "addressForm18"))
                        .param("fields:1:editor:edit", "Bob")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:7:editor:state", "GB-GB")
                        .param("fields:8:editor:country", "GB")
                        .param("fields:9:editor:edit", "123123123")
                        .param("addAddress", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep2Address2 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        mockMvc.perform(get(redirectFromPrevious(checkoutStep2Address2))
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
            .andExpect(content().string(StringContains.containsString("shippingAddress-selectAddressForm")))
            .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 col-sm-10 single-address-row\">")))
            .andExpect(content().string(StringContains.containsString("<span>In the middle of  0001 Nowhere GB GB-GB,  Bob  Doe, 123123123  bob.doe@checkout-wicket.com</span>")))
            .andReturn();

        final MvcResult checkoutStep3Shipping =
                mockMvc.perform(get("/checkout?thp=true&step=ship")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep3Shipping2 =
            mockMvc.perform(get(redirectFromPrevious(checkoutStep3Shipping))
                    .session(mockSession)
                    .accept(MediaType.TEXT_HTML)
                    .header(X_CW_TOKEN, xCwToken)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                .andExpect(content().string(StringContains.containsString("<h2>Shipping method</h2>")))
                .andExpect(content().string(StringContains.containsString("shippingForm22")))
                .andExpect(content().string(StringContains.containsString("content-shipmentView-deliveryList")))
                .andReturn();

        final Document checkoutStep3Shipping2HTML = Jsoup.parse(checkoutStep3Shipping2.getResponse().getContentAsString());

        final MvcResult checkoutStep3ShippingSubmitSelectShipping =
                mockMvc.perform(post(formRadio(checkoutStep3Shipping2HTML, "shippingForm22", "carrierSla"))
                        .param("carrierSla", "radio0")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep3Shipping3 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep3ShippingSubmitSelectShipping))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("<h2>Shipping method</h2>")))
                    .andExpect(content().string(StringContains.containsString("Shipping cost")))
                    .andReturn();


        final MvcResult checkoutStep4Payment =
                mockMvc.perform(get("/checkout?thp=true&step=payment")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep4Payment2 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep4Payment))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("<label>Order total (inc. tax)</label>")))
                    .andExpect(content().string(StringContains.containsString("In the middle of  0001 Nowhere GB GB-GB,  Bob  Doe, 123123123  bob.doe@checkout-wicket.com")))
                    .andExpect(content().string(StringContains.containsString("paymentOptionsForm2a")))
                    .andExpect(content().string(StringContains.containsString("courierPaymentGateway")))
                    .andReturn();


        final Document checkoutStep4Payment2HTML = Jsoup.parse(checkoutStep4Payment2.getResponse().getContentAsString());

        final MvcResult checkoutStep4PaymentSubmitSelectPayment =
                mockMvc.perform(post(formRadio(checkoutStep4Payment2HTML, "paymentOptionsForm2a", "paymentGateway"))
                        .param("paymentGateway", "radio0")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep4Payment3 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep4PaymentSubmitSelectPayment))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("checked=\"checked\"")))
                    .andExpect(content().string(StringContains.containsString("paymentDiv")))
                    .andReturn();

        final MvcResult checkoutComplete =
                mockMvc.perform(post("/payment")
                        .param("submit", "Place order")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart default\"></span>")))
                    .andExpect(content().string(StringContains.containsString("<span>(0)</span>")))
                    .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 payment-result-holder\">")))
                    .andReturn();

        final String xCwToken2 = checkoutComplete.getResponse().getHeader(X_CW_TOKEN);

        final Mail newOrder = getEmailBySubjectLike( "New order", email);
        assertNotNull("New order email", newOrder);
        assertTrue(newOrder.getHtmlVersion().contains("Dear <b>Bob Doe</b>!<br>"));
        assertTrue(newOrder.getHtmlVersion().contains("New order"));
        assertTrue(newOrder.getHtmlVersion().contains("BENDER-ua"));
        assertTrue(newOrder.getHtmlVersion().contains("<td align=\"right\"><b>109.99</b></td>"));
        assertTrue(newOrder.getHtmlVersion().contains("https://www.gadget.yescart.org/order?order="));

        final MvcResult orderHistory =
                mockMvc.perform(get("/orders")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken2)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<h2 id=\"orderInfo\" class=\"profile-title\">Orders</h2>")))
                    .andExpect(content().string(StringContains.containsString("<a rel=\"nofollow\" href=\"./order?order=")))
                    .andExpect(content().string(StringContains.containsString("<span>Awaiting confirmation</span>")))
                    .andReturn();

        final Document orderHistoryHTML = Jsoup.parse(orderHistory.getResponse().getContentAsString());

        final Elements orderLinkElems = orderHistoryHTML.select("a[href^=\"./order?order=\"]");
        final String orderLink = !orderLinkElems.isEmpty() ? orderLinkElems.get(0).attr("href") : null;
        assertNotNull(orderLink);

        final MvcResult orderView =
                mockMvc.perform(get(relativeToAbsoluteUrl(orderLink))
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken2)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Order")))
                    .andExpect(content().string(StringContains.containsString("<span>Awaiting confirmation</span>")))
                    .andExpect(content().string(StringContains.containsString("Delivery number")))
                    .andExpect(content().string(StringContains.containsString("<span>BENDER-ua</span>")))
                    .andReturn();


    }


    @Test
    public void testGuestCheckout() throws Exception {

        reindex();

        final MvcResult start =
        mockMvc.perform(get("/category/104/fc/WAREHOUSE_2/product/bender-bending-rodriguez")
                .accept(MediaType.TEXT_HTML)
                .locale(LOCALE))
            .andDo(print()).andDo((res) -> {
                mockSession = (MockHttpSession) res.getRequest().getSession();
            })
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("product-detail-holder\" itemscope itemtype=\"http://schema.org/Product\">")))
            .andExpect(content().string(StringContains.containsString("product-detail-img-holder js-product-image-view")))
            .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\" itemprop=\"productID\">BENDER-ua</span>")))
            .andExpect(content().string(StringContains.containsString("js-buy\" rel=\"nofollow\" href=\"./bender-bending-rodriguez/addToCartCmd/BENDER-ua/supplier/WAREHOUSE_2\"")))
            .andExpect(content().string(StringContains.containsString("<span class=\"regular-price-whole\">99</span><span class=\"regular-price-dot\">.</span><span class=\"regular-price-decimal\">99</span>")))
            .andExpect(content().string(StringContains.containsString("<div class=\"product-detail-tabs\">")))
            .andExpect(content().string(StringContains.containsString("<div class=\"attr-head\">DVD Players view group</div>")))
            .andExpect(content().string(StringContains.containsString("<span>Weight</span>")))
            .andExpect(content().string(StringContains.containsString("<span class=\"pull-right\">1.15</span>")))
            .andReturn();

        final String xCwToken = start.getResponse().getHeader(X_CW_TOKEN);
        assertNotNull(xCwToken);

        final MvcResult addItemToCart =
                mockMvc.perform(get("/category/104/fc/WAREHOUSE_2/product/bender-bending-rodriguez/addToCartCmd/BENDER-ua/supplier/WAREHOUSE_2")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart orange\"></span>")))
                    .andExpect(content().string(StringContains.containsString("<span>(1)</span>")))
                    .andReturn();

        mockMvc.perform(get("/cart")
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart orange\"></span>")))
            .andExpect(content().string(StringContains.containsString("<span>(1)</span>")))
            .andExpect(content().string(StringContains.containsString("<div class=\"shopping-cart-details\">")))
            .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\">BENDER-ua</span>")))
            .andReturn();

        final MvcResult checkoutStart =
                mockMvc.perform(get("/checkout")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep1Login =
                mockMvc.perform(get(redirectFromPrevious(checkoutStart))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<h2>Guest checkout</h2>")))
                    .andExpect(content().string(StringContains.containsString("content-guestView-guestForm")))
                    .andExpect(content().string(StringContains.containsString("guestForm9")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:1:editor:edit\" placeholder=\"Customer email\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:2:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Last name\"")))
                    .andReturn();

        final String email = "bob.doe@guest-checkout-wicket.com";

        assertFalse("Customer not yet registered", hasEmails(email));

        final Document registrationPageHTML = Jsoup.parse(checkoutStep1Login.getResponse().getContentAsString());

        final MvcResult registrationPageSubmit =
                mockMvc.perform(post(formAction(registrationPageHTML, "guestForm9"))
                        .param("fields:1:editor:edit", email)
                        .param("fields:2:editor:edit", "Bob")
                        .param("fields:3:editor:edit", "Doe")
                        .param("guestBtn", "Checkout")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep2Address =
                mockMvc.perform(get(redirectFromPrevious(registrationPageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult shippingAddressCreatePageView =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep2Address))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                        .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span>Please provide delivery address</span>")))
                    .andExpect(content().string(StringContains.containsString("addressFormd")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andReturn();


        final Document shippingAddressCreatePageViewHTML = Jsoup.parse(shippingAddressCreatePageView.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmitSelectCountry =
                mockMvc.perform(post(formSelect(shippingAddressCreatePageViewHTML, "addressFormd", "fields:8:editor:country"))
                        .param("fields:1:editor:edit", "Bob")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:8:editor:country", "GB")
                        .param("fields:9:editor:edit", "123123123")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult shippingAddressCreatePageView2 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmitSelectCountry))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("addressFormd")))
                    .andExpect(content().string(StringContains.containsString("value=\"Bob\" name=\"fields:1:editor:edit\" placeholder=\"First name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Doe\" name=\"fields:2:editor:edit\" placeholder=\"Last name\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"In the middle of\" name=\"fields:3:editor:edit\" placeholder=\"Address line 1\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"\" name=\"fields:4:editor:edit\" placeholder=\"Address line 2\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"Nowhere\" name=\"fields:5:editor:edit\" placeholder=\"City\"")))
                    .andExpect(content().string(StringContains.containsString("value=\"0001\" name=\"fields:6:editor:edit\" placeholder=\"Post code\"")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:7:editor:state\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"\">Choose One</option>")))
                    .andExpect(content().string(StringContains.containsString("<option value=\"GB-GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("<select class=\"form-control\" name=\"fields:8:editor:country\"")))
                    .andExpect(content().string(StringContains.containsString("<option selected=\"selected\" value=\"GB\">United Kingdom</option>")))
                    .andExpect(content().string(StringContains.containsString("value=\"123123123\" name=\"fields:9:editor:edit\" placeholder=\"Phone\"")))
                    .andReturn();

        final Document shippingAddressCreatePageView2HTML = Jsoup.parse(shippingAddressCreatePageView2.getResponse().getContentAsString());

        final MvcResult shippingAddressCreatePageSubmit =
                mockMvc.perform(post(formAction(shippingAddressCreatePageView2HTML, "addressFormd"))
                        .param("fields:1:editor:edit", "Bob")
                        .param("fields:2:editor:edit", "Doe")
                        .param("fields:3:editor:edit", "In the middle of")
                        .param("fields:5:editor:edit", "Nowhere")
                        .param("fields:6:editor:edit", "0001")
                        .param("fields:7:editor:state", "GB-GB")
                        .param("fields:8:editor:country", "GB")
                        .param("fields:9:editor:edit", "123123123")
                        .param("addAddress", "x")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep2Address2 =
                mockMvc.perform(get(redirectFromPrevious(shippingAddressCreatePageSubmit))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        mockMvc.perform(get(redirectFromPrevious(checkoutStep2Address2))
                .session(mockSession)
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
            .andExpect(content().string(StringContains.containsString("shippingAddress-selectAddressForm")))
            .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 col-sm-10 single-address-row\">")))
            .andExpect(content().string(StringContains.containsString("<span>In the middle of  0001 Nowhere GB GB-GB,  Bob  Doe, 123123123  bob.doe@guest-checkout-wicket.com</span>")))
            .andReturn();

        final MvcResult checkoutStep3Shipping =
                mockMvc.perform(get("/checkout?thp=true&step=ship")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep3Shipping2 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep3Shipping))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("<h2>Shipping method</h2>")))
                    .andExpect(content().string(StringContains.containsString("shippingForm11")))
                    .andExpect(content().string(StringContains.containsString("content-shipmentView-deliveryList")))
                    .andReturn();

        final Document checkoutStep3Shipping2HTML = Jsoup.parse(checkoutStep3Shipping2.getResponse().getContentAsString());

        final MvcResult checkoutStep3ShippingSubmitSelectShipping =
                mockMvc.perform(post(formRadio(checkoutStep3Shipping2HTML, "shippingForm11", "carrierSla"))
                        .param("carrierSla", "radio0")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep3Shipping3 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep3ShippingSubmitSelectShipping))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("<h2>Shipping method</h2>")))
                    .andExpect(content().string(StringContains.containsString("Shipping cost")))
                    .andReturn();


        final MvcResult checkoutStep4Payment =
                mockMvc.perform(get("/checkout?thp=true&step=payment")
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();

        final MvcResult checkoutStep4Payment2 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep4Payment))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("<label>Order total (inc. tax)</label>")))
                    .andExpect(content().string(StringContains.containsString("In the middle of  0001 Nowhere GB GB-GB,  Bob  Doe, 123123123  bob.doe@guest-checkout-wicket.com")))
                    .andExpect(content().string(StringContains.containsString("paymentOptionsForm15")))
                    .andExpect(content().string(StringContains.containsString("courierPaymentGateway")))
                    .andReturn();


        final Document checkoutStep4Payment2HTML = Jsoup.parse(checkoutStep4Payment2.getResponse().getContentAsString());

        final MvcResult checkoutStep4PaymentSubmitSelectPayment =
                mockMvc.perform(post(formRadio(checkoutStep4Payment2HTML, "paymentOptionsForm15", "paymentGateway"))
                        .param("paymentGateway", "radio0")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andReturn();


        final MvcResult checkoutStep4Payment3 =
                mockMvc.perform(get(redirectFromPrevious(checkoutStep4PaymentSubmitSelectPayment))
                        .session(mockSession)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<div class=\"row checkout-progress-bar\">")))
                    .andExpect(content().string(StringContains.containsString("checked=\"checked\"")))
                    .andExpect(content().string(StringContains.containsString("paymentDiv")))
                    .andReturn();

        final MvcResult checkoutComplete =
                mockMvc.perform(post("/payment")
                        .param("submit", "Place order")
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .header(X_CW_TOKEN, xCwToken)
                        .locale(LOCALE))
                    .andDo(print()).andDo((res) -> {
                        mockSession = (MockHttpSession) res.getRequest().getSession();
                    })
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("<span class=\"glyphicon glyphicon-shopping-cart default\"></span>")))
                    .andExpect(content().string(StringContains.containsString("<span>(0)</span>")))
                    .andExpect(content().string(StringContains.containsString("<div class=\"col-xs-12 payment-result-holder\">")))
                    .andReturn();

        final Mail newOrder = getEmailBySubjectLike( "New order", email);
        assertNotNull("New order email", newOrder);
        assertTrue(newOrder.getHtmlVersion().contains("Dear <b>Bob Doe</b>!<br>"));
        assertTrue(newOrder.getHtmlVersion().contains("New order"));
        assertTrue(newOrder.getHtmlVersion().contains("BENDER-ua"));
        assertTrue(newOrder.getHtmlVersion().contains("<td align=\"right\"><b>109.99</b></td>"));
        assertTrue(newOrder.getHtmlVersion().contains("https://www.gadget.yescart.org/order?order="));


    }
}
