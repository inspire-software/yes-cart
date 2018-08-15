package org.yes.cart.report.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2015
 * Time: 15:38
 */
public class FopThemeResourceResolver implements TempResourceResolver, ResourceResolver {

    private static final Logger LOG = LoggerFactory.getLogger(FopThemeResourceResolver.class);

    private final Shop shop;
    private final String language;
    private final ThemeService themeService;
    private final ContentService contentService;
    private final ServletContext servletContext;
    private final SystemService systemService;
    private final ImageService imageService;

    private final String contentSuffix;
    private final String fileSuffix;

    public FopThemeResourceResolver(final Shop shop,
                                    final String language,
                                    final ThemeService themeService,
                                    final ContentService contentService,
                                    final ServletContext servletContext,
                                    final SystemService systemService,
                                    final ImageService imageService) {
        this(shop, language, null, null, themeService, contentService, servletContext, systemService, imageService);
    }

    public FopThemeResourceResolver(final Shop shop,
                                    final String language,
                                    final String contentSuffix,
                                    final String fileSuffix,
                                    final ThemeService themeService,
                                    final ContentService contentService,
                                    final ServletContext servletContext,
                                    final SystemService systemService,
                                    final ImageService imageService) {
        this.shop = shop;
        this.language = language;
        this.contentSuffix = contentSuffix;
        this.fileSuffix = fileSuffix;
        this.themeService = themeService;
        this.contentService = contentService;
        this.servletContext = servletContext;
        this.systemService = systemService;
        this.imageService = imageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource(final URI uri) throws IOException {

        final Long shopId = shop != null ? shop.getShopId() : null;

        if (shop != null) {

            final String curi = shop.getCode().concat("_report_").concat(contentSuffix != null ? contentSuffix : uri.toString());
            final String content = contentService.getContentBody(curi, language);
            if (StringUtils.isNotBlank(content)) {
                LOG.debug("Using shop specific report template file {}", curi);
                return new Resource(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            }

            final Long contentObjId = contentService.findContentIdBySeoUri(curi);
            if (contentObjId != null && contentObjId > 0L) {
                final Category contentObj = contentService.getById(contentObjId);
                if (contentObj != null) {
                    final byte[] localeSpecificImage = getImageAsBytes(contentObj, AttributeNamesKeys.Category.CATEGORY_IMAGE + "_" + language, curi);
                    if (localeSpecificImage != null && localeSpecificImage.length > 0) {
                        return new Resource(new ByteArrayInputStream(localeSpecificImage));
                    }

                    final byte[] defaultImage = getImageAsBytes(contentObj, AttributeNamesKeys.Category.CATEGORY_IMAGE, curi);
                    if (defaultImage != null && defaultImage.length > 0) {
                        return new Resource(new ByteArrayInputStream(defaultImage));
                    }
                }
            }

        }

        final List<String> chain = themeService.getReportsTemplateChainByShopId(shopId);
        for (final String path : chain) {
            final String configFile = path + (fileSuffix != null ? fileSuffix : uri.toString());
            try {
                final InputStream stream = servletContext.getResourceAsStream(configFile);
                if (stream != null) {
                    LOG.debug("Using theme specific report template file {}", configFile);
                    return new Resource(stream);
                }
            } catch (Exception mue) {
                LOG.error("Unable to load report template file " + configFile, mue);
            }
        }

        LOG.debug("No report template for {}", uri);
        return null;

    }

    private byte[] getImageAsBytes(final Category content, final String imageAttribute, final String contentFullUri) throws IOException {
        final String imageValue = content.getAttributeValueByCode(imageAttribute);
        if (StringUtils.isNotBlank(imageValue)) {

            final String path = systemService.getImageRepositoryDirectory();

            if (imageService.isImageInRepository(imageValue, contentFullUri, Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, path)) {
                return imageService.imageToByteArray(imageValue, contentFullUri, Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, path);
            }

            if (imageService.isImageInRepository(imageValue, content.getGuid(), Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, path)) {
                return imageService.imageToByteArray(imageValue, content.getGuid(), Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, path);
            }

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream(final URI uri) throws IOException {
        throw new IOException("output stream is not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource(final String id) throws IOException {
        throw new IOException("tmp:// resources are not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream(final String id) throws IOException {
        throw new IOException("tmp:// resources are not supported");
    }
}
