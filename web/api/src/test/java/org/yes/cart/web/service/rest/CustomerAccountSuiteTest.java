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

import org.hamcrest.CustomMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.domain.ro.LoginRO;
import org.yes.cart.domain.ro.xml.XMLParamsRO;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.HashHelper;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.YcMockMvcResultHandlers.print;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 11:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/test/webapp")
public class CustomerAccountSuiteTest extends AbstractSuiteTest {

    private final Locale locale = Locale.ENGLISH;


    @Autowired
    private ShoppingCartStateService shoppingCartStateService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HashHelper hashHelper;


    @Test
    public void testCustomerJson() throws Exception {

        reindex();

        mockMvc.perform(get("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("custom")))
                .andExpect(content().string(StringContains.containsString("customerType")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/auth/register?customerType=B2C")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("custom")))
                .andExpect(content().string(StringContains.containsString("firstname")))
                .andExpect(content().string(StringContains.containsString("attributeType\":\"String")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/customer/addressbook/B/form")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("custom\":null")))
                .andExpect(content().string(StringContains.containsString("addressType\":\"B")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        final String email = "bob.doe@yc-account-json.com";
        final byte[] regBody = toJsonBytesRegistrationDetails(email);


        final MvcResult regResult =
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .content(regBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("uuid")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = regResult.getResponse().getHeader("yc");

        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertEquals(email, state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertEquals(email, cart.getCustomerEmail());


        mockMvc.perform(get("/auth/check")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(email)))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/addressbook/B/form")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("custom\":[")))
                .andExpect(content().string(StringContains.containsString("\"attributeCode\":\"lastname\"")))
                .andExpect(content().string(StringContains.containsString("addressType\":\"B")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("[]")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/S/options/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/S/options/countries/UA")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        mockMvc.perform(post("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(shippingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("[]")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/addressbook/B/options/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB")))
            .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/B/options/countries/GB")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB-GB")))
            .andExpect(header().string("yc", uuid));


        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        mockMvc.perform(post("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(billingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/wishlist/W")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("[]")))
            .andExpect(header().string("yc", uuid));

        final byte[] addToWishList = toJsonAddToWishListCommand("BENDER-ua", "WAREHOUSE_2");

         mockMvc.perform(post("/cart")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .locale(locale)
                     .header("yc", uuid)
                     .content(addToWishList))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

         mockMvc.perform(get("/customer/wishlist/W")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .locale(locale)
                     .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk());

        
        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":false")))
                .andExpect(header().string("yc", uuid));

        assertNull(getCustomerToken(email));
        final String pwHash = getCustomerPasswordHash(email);
        assertNotNull(pwHash);

