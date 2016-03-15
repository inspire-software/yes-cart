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

import org.hamcrest.CustomMatchers;
import org.junit.Test;
import org.junit.internal.matchers.StringContains;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        final byte[] regBody = toJsonBytesRegistrationDetails("bob.doe@yc-account-json.com");


        final MvcResult regResult =
        mockMvc.perform(put("/auth/register")
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
        assertEquals("bob.doe@yc-account-json.com", state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertEquals("bob.doe@yc-account-json.com", cart.getCustomerEmail());


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
                .andExpect(content().string(StringContains.containsString("bob.doe@yc-account-json.com")))
                .andExpect(header().string("yc", uuid));


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

        mockMvc.perform(get("/customer/addressbook/S/options/country/UA")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        mockMvc.perform(put("/customer/addressbook/S")
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

        mockMvc.perform(get("/customer/addressbook/B/options/country/GB")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB-GB")))
            .andExpect(header().string("yc", uuid));


        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        mockMvc.perform(put("/customer/addressbook/B")
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

        final byte[] addToWishList = toJsonAddToWishListCommand("BENDER-ua");

         mockMvc.perform(put("/cart")
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
                .andExpect(header().string("yc", uuid));


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


        final byte[] regBody = toJsonBytesRegistrationDetails("bob.doe@yc-account-xml.com");


        final MvcResult regResult =
        mockMvc.perform(put("/auth/register")
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
        assertEquals("bob.doe@yc-account-xml.com", state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertEquals("bob.doe@yc-account-xml.com", cart.getCustomerEmail());

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
                .andExpect(content().string(StringContains.containsString("bob.doe@yc-account-xml.com")))
                .andExpect(header().string("yc", uuid));


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

        mockMvc.perform(get("/customer/addressbook/S/options/country/UA")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andExpect(header().string("yc", uuid));


        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        mockMvc.perform(put("/customer/addressbook/S")
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

        mockMvc.perform(get("/customer/addressbook/B/options/country/GB")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("GB-GB")))
            .andExpect(header().string("yc", uuid));


        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        mockMvc.perform(put("/customer/addressbook/B")
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

        final byte[] addToWishList = toJsonAddToWishListCommand("BENDER-ua");

         mockMvc.perform(put("/cart")
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
                .andExpect(header().string("yc", uuid));


    }

    private byte[] toJsonAddToWishListCommand(final String sku) throws Exception {

        final Map<String, String> addToWishList = new HashMap<String, String>();
        addToWishList.put(ShoppingCartCommand.CMD_ADDTOWISHLIST, sku);
        addToWishList.put(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_QTY, "10");

        return toJsonBytes(addToWishList);

    }




}
