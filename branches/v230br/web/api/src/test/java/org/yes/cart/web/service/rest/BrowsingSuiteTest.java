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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.StringContains;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.ro.SearchRO;

import javax.annotation.Resource;
import javax.servlet.Filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 19:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration
public class BrowsingSuiteTest extends AbstractTestDAO {

    @Resource(name = "shopResolverFilter")
    private Filter shopResolverFilter;

    @Resource(name = "shoppingCartFilter")
    private Filter shoppingCartFilter;

    @Resource(name = "requestLocaleResolverFilter")
    private Filter requestLocaleResolverFilter;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Override
    protected ApplicationContext createContext() {
        return webApplicationContext;
    }

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(shopResolverFilter)
                .addFilter(shoppingCartFilter)
                .addFilter(requestLocaleResolverFilter)
                .build();

    }

    @Test
    public void testCategoryJson() throws Exception {

        mockMvc.perform(get("/category/menu").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")));

        mockMvc.perform(get("/category/menu/106").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")));

        mockMvc.perform(get("/category/view/106").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")));

    }

    @Test
    public void testCategoryXML() throws Exception {

        mockMvc.perform(get("/category/menu").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")));

        mockMvc.perform(get("/category/menu/106").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")));

        mockMvc.perform(get("/category/view/106").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")));

    }



    @Test
    public void testProductJson() throws Exception {

        reindex();

        mockMvc.perform(get("/product/9998").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

        mockMvc.perform(get("/sku/9998").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

        mockMvc.perform(get("/product/9999").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

        mockMvc.perform(get("/sku/9999").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

        mockMvc.perform(get("/product/9998/associations/accessories").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

    }


    @Test
    public void testProductXML() throws Exception {

        reindex();

        mockMvc.perform(get("/product/9998").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

        mockMvc.perform(get("/sku/9998").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

        mockMvc.perform(get("/product/9999").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

        mockMvc.perform(get("/sku/9999").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

        mockMvc.perform(get("/product/9998/associations/accessories").contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")));

    }

    @Test
    public void testSearchJson() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        final byte[] body = mapper.writeValueAsBytes(search);


        mockMvc.perform(put("/search").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

    }

    @Test
    public void testSearchXML() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        final byte[] body = mapper.writeValueAsBytes(search);


        mockMvc.perform(put("/search").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_XML).content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")));

    }

    private void reindex() {
        PlatformTransactionManager transactionManager =   ctx().getBean("transactionManager", PlatformTransactionManager.class);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);


        tx.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO)).fullTextSearchReindex(false);

            }
        });
    }


}
