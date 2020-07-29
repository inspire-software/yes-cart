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

package org.yes.cart.web.service.apisupport.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 27/06/2020
 * Time: 22:55
 */
public class ApiUrlTemplateFunctionProviderImplTest {

    final Mockery context = new JUnit4Mockery();

    @Test
    public void doActionNoParamCtx() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("/ctx");

        assertEquals("https://www.myshop.com/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionNoParamRoot() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("");

        assertEquals("https://www.myshop.com", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionOneParamCtx() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("/ctx", "one");

        assertEquals("https://www.myshop.com/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionOneParamRoot() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("", "one");

        assertEquals("https://www.myshop.com", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionTwoParamsCtx() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("/ctx", "one", "two");

        assertEquals("https://www.myshop.com/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1/two/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/ctx/one/1/two/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionTwoParamsRoot() throws Exception {

        final Shop shop = this.context.mock(Shop.class);

        ApplicationDirector.setCurrentShop(shop);

        this.context.checking(new Expectations() {{
            allowing(shop).getDefaultShopPreferredUrl(); will(returnValue("https://www.myshop.com"));
        }});

        final ApiUrlTemplateFunctionProviderImpl func = new ApiUrlTemplateFunctionProviderImpl("", "one", "two");

        assertEquals("https://www.myshop.com", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1/two/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("https://www.myshop.com/one/1/two/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

}