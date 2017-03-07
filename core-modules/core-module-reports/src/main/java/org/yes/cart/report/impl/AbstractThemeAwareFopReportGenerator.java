package org.yes.cart.report.impl;

import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/10/2015
 * Time: 12:27
 */
public abstract class AbstractThemeAwareFopReportGenerator extends AbstractFopReportGenerator implements ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractThemeAwareFopReportGenerator.class);

    private final ThemeService themeService;
    private final ShopService shopService;
    private final ContentService contentService;

    private ServletContext servletContext;

    protected AbstractThemeAwareFopReportGenerator(final ThemeService themeService,
                                                   final ShopService shopService,
                                                   final ContentService contentService) {
        this.themeService = themeService;
        this.shopService = shopService;
        this.contentService = contentService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream getFopUserConfigInputStream(final ReportDescriptor descriptor,
                                                      final Map<String, Object> parameters,
                                                      final Object data,
                                                      final String lang) {

        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        try {
            return new FopThemeResourceResolver(
                    shop, lang, "fop-userconfig.xml", "fop-userconfig.xml",
                    themeService, contentService, servletContext
            ).getResource((URI) null);
        } catch (Exception exp) {
            LOG.error("Unable to load report template URI fop-userconfig.xml", exp);
            return null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Source getXsltFile(final ReportDescriptor descriptor,
                               final Map<String, Object> parameters,
                               final Object data,
                               final String lang) {

        final Shop shop = resolveShop(descriptor, parameters, data, lang);

        try {
            return new StreamSource(new FopThemeResourceResolver(
                    shop, lang, descriptor.getReportId(), descriptor.getLangXslfo(lang),
                    themeService, contentService, servletContext
            ).getResource((URI) null));
        } catch (Exception exp) {
            LOG.error("Unable to load report template URI " + descriptor.getReportId(), exp);
            return null;
        }

    }

    /**
     * Resolve shop instance from parameters.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return shop instance for given parameters
     */
    protected final Shop resolveShop(final ReportDescriptor descriptor,
                                     final Map<String, Object> parameters,
                                     final Object data,
                                     final String lang) {

        Shop shop = null;
        if (parameters.get("shop") instanceof Shop) {
            shop = (Shop) parameters.get("shop");
        } else if (parameters.get("shopId") instanceof Long) {
            shop = shopService.getById((Long) parameters.get("shopId"));
        } else if (parameters.get("shopCode") instanceof String) {
            shop = shopService.getShopByCode((String) parameters.get("shopCode"));
        }

        return shop;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TempResourceResolver getTempResourceResolver(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        return new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceResolver getResourceResolver(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        return new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
