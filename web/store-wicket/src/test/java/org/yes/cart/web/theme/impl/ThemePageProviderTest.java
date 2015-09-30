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
import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
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

        final ApplicationContext applicationContextDefault = mockery.mock(ApplicationContext.class, "applicationContextDefault");
        final ApplicationContext applicationContextTheme1 = mockery.mock(ApplicationContext.class, "applicationContextTheme1");
        final ApplicationContext applicationContextTheme2 = mockery.mock(ApplicationContext.class, "applicationContextTheme2");

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");
        final SystemService systemService = mockery.mock(SystemService.class, "systemService");

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
            allowing(applicationContextDefault).getBean("shopService", ShopService.class);
            will(returnValue(shopService));
            allowing(applicationContextDefault).getBean("systemService", SystemService.class);
            will(returnValue(systemService));
            allowing(applicationContextDefault).getBean("themeService", ThemeService.class);
            will(returnValue(themeServiceDefault));
            allowing(shopDefault).getShopId();
            will(returnValue(1L));
            allowing(themeServiceDefault).getThemeChainByShopId(1L, null);
            will(returnValue(Arrays.asList("default")));

            allowing(applicationContextTheme1).getBean("shopService", ShopService.class);
            will(returnValue(shopService));
            allowing(applicationContextTheme1).getBean("systemService", SystemService.class);
            will(returnValue(systemService));
            allowing(applicationContextTheme1).getBean("themeService", ThemeService.class);
            will(returnValue(themeServiceTheme1));
            allowing(shopTheme1).getShopId();
            will(returnValue(2L));
            allowing(themeServiceTheme1).getThemeChainByShopId(2L, null);
            will(returnValue(Arrays.asList("theme1","default")));

            allowing(applicationContextTheme2).getBean("shopService", ShopService.class);
            will(returnValue(shopService));
            allowing(applicationContextTheme2).getBean("systemService", SystemService.class);
            will(returnValue(systemService));
            allowing(applicationContextTheme2).getBean("themeService", ThemeService.class);
            will(returnValue(themeServiceTheme2));
            allowing(shopTheme2).getShopId();
            will(returnValue(3L));
            allowing(themeServiceTheme2).getThemeChainByShopId(3L, null);
            will(returnValue(Arrays.asList("theme2","theme1","default")));
        }});

        new ApplicationDirector();

        ApplicationDirector.getInstance().setApplicationContext(applicationContextDefault);
        ApplicationDirector.setCurrentShop(shopDefault);
        assertSame(IRequestablePage.class, page.get());
        ApplicationDirector.clear();

        ApplicationDirector.getInstance().setApplicationContext(applicationContextTheme1);
        ApplicationDirector.setCurrentShop(shopTheme1);
        assertSame(IRequestablePageTheme1.class, page.get());
        ApplicationDirector.clear();

        ApplicationDirector.getInstance().setApplicationContext(applicationContextTheme2);
        ApplicationDirector.setCurrentShop(shopTheme2);
        assertSame(IRequestablePageTheme2.class, page.get());
        ApplicationDirector.clear();

    }

}
