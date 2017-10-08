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

package org.yes.cart.web.theme.impl;

import org.apache.wicket.request.component.IRequestablePage;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertSame;

/**
 * User: denispavlov
 * Date: 04/08/2014
 * Time: 16:11
 */
public class ThemePageProviderTest {

    public interface IRequestablePageTheme1 extends IRequestablePage {
    }

    public interface IRequestablePageTheme2 extends IRequestablePage {
    }

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGet() throws Exception {

        final ThemeService themeServiceDefault = mockery.mock(ThemeService.class, "themeServiceDefault");
        final ThemeService themeServiceTheme1 = mockery.mock(ThemeService.class, "themeServiceTheme1");
        final ThemeService themeServiceTheme2 = mockery.mock(ThemeService.class, "themeServiceTheme2");

        final ThemePageProvider page = new ThemePageProvider(new HashMap() {{
            put("default", IRequestablePage.class);
            put("theme1", IRequestablePageTheme1.class);
            put("theme2", IRequestablePageTheme2.class);
        }});


        final Shop shopDefault = mockery.mock(Shop.class, "default");
        final Shop shopTheme1 = mockery.mock(Shop.class, "theme1,default");
        final Shop shopTheme2 = mockery.mock(Shop.class, "theme2,theme1,default");


        mockery.checking(new Expectations() {{
            allowing(shopDefault).getShopId();
            will(returnValue(1L));
            allowing(themeServiceDefault).getThemeChainByShopId(1L, null);
            will(returnValue(Arrays.asList("default")));

            allowing(shopTheme1).getShopId();
            will(returnValue(2L));
            allowing(themeServiceTheme1).getThemeChainByShopId(2L, null);
            will(returnValue(Arrays.asList("theme1","default")));

            allowing(shopTheme2).getShopId();
            will(returnValue(3L));
            allowing(themeServiceTheme2).getThemeChainByShopId(3L, null);
            will(returnValue(Arrays.asList("theme2","theme1","default")));
        }});

        new ApplicationDirector();

        ApplicationDirector.setCurrentThemeChain(Arrays.asList("default"));
        ApplicationDirector.setCurrentShop(shopDefault);
        assertSame(IRequestablePage.class, page.get());
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentThemeChain(Arrays.asList("theme1","default"));
        ApplicationDirector.setCurrentShop(shopTheme1);
        assertSame(IRequestablePageTheme1.class, page.get());
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentThemeChain(Arrays.asList("theme2","theme1","default"));
        ApplicationDirector.setCurrentShop(shopTheme2);
        assertSame(IRequestablePageTheme2.class, page.get());
        ApplicationDirector.clear();

    }

}
