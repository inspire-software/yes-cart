package org.yes.cart.service.theme.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.service.domain.ShopService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 30/09/2015
 * Time: 08:38
 */
public class ThemeServiceImplTest {

    private final Mockery mockery = new JUnit4Mockery();


    @Test
    public void testGetCurrentThemeChainWhenShopIdNull() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(null, "www.default.com");
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetCurrentThemeChainWhenShopNotFound() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(null));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlNullDefaultThemeCfgNull() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(null));
            allowing(shop).getFspointer();
            will(returnValue(null));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlEmptyDefaultThemeCfgNull() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(Collections.emptySet()));
            allowing(shop).getFspointer();
            will(returnValue(null));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlEmptyDefaultThemeCfgBlank() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(Collections.emptySet()));
            allowing(shop).getFspointer();
            will(returnValue(" "));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlEmptyDefaultThemeCfgSingle() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(Collections.emptySet()));
            allowing(shop).getFspointer();
            will(returnValue("theme1"));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(2, chain0.size());
        assertEquals("theme1", chain0.get(0));
        assertEquals("default", chain0.get(1));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlEmptyDefaultThemeCfgMulti() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(Collections.emptySet()));
            allowing(shop).getFspointer();
            will(returnValue("theme1;theme2"));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(3, chain0.size());
        assertEquals("theme1", chain0.get(0));
        assertEquals("theme2", chain0.get(1));
        assertEquals("default", chain0.get(2));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetCurrentThemeChainWhenShopUrlNull() throws Exception {

        final ShopService shopService = mockery.mock(ShopService.class, "shopService");

        final Shop shop = mockery.mock(Shop.class, "shop");
        final ShopUrl url1 = mockery.mock(ShopUrl.class, "url1");
        final ShopUrl url2 = mockery.mock(ShopUrl.class, "url2");
        final ShopUrl url3 = mockery.mock(ShopUrl.class, "url3");

        mockery.checking(new Expectations() {{
            allowing(shopService).getById(1L);
            will(returnValue(shop));
            allowing(shop).getShopUrl();
            will(returnValue(new HashSet<ShopUrl>(Arrays.asList(url1, url2, url3))));
            allowing(url1).getUrl();
            will(returnValue("www.default.com"));
            allowing(url1).getThemeChain();
            will(returnValue(null));
            allowing(url2).getUrl();
            will(returnValue("www.single.com"));
            allowing(url2).getThemeChain();
            will(returnValue("theme1"));
            allowing(url3).getUrl();
            will(returnValue("www.multi.com"));
            allowing(url3).getThemeChain();
            will(returnValue("theme1;theme3"));
            allowing(shop).getFspointer();
            will(returnValue("theme1;theme2"));
        }});

        final ThemeServiceImpl themeService = new ThemeServiceImpl(shopService);

        final List<String> chain0 = themeService.getThemeChainByShopId(1L, "www.default.com");
        assertNotNull(chain0);
        assertEquals(3, chain0.size());
        assertEquals("theme1", chain0.get(0));
        assertEquals("theme2", chain0.get(1));
        assertEquals("default", chain0.get(2));

        final List<String> chain1 = themeService.getThemeChainByShopId(1L, "www.single.com");
        assertNotNull(chain1);
        assertEquals(2, chain1.size());
        assertEquals("theme1", chain1.get(0));
        assertEquals("default", chain1.get(1));

        final List<String> chain3 = themeService.getThemeChainByShopId(1L, "www.multi.com");
        assertNotNull(chain3);
        assertEquals(3, chain3.size());
        assertEquals("theme1", chain3.get(0));
        assertEquals("theme3", chain3.get(1));
        assertEquals("default", chain3.get(2));

        final List<String> chain4 = themeService.getThemeChainByShopId(1L, "www.other.com");
        assertNotNull(chain4);
        assertEquals(3, chain4.size());
        assertEquals("theme1", chain4.get(0));
        assertEquals("theme2", chain4.get(1));
        assertEquals("default", chain4.get(2));

        final List<String> chain5 = themeService.getThemeChainByShopId(1L, null);
        assertNotNull(chain5);
        assertEquals(3, chain5.size());
        assertEquals("theme1", chain5.get(0));
        assertEquals("theme2", chain5.get(1));
        assertEquals("default", chain5.get(2));

        mockery.assertIsSatisfied();

    }


}