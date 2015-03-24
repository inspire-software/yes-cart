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

package org.yes.cart.web.theme.impl;

import org.apache.wicket.request.component.IRequestablePage;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.HashMap;

import static org.junit.Assert.assertSame;

/**
 * User: denispavlov
 * Date: 04/08/2014
 * Time: 16:11
 */
public class ThemePageProviderTest {

    public interface IRequestablePageTheme1 extends IRequestablePage {}
    public interface IRequestablePageTheme2 extends IRequestablePage {}

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGet() throws Exception {


        final ThemePageProvider onlyDefault = new ThemePageProvider(new HashMap() {{
            put("default", IRequestablePage.class);
            put("theme1", IRequestablePageTheme1.class);
            put("theme2", IRequestablePageTheme2.class);
        }});


        final Shop shopDefault = mockery.mock(Shop.class, "default");
        final Shop shopTheme1 = mockery.mock(Shop.class, "theme1,default");
        final Shop shopTheme2 = mockery.mock(Shop.class, "theme2,theme1,default");


        mockery.checking(new Expectations() {{
            allowing(shopDefault).getFspointer(); will(returnValue("default"));
            allowing(shopTheme1).getFspointer(); will(returnValue("theme1;default"));
            allowing(shopTheme2).getFspointer(); will(returnValue("theme2;theme1;default"));
        }});

        ApplicationDirector.setCurrentShop(shopDefault);
        assertSame(IRequestablePage.class, onlyDefault.get());
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentShop(shopTheme1);
        assertSame(IRequestablePageTheme1.class, onlyDefault.get());
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentShop(shopTheme2);
        assertSame(IRequestablePageTheme2.class, onlyDefault.get());
        ApplicationDirector.clear();

    }
}
