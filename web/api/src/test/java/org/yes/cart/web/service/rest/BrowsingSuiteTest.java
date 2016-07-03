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
import org.yes.cart.domain.ro.SearchRO;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.YcMockMvcResultHandlers.print;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 19:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/test/webapp")
public class BrowsingSuiteTest extends AbstractSuiteTest {

    private final Locale locale = Locale.ENGLISH;

    @Autowired
    private ShoppingCartStateService shoppingCartStateService;

    @Autowired
    private CartRepository cartRepository;


    @Test
    public void testCategoryJson() throws Exception {

        mockMvc.perform(get("/category/menu")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/category/menu/106")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/category/view/106")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }

    @Test
    public void testCategoryXML() throws Exception {

        mockMvc.perform(get("/category/menu")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/category/menu/106")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/category/view/106")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }



    @Test
    public void testProductJson() throws Exception {

        reindex();


        final MvcResult firstLoad =
        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = firstLoad.getResponse().getHeader("yc");

        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertNull(state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertNull(cart.getCustomerEmail());


        mockMvc.perform(get("/product/9998")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/products/9998|9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"BENDER-ua\"")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("\"BENDER\"")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/sku/9998")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/sku/BENDER-ua")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/9998|9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/product/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/sku/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/product/9998/associations/accessories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/recentlyviewed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));


    }


    @Test
    public void testProductXML() throws Exception {

        reindex();

        final MvcResult firstLoad =
        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = firstLoad.getResponse().getHeader("yc");


        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertNull(state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertNull(cart.getCustomerEmail());


        mockMvc.perform(get("/product/9998")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/products/9998|9999")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(">BENDER-ua<")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString(">BENDER<")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/sku/9998")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/sku/BENDER-ua")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/9998|9999")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(">BENDER-ua<")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString(">BENDER<")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/product/9999")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/sku/9999")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/product/9998/associations/accessories")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/recentlyviewed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", uuid));


    }

    @Test
    public void testSearchJson() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();

        final byte[] body = toJsonBytes(search);

        mockMvc.perform(put("/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }

    @Test
    public void testSearchXML() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();

        final byte[] body = toJsonBytes(search);


        mockMvc.perform(put("/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }


}
