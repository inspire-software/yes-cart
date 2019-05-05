package org.yes.cart.report.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 26/10/2015
 * Time: 17:04
 */
public class FopThemeResourceResolverTest {

    private final Mockery context = new JUnit4Mockery();

    private String lang;
    private Shop shop;
    private ThemeService themeService;
    private ContentService contentService;
    private ServletContext servletContext;
    private SystemService systemService;
    private ImageService imageService;
    private Content content;

    @Before
    public void setUp() {
        lang = "en";
        shop = context.mock(Shop.class, "shop");
        themeService = context.mock(ThemeService.class, "themeService");
        contentService = context.mock(ContentService.class, "contentService");
        servletContext = context.mock(ServletContext.class, "servletContext");
        systemService = context.mock(SystemService.class, "systemService");
        imageService = context.mock(ImageService.class, "imageService");
        content = context.mock(Content.class, "content");
    }

    @Test
    public void testGetResourceFromContent() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.xml", "en"); will(returnValue("xml contents"));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetResourceFromCategoryImageLocaleSpecific() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.image.png", "en"); will(returnValue(null));
            oneOf(contentService).findContentIdBySeoUri("SHOP10_report_fop-config.image.png"); will(returnValue(123L));
            oneOf(contentService).getById(123L); will(returnValue(content));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0_en"); will(returnValue("imageValue"));
            oneOf(systemService).getImageRepositoryDirectory(); will(returnValue("imageVaultPath"));
            oneOf(imageService).isImageInRepository("imageValue", "SHOP10_report_fop-config.image.png", "/imgvault/category/", "imageVaultPath"); will(returnValue(true));
            oneOf(imageService).imageToByteArray("imageValue", "SHOP10_report_fop-config.image.png", "/imgvault/category/", "imageVaultPath"); will(returnValue(new byte[1]));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.image.png", "fop-config.file.xml", themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();
    }

    @Test
    public void testGetResourceFromDefaultCategoryImage() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.image.png", "en"); will(returnValue(null));
            oneOf(contentService).findContentIdBySeoUri("SHOP10_report_fop-config.image.png"); will(returnValue(123L));
            oneOf(contentService).getById(123L); will(returnValue(content));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0"); will(returnValue("imageValue"));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0_en"); will(returnValue(null));
            oneOf(systemService).getImageRepositoryDirectory(); will(returnValue("imageVaultPath"));
            oneOf(imageService).isImageInRepository("imageValue", "SHOP10_report_fop-config.image.png", "/imgvault/category/", "imageVaultPath"); will(returnValue(true));
            oneOf(imageService).imageToByteArray("imageValue", "SHOP10_report_fop-config.image.png", "/imgvault/category/", "imageVaultPath"); will(returnValue(new byte[1]));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.image.png", "fop-config.file.xml", themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();
    }

    @Test
    public void testGetResourcePredefinedFromContent() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.content.xml", "en"); will(returnValue("xml contents"));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.content.xml", "fop-config.file.xml", themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetResourceFromTheme() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.xml", "en"); will(returnValue(null));
            oneOf(contentService).findContentIdBySeoUri("SHOP10_report_fop-config.xml"); will(returnValue(123L));
            oneOf(contentService).getById(123L); will(returnValue(content));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0"); will(returnValue(null));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0_en"); will(returnValue(null));
            oneOf(themeService).getReportsTemplateChainByShopId(123L); will(returnValue(Arrays.asList("theme1/reports/", "default/reports/")));
            oneOf(servletContext).getResourceAsStream("theme1/reports/fop-config.xml"); will(returnValue(null));
            oneOf(servletContext).getResourceAsStream("default/reports/fop-config.xml"); will(returnValue(new ByteArrayInputStream(new byte[0])));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }


    @Test
    public void testGetResourcePredefinedFromTheme() throws Exception {

        context.checking(new Expectations() {{
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(shop).getCode(); will(returnValue("SHOP10"));
            oneOf(contentService).getContentBody("SHOP10_report_fop-config.content.xml", "en"); will(returnValue(null));
            oneOf(contentService).findContentIdBySeoUri("SHOP10_report_fop-config.content.xml"); will(returnValue(123L));
            oneOf(contentService).getById(123L); will(returnValue(content));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0"); will(returnValue(null));
            oneOf(content).getAttributeValueByCode("CATEGORY_IMAGE0_en"); will(returnValue(null));
            oneOf(themeService).getReportsTemplateChainByShopId(123L); will(returnValue(Arrays.asList("theme1/reports/", "default/reports/")));
            oneOf(servletContext).getResourceAsStream("theme1/reports/fop-config.file.xml"); will(returnValue(null));
            oneOf(servletContext).getResourceAsStream("default/reports/fop-config.file.xml"); will(returnValue(new ByteArrayInputStream(new byte[0])));
        }});

        final FopThemeResourceResolver resolver =
                new FopThemeResourceResolver(shop, lang, "fop-config.content.xml", "fop-config.file.xml", themeService, contentService, servletContext, systemService, imageService);

        assertNotNull(resolver.getResource(new URI("fop-config.xml")));

        context.assertIsSatisfied();

    }

}