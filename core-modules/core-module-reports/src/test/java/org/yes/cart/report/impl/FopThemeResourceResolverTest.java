package org.yes.cart.report.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 26/10/2015
 * Time: 17:04
 */
public class FopThemeResourceResolverTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testGetResourceFromContent() throws Exception {

        final Shop shop = context.mock(Shop.class, "shop");
        final String lang = "en";

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            one(contentService).getContentBody("SHOP10_report_fop-config.xml", "en"); will(returnValue("xml contents"));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetResourcePredefinedFromContent() throws Exception {

        final Shop shop = context.mock(Shop.class, "shop");
        final String lang = "en";

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            one(contentService).getContentBody("SHOP10_report_fop-config.content.xml", "en"); will(returnValue("xml contents"));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.content.xml", "fop-config.file.xml", themeService, contentService, servletContext);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetResourceFromTheme() throws Exception {

        final Shop shop = context.mock(Shop.class, "shop");
        final String lang = "en";

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            one(contentService).getContentBody("SHOP10_report_fop-config.xml", "en"); will(returnValue(null));
            one(themeService).getReportsTemplateChainByShopId(123L); will(returnValue(Arrays.asList("theme1/reports/", "default/reports/")));
            one(servletContext).getResourceAsStream("theme1/reports/fop-config.xml"); will(returnValue(null));
            one(servletContext).getResourceAsStream("default/reports/fop-config.xml"); will(returnValue(new ByteArrayInputStream(new byte[0])));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetResourcePredefinedFromTheme() throws Exception {

        final Shop shop = context.mock(Shop.class, "shop");
        final String lang = "en";

        final ThemeService themeService = context.mock(ThemeService.class, "themeService");
        final ContentService contentService = context.mock(ContentService.class, "contentService");
        final ServletContext servletContext = context.mock(ServletContext.class, "servletContext");

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            one(contentService).getContentBody("SHOP10_report_fop-config.content.xml", "en"); will(returnValue(null));
            one(themeService).getReportsTemplateChainByShopId(123L); will(returnValue(Arrays.asList("theme1/reports/", "default/reports/")));
            one(servletContext).getResourceAsStream("theme1/reports/fop-config.file.xml"); will(returnValue(null));
            one(servletContext).getResourceAsStream("default/reports/fop-config.file.xml"); will(returnValue(new ByteArrayInputStream(new byte[0])));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.content.xml", "fop-config.file.xml", themeService, contentService, servletContext);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }

}