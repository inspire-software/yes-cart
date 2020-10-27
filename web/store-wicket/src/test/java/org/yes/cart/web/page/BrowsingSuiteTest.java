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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.CustomMockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: inspiresoftware
 * Date: 19/10/2020
 * Time: 20:59
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/main/webapp")
public class BrowsingSuiteTest extends AbstractSuiteTest {

    @Test
    public void testCategory() throws Exception {

        final MvcResult start =
        mockMvc.perform(get("/")
                    .accept(MediaType.TEXT_HTML)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" href=\"./category/113\" class=\"\">")))
                .andExpect(content().string(StringContains.containsString("<span>Fun Gadgets</span>")))
                .andReturn();

        final String xCwToken = start.getResponse().getHeader(X_CW_TOKEN);
        assertNotNull(xCwToken);

        mockMvc.perform(get("/category/106")
                    .accept(MediaType.TEXT_HTML)
                    .header(X_CW_TOKEN, xCwToken)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" href=\"./109\" class=\"\">")))
                .andExpect(content().string(StringContains.containsString("<span>Retro Gadgets</span>")));


    }

    @Test
    public void testContent() throws Exception {

        final MvcResult start =
        mockMvc.perform(get("/content/SHOIP1_menu_item_1")
                    .accept(MediaType.TEXT_HTML)
                    .locale(LOCALE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
                .andExpect(content().string(StringContains.containsString("<span>menu item 1.1</span>")))
                .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" href=\"./SHOIP1_menu_item_1_1\" class=\"\">")))
                .andExpect(content().string(StringContains.containsString("<div>Menu Item Content</div>")))
                .andReturn();

        final String xCwToken = start.getResponse().getHeader(X_CW_TOKEN);
        assertNotNull(xCwToken);


    }

    @Test
    public void testProduct() throws Exception {

        reindex();

        final MvcResult start =
        mockMvc.perform(get("/category/104")
                .accept(MediaType.TEXT_HTML)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
            .andExpect(content().string(StringContains.containsString("<span>Robotics</span>")))
            .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" href=\"./104\" class=\"active-category\">")))
            .andExpect(content().string(StringContains.containsString("<ul class=\"pagination pagination-sm\">")))
            .andExpect(content().string(StringContains.containsString("<div class=\"thumbnail\" itemscope itemtype=\"http://schema.org/Product\">")))
            .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" itemprop=\"url\" href=\"./104/fc/WAREHOUSE_1/product/15126\">")))
            .andExpect(content().string(StringContains.containsString("<span>pre order product</span>")))
                .andExpect(content().string(StringContains.containsString("js-buy\" rel=\"nofollow\" href=\"./104/addToCartCmd/CC_TEST6/supplier/WAREHOUSE_1\"")))
            .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" href=\"./104/fc/WAREHOUSE_2/product/bender-bending-rodriguez\">")))
            .andExpect(content().string(StringContains.containsString("<span>Bender Bending Rodriguez</span>")))
            .andReturn();

        final String xCwToken = start.getResponse().getHeader(X_CW_TOKEN);
        assertNotNull(xCwToken);

        mockMvc.perform(get("/query/Sobot")
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
            .andExpect(content().string(StringContains.containsString("<ul class=\"pagination pagination-sm\">")))
            .andExpect(content().string(StringContains.containsString("<div class=\"thumbnail\" itemscope itemtype=\"http://schema.org/Product\">")))
            .andExpect(content().string(StringContains.containsString("<a rel=\"bookmark\" itemprop=\"url\" href=\"./Sobot/fc/WAREHOUSE_2/product/i-sobot\">")))
            .andExpect(content().string(StringContains.containsString("<span itemprop=\"name\">I-Sobot</span>")))
            .andExpect(content().string(StringContains.containsString("js-buy\" rel=\"nofollow\" href=\"./Sobot/addToCartCmd/SOBOT-ORIG/supplier/WAREHOUSE_2\"")));


        mockMvc.perform(get("/category/104/fc/WAREHOUSE_2/product/bender-bending-rodriguez")
                .accept(MediaType.TEXT_HTML)
                .header(X_CW_TOKEN, xCwToken)
                .locale(LOCALE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(StringContains.containsString("<span>Login / Register</span>")))
            .andExpect(content().string(StringContains.containsString("product-detail-holder\" itemscope itemtype=\"http://schema.org/Product\">")))
            .andExpect(content().string(StringContains.containsString("product-detail-img-holder js-product-image-view")))
            .andExpect(content().string(StringContains.containsString("<span class=\"sku-code\" itemprop=\"productID\">BENDER-ua</span>")))
            .andExpect(content().string(StringContains.containsString("js-buy\" rel=\"nofollow\" href=\"./bender-bending-rodriguez/addToCartCmd/BENDER-ua/supplier/WAREHOUSE_2\"")))
            .andExpect(content().string(StringContains.containsString("<span class=\"regular-price-whole\">99</span><span class=\"regular-price-dot\">.</span><span class=\"regular-price-decimal\">99</span>")))
            .andExpect(content().string(StringContains.containsString("<div class=\"product-detail-tabs\">")))
            .andExpect(content().string(StringContains.containsString("<div class=\"attr-head\">DVD Players view group</div>")))
            .andExpect(content().string(StringContains.containsString("<span>Weight</span>")))
            .andExpect(content().string(StringContains.containsString("<span class=\"pull-right\">1.15</span>")));


    }
}
