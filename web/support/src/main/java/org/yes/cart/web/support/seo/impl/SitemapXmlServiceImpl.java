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

package org.yes.cart.web.support.seo.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.DomainApiUtils;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.utils.RuntimeConstants;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:46 PM
 */
public class SitemapXmlServiceImpl implements SitemapXmlService {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapXmlServiceImpl.class);

    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ContentService contentService;
    private final ProductService productService;
    private final LanguageService languageService;
    private final String contextPath;

    public SitemapXmlServiceImpl(final ShopService shopService,
                                 final CategoryService categoryService,
                                 final ContentService contentService,
                                 final ProductService productService,
                                 final LanguageService languageService,
                                 final RuntimeConstants runtimeConstants) {
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.productService = productService;
        this.languageService = languageService;
        this.contextPath = runtimeConstants.getConstant(RuntimeConstants.WEBAPP_SF_CONTEXT_PATH);
    }

    /** {@inheritDoc} */
    @Override
    public InputStream generateSitemapXmlStream(final String shopCode) {

        try {

            final File sitemap = File.createTempFile("sitemap-" + shopCode, ".tmp");

            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(sitemap), StandardCharsets.UTF_8);

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
            writer.write("<!-- YecCart sitemap generator. ");
            writer.write(DateUtils.formatSDT());
            writer.write(" -->\n");

            final Shop shop = shopService.getShopByCode(shopCode);
            if (shop != null) {

                List<String> supportedLanguages = languageService.getSupportedLanguages(shopCode);
                if (supportedLanguages == null) {
                    supportedLanguages = Arrays.asList("en", "ru");
                }

                appendShopUrls(writer, shop, supportedLanguages);

            }

            writer.write("</urlset>");

            writer.close();

            return new DeleteOnCloseFileInputStream(sitemap);


        } catch (Exception exp) {

            LOG.error("Error generating sitemap for " + shopCode, exp);

        }

        return new ByteArrayInputStream(new byte[0]);
    }

    /** {@inheritDoc} */
    @Override
    public String generateSitemapXml(final String shopCode) {

        try {

            return IOUtils.toString(generateSitemapXmlStream(shopCode), StandardCharsets.UTF_8.name());

        } catch (IOException exp) {

            LOG.error("Error generating sitemap for " + shopCode, exp);

        }

        return "";

    }



    private void appendShopUrls(final OutputStreamWriter writer, final Shop shop, final List<String> languages) throws IOException {

        if (shop.isDisabled()) {
            return;
        }

        final LocalDateTime now = TimeContext.getLocalDateTime();
        final String urlBase = getShopBaseUrl(shop);

        appendHomeLoc(writer, urlBase, languages);

        final Set<Long> accessible = appendCategories(writer, shop, urlBase, languages, now);

        appendContent(writer, shop, urlBase, languages, now);

        appendProducts(writer, shop, accessible, urlBase, languages, now);

    }


    private void appendHomeLoc(final OutputStreamWriter writer, final String urlBase, final List<String> languages) throws IOException {

        writer.write("<!-- Main URL -->\n");

        appendLoc(writer, urlBase, languages);

        writer.write("<!-- End of Main URL -->\n");

    }


    private Set<Long> appendCategories(final OutputStreamWriter writer, final Shop shop, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        final Set<Long> accessible = new HashSet<>();

        writer.write("<!-- Categories -->\n");

        final Set<Category> categories = shopService.getTopLevelCategories(shop.getShopId());
        for (final Category category : categories) {

            appendCategory(writer, category, accessible, urlBase, languages, now);

        }

        writer.write("<!-- End of Categories -->\n");

        return accessible;

    }


    private void appendCategory(final OutputStreamWriter writer, final Category category, final Set<Long> accessible, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        if (DomainApiUtils.isObjectAvailableNow(!category.isDisabled(), category.getAvailablefrom(), category.getAvailableto(), now)) {

            appendCategoryLoc(writer, category, languages, urlBase);

            accessible.add(category.getCategoryId());

            for (final Category child : categoryService.getChildCategories(category.getCategoryId())) {

                appendCategory(writer, child, accessible, urlBase, languages, now);

            }

        }

    }

    private void appendCategoryLoc(final OutputStreamWriter writer, final Category category, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(category.getSeo(), category.getCategoryId(), urlBase, WebParametersKeys.CATEGORY_ID), languages);
    }


    private void appendContent(final OutputStreamWriter writer, final Shop shop, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        writer.write("<!-- Content -->\n");

        final Content root = contentService.getRootContent(shop.getShopId());
        if (root != null) {

            appendContent(writer, root, false, urlBase, languages, now);

        }

        writer.write("<!-- End of Content -->\n");

    }


    private void appendContent(final OutputStreamWriter writer, final Content content, final boolean childInclude, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        if (DomainApiUtils.isObjectAvailableNow(!content.isDisabled(), content.getAvailablefrom(), content.getAvailableto(), now)) {

            final boolean include = CentralViewLabel.INCLUDE.equals(content.getUitemplate()) ||
                    childInclude && StringUtils.isBlank(content.getUitemplate());

            if (!include) {

                appendContentLoc(writer, content, languages, urlBase);

            }

            for (final Content contentItem : contentService.getChildContent(content.getContentId())) {

                appendContent(writer, contentItem, include, urlBase, languages, now);

            }

        }

    }


    private void appendContentLoc(final OutputStreamWriter writer, final Content content, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(content.getSeo(), content.getContentId(), urlBase, WebParametersKeys.CONTENT_ID), languages);
    }


    private void appendProducts(final OutputStreamWriter writer, final Shop shop, final Set<Long> accessible, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        writer.write("<!-- Products -->\n");

        productService.findAllIterator(product -> {

            if (DomainApiUtils.isObjectAvailableNow(!product.isDisabled(), product.getAvailablefrom(), product.getAvailableto(), now)) {

                try {
                    for (final ProductCategory productCategory : product.getProductCategory()) {
                        if (accessible.contains(productCategory.getCategory().getCategoryId())) {

                            appendProductLoc(writer, product, languages, urlBase);

                            if (product.isMultiSkuProduct()) {

                                for (final ProductSku sku : product.getSku()) {

                                    appendSkuLoc(writer, sku, languages, urlBase);

                                }

                            }

                            break;
                        }

                    }
                } catch (IOException ioe) {

                    LOG.error("Error generating sitemap for " + shop.getCode(), ioe);
                    return false; // no point to continue if we have IO failure

                }

            }

            return true; // read fully

        });

        writer.write("<!-- End of Products -->\n");
    }

    private void appendProductLoc(final OutputStreamWriter writer, final Product product, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(product.getSeo(), product.getProductId(), urlBase, WebParametersKeys.PRODUCT_ID), languages);
    }

    private void appendSkuLoc(final OutputStreamWriter writer, final ProductSku product, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(product.getSeo(), product.getSkuId(), urlBase, WebParametersKeys.SKU_ID), languages);
    }


    private void appendLoc(final OutputStreamWriter writer, final String loc, final List<String> languages) throws IOException {
        for (final String urlLang : languages) {
            writer.write("<url><loc><![CDATA[");
            writer.write(alternativeUrl(loc, urlLang));
            writer.write("]]></loc>");
            for (final String language : languages) {
                writer.write("<xhtml:link rel=\"alternate\" hreflang=\"");
                writer.write(language);
                writer.write("\" href=\"");
                writer.write(alternativeUrl(loc, language));
                writer.write("\" />");
            }
            writer.write("<changefreq>daily</changefreq></url>\n");
        }
    }


    private String seoUrl(final Seo seo, final long pk, final String urlBase, final String namespace) {
        if (seo != null && seo.getUri() != null && seo.getUri().length() > 0) {
            return urlBase + namespace + "/" + seo.getUri();
        }
        return urlBase + namespace + "/" + pk;
    }


    private String alternativeUrl(final String original, final String language) {
        if (original.endsWith("/")) {
            return original + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;
        }
        return original + "/" + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;
    }

    private String getShopBaseUrl(final Shop shop) {
        final String urlBase = shop.getDefaultShopUrl();
        if (urlBase.endsWith("/")) {
            return urlBase + contextPath.substring(1) + "/";
        }
        return urlBase + this.contextPath + "/";
    }

    private static class DeleteOnCloseFileInputStream extends FileInputStream {

        private File file;

        public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException{
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                if(file != null) {
                    file.delete();
                    file = null;
                }
            }
        }
    }

}