        mockMvc.perform(post("/auth/resetpassword?email=" + email)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

        final String resetTokenP1 = getCustomerToken(email);
        assertNotNull(resetTokenP1);
        final String pwHashResetP1 = getCustomerPasswordHash(email);
        assertEquals("Phase 1 password not yet chaged, we just sent token via email", pwHash, pwHashResetP1);

        mockMvc.perform(post("/auth/resetpassword?email=" + email + "&authToken=" + resetTokenP1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

        final String resetTokenP2 = getCustomerToken(email);
        assertNull(resetTokenP2);
        final String pwHashResetP2 = getCustomerPasswordHash(email);
        assertFalse("Phase 2 password is now changed", pwHash.equals(pwHashResetP2));

        final String password = "1234567";
        setCustomerPassword(email, password);

        final LoginRO login = new LoginRO();
        login.setUsername(email);
        login.setPassword(password);
        final byte[] loginBody = toJsonBytes(login);

        final MvcResult loginResult =
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .content(loginBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid2 = loginResult.getResponse().getHeader("yc");

        assertNull(getCustomerToken(email));

        mockMvc.perform(post("/auth/deleteaccount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid2));

        final String deleteTokenP1 = getCustomerToken(email);
        assertNotNull(deleteTokenP1);


        mockMvc.perform(post("/auth/deleteaccount?authToken=" + deleteTokenP1 + "&password=" + password)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid2));


        assertNull("Account removed", getCustomer(email));


    }

    @Test
    public void testCustomerXML() throws Exception {

        reindex();



        mockMvc.perform(get("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<custom")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/auth/register?customerType=B2C")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("custom")))
                .andExpect(content().string(StringContains.containsString("firstname")))
                .andExpect(content().string(StringContains.containsString("attribute-type=\"String\"")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/customer/addressbook/B/form")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<address-form address-type=\"B\"/>")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        final String email = "bob.doe@yc-account-xml.com";
        final byte[] regBody = toJsonBytesRegistrationDetails(email);


        final MvcResult regResult =
        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .content(regBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("uuid")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = regResult.getResponse().getHeader("yc");

        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertEquals(email, state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertEquals(email, cart.getCustomerEmail());

        mockMvc.perform(get("/auth/check")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<authenticated>true</authenticated>")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(email)))
                .andExpect(header().string("yc", uuid));



        mockMvc.perform(get("/customer/addressbook/B/form")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<custom><")))
                .andExpect(content().string(StringContains.containsString("attribute-code=\"lastname\"")))
                .andExpect(content().string(StringContains.containsString("address-type=\"B\"")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<addresses/>")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/S/options/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/S/options/countries/UA")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        mockMvc.perform(post("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(shippingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<addresses/>")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/addressbook/B/options/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB")))
            .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/addressbook/B/options/countries/GB")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB-GB")))
            .andExpect(header().string("yc", uuid));


        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        mockMvc.perform(post("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(billingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/customer/wishlist/W")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<wishlist/>")))
            .andExpect(header().string("yc", uuid));

        final byte[] addToWishList = toJsonAddToWishListCommand("BENDER-ua", "WAREHOUSE_2");

         mockMvc.perform(post("/cart")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_XML)
                     .locale(locale)
                     .header("yc", uuid)
                     .content(addToWishList))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

         mockMvc.perform(get("/customer/wishlist/W")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_XML)
                     .locale(locale)
                     .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<authenticated>true</authenticated>")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk());


        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<authenticated>false</authenticated>")))
                .andExpect(header().string("yc", uuid));


        assertNull(getCustomerToken(email));
        final String pwHash = getCustomerPasswordHash(email);
        assertNotNull(pwHash);

        mockMvc.perform(post("/auth/resetpassword?email=" + email)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

        final String resetTokenP1 = getCustomerToken(email);
        assertNotNull(resetTokenP1);
        final String pwHashResetP1 = getCustomerPasswordHash(email);
        assertEquals("Phase 1 password not yet chaged, we just sent token via email", pwHash, pwHashResetP1);

        mockMvc.perform(post("/auth/resetpassword?email=" + email + "&authToken=" + resetTokenP1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid));

        final String resetTokenP2 = getCustomerToken(email);
        assertNull(resetTokenP2);
        final String pwHashResetP2 = getCustomerPasswordHash(email);
        assertFalse("Phase 2 password is now changed", pwHash.equals(pwHashResetP2));

        final String password = "1234567";
        setCustomerPassword(email, password);

        final LoginRO login = new LoginRO();
        login.setUsername(email);
        login.setPassword(password);
        final byte[] loginBody = toJsonBytes(login);

        final MvcResult loginResult =
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .content(loginBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<authenticated>true</authenticated>")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid2 = loginResult.getResponse().getHeader("yc");

        assertNull(getCustomerToken(email));

        mockMvc.perform(post("/auth/deleteaccount")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid2));

        final String deleteTokenP1 = getCustomerToken(email);
        assertNotNull(deleteTokenP1);


        mockMvc.perform(post("/auth/deleteaccount?authToken=" + deleteTokenP1 + "&password=" + password)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", uuid2));


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

    private byte[] toJsonAddToWishListCommand(final String sku, final String supplier) throws Exception {

        final Map<String, String> addToWishList = new HashMap<String, String>();
        addToWishList.put(ShoppingCartCommand.CMD_ADDTOWISHLIST, sku);
        addToWishList.put(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        addToWishList.put(ShoppingCartCommand.CMD_P_QTY, "10");

        return toJsonBytes(new XMLParamsRO(addToWishList));

    }




}
