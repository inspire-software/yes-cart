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
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerRole;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.ro.LoginRO;
import org.yes.cart.domain.ro.SearchRO;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.YcMockMvcResultHandlers.print;

/**
 * User: denispavlov
 * Date: 27/10/2019
 * Time: 12:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/test/webapp")
public class ManagerSuiteTest extends AbstractSuiteTest {

    private final Locale locale = Locale.ENGLISH;


    @Autowired
    private ShoppingCartStateService shoppingCartStateService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private HashHelper hashHelper;

    private String createManagerWithSfAccess(final long shopId, final String password) throws Exception {

        final String email = UUID.randomUUID().toString() + "@manager.com";

        final Manager manager = managerService.getGenericDao().getEntityFactory().getByIface(Manager.class);
        manager.setEmail(email);
        manager.setPassword(hashHelper.getHash(password));
        manager.setFirstname("John");
        manager.setLastname("Doe");
        manager.setEnabled(true);

        managerService.create(
                manager,
                shopService.getById(shopId),
                "ROLE_SMCALLCENTERLOGINSF", "ROLE_SMCALLCENTERLOGINONBEHALF", "ROLE_SMCALLCENTERCREATEMANAGEDLISTS"
        );

        return email;

    }

    @Test
    public void testManagerLoginOnBehalfJson() throws Exception {

        final String password = "123456789";
        final String email = createManagerWithSfAccess(10L, password);

        final LoginRO login = new LoginRO();
        login.setUsername(email);
        login.setPassword(password);
        final byte[] loginBody = toJsonBytes(login);

        final MvcResult loginResult =
                mockMvc.perform(put("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .locale(locale)
                        .content(loginBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                    .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                    .andReturn();

        final String uuid = loginResult.getResponse().getHeader("yc");


        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"managedCart\":false")))
                .andExpect(content().string(StringContains.containsString("\"customerEmail\":\"" + email + "\"")))
                .andExpect(content().string(StringContains.containsString("\"customerType\":\"MGR\"")))
                .andExpect(content().string(StringContains.containsString("\"managerCreateManagedListsEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"managerLoginOnBehalfEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"blockCheckoutType\":\"true\"")))
                .andExpect(header().string("yc", uuid));


        final SearchRO search = new SearchRO();
        search.setParameters(Collections.singletonMap("any", Collections.singletonList("reg@test.com")));

        final byte[] body = toJsonBytes(search);

        mockMvc.perform(post("/management/customers/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .content(body)
                .header("yc", uuid))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("reg@test.com")))
            .andExpect(content().string(StringContains.containsString("John")))
            .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        final LoginRO loginOnBehalf = new LoginRO();
        loginOnBehalf.setUsername("reg@test.com");
        final byte[] loginOnBehalfBody = toJsonBytes(loginOnBehalf);

        mockMvc.perform(put("/auth/login?customer=true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .content(loginOnBehalfBody)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"managedCart\":true")))
                .andExpect(content().string(StringContains.containsString("\"managerEmail\":\"" + email + "\"")))
                .andExpect(content().string(StringContains.containsString("\"customerEmail\":\"reg@test.com\"")))
                .andExpect(content().string(StringContains.containsString("\"customerType\":\"TEST\"")))
                .andExpect(content().string(StringContains.containsString("\"managerCreateManagedListsEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"managerLoginOnBehalfEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"blockCheckoutType\":\"false\"")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(put("/auth/logout?customer=true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"managedCart\":false")))
                .andExpect(content().string(StringContains.containsString("\"customerEmail\":\"" + email + "\"")))
                .andExpect(content().string(StringContains.containsString("\"customerType\":\"MGR\"")))
                .andExpect(content().string(StringContains.containsString("\"managerCreateManagedListsEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"managerLoginOnBehalfEnabled\":\"true\"")))
                .andExpect(content().string(StringContains.containsString("\"blockCheckoutType\":\"true\"")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(put("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":false")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


    }
}
