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

package org.yes.cart.web.support.seo.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.RuntimeConstants;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;
    private final LanguageService languageService;
    private final SystemService systemService;
    private final String contextPath;

    public SitemapXmlServiceImpl(final ShopService shopService,
                                 final CategoryService categoryService,
                                 final ContentService contentService,
                                 final ProductService productService,
                                 final WarehouseService warehouseService,
                                 final SkuWarehouseService skuWarehouseService,
                                 final LanguageService languageService,
                                 final RuntimeConstants runtimeConstants,
                                 final SystemService systemService) {
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.languageService = languageService;
        this.systemService = systemService;
        this.contextPath = runtimeConstants.getConstant(RuntimeConstants.WEBAPP_SF_CONTEXT_PATH);
    }

    protected String determineSitemapXmlFilename(final String shopCode, final String suffix, final boolean temp) {
        return "sitemap-" + shopCode + (StringUtils.isNotBlank(suffix) ? "-" + suffix : "") + (temp ? ".tmp" : ".xml");
    }

    private File generateSitemapXmlInDirectory(final String shopCode, final File directory, final String suffix, final SitemapXmlCallback callback) {

        try {

            final File tmp = new File(directory, determineSitemapXmlFilename(shopCode, suffix, true));
            final File target = new File(directory, determineSitemapXmlFilename(shopCode, suffix, false));

            if (!tmp.getParentFile().exists()) {
                // ensure we create all dirs necessary
                if (!tmp.getParentFile().mkdirs()) {
                    LOG.error(Markers.alert(), "Unable to create directory {} for sitemap.xml for shop {}", tmp.getParent(), shopCode);
                }
            }

            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8);

            startSitemapXml(writer);

            final Shop shop = shopService.getShopByCode(shopCode);
            if (shop != null) {

                List<String> supportedLanguages = languageService.getSupportedLanguages(shopCode);
                if (supportedLanguages == null) {
                    supportedLanguages = Collections.singletonList("en");
                }

                callback.appendShopUrls(writer, shop, supportedLanguages);

            }

            endSitemapXml(writer);

            writer.close();

            Files.move(Paths.get(tmp.toURI()), Paths.get(target.toURI()), StandardCopyOption.REPLACE_EXISTING);

            return target;

        } catch (Exception exp) {

            LOG.error("Error generating sitemap for " + shopCode, exp);

        }

        return null;

    }


    /** {@inheritDoc} */
    @Override
    public InputStream generateSitemapXmlStream(final String shopCode) {

        try {

            final File directory = determineSitemapsDirectory(shopCode);

            final File sitemap = generateSitemapXmlInDirectory(shopCode, directory, null, (writer, shop, supportedLanguages) -> appendShopUrls(writer, shop, supportedLanguages));

            if (sitemap != null && sitemap.exists()) {
                return new FileInputStream(sitemap);
            }


        } catch (Exception exp) {

            LOG.error("Error generating sitemap for " + shopCode, exp);

        }

        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public boolean generateSitemapXmlAndRetain(final String shopCode) {

        return generateSitemapXmlAndRetain(shopCode, false, 0);

    }

    @Override
    public boolean generateSitemapXmlAndRetain(final String shopCode, final boolean multipart, final int maxProducts) {

        try {

            final File directory = determineSitemapsDirectory(shopCode);

            if (multipart) {

                final File sitemapMain = generateSitemapXmlInDirectory(shopCode, directory, "main", (writer, shop, supportedLanguages) -> appendShopUrlsMain(writer, shop, supportedLanguages));

                if (sitemapMain == null || !sitemapMain.exists()) {

                    return false;

                }


                long[] from = { 0 };
                long[] to = { maxProducts };
                long[] urlsWritten = { 0 };
                int count = 1;

                while (true) {
                    final File sitemapProduct = generateSitemapXmlInDirectory(shopCode, directory, "products-" + count, (writer, shop, supportedLanguages) -> urlsWritten[0] = appendShopUrlsProduct(writer, shop, supportedLanguages, from[0], to[0]));

                    if (sitemapProduct == null || !sitemapProduct.exists()) {

                        return false;

                    }

                    if (urlsWritten[0] < maxProducts) {
                        break;
                    }

                    from[0] = to[0];
                    to[0] = to[0] + maxProducts;
                    count++;
                }

                return true;

            } else {

                final File sitemap = generateSitemapXmlInDirectory(shopCode, directory, null, (writer, shop, supportedLanguages) -> appendShopUrls(writer, shop, supportedLanguages));

                return sitemap != null && sitemap.exists();

            }

        } catch (Exception exp) {

            LOG.error("Error generating sitemap for " + shopCode, exp);

        }

        return false;

    }

    @Override
    public InputStream retrieveSitemapXmlStream(final String shopCode) {

        return retrieveSitemapXmlStream(shopCode, null);

    }

    @Override
    public InputStream retrieveSitemapXmlStream(final String shopCode, final String suffix) {

        try {

            final File directory = determineSitemapsDirectory(shopCode);

            final File sitemap = new File(directory, determineSitemapXmlFilename(shopCode, suffix, false));

            if (sitemap.exists()) {
                return new FileInputStream(sitemap);
            }


        } catch (Exception exp) {

            LOG.error("Error retrieving sitemap for " + shopCode, exp);

        }

        return new ByteArrayInputStream(new byte[0]);

    }

    protected File determineSitemapsDirectory(final String shopCode) {
        return new File(systemService.getSystemFileRepositoryDirectory(), "sitemaps");
    }

    protected void startSitemapXml(final OutputStreamWriter writer) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
        writer.write("<!-- YecCart sitemap generator. ");
        writer.write(DateUtils.formatSDT());
        writer.write(" -->\n");
    }

    protected void endSitemapXml(final OutputStreamWriter writer) throws IOException {
        writer.write("</urlset>");
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

        appendCategories(writer, shop, urlBase, languages, now);

        appendContent(writer, shop, urlBase, languages, now);

        appendProducts(writer, shop, urlBase, languages, now, 0, Long.MAX_VALUE);

    }


    private void appendShopUrlsMain(final OutputStreamWriter writer, final Shop shop, final List<String> languages) throws IOException {

        if (shop.isDisabled()) {
            return;
        }

        final LocalDateTime now = TimeContext.getLocalDateTime();
        final String urlBase = getShopBaseUrl(shop);

        appendHomeLoc(writer, urlBase, languages);

        appendCategories(writer, shop, urlBase, languages, now);

        appendContent(writer, shop, urlBase, languages, now);

    }

    private long appendShopUrlsProduct(final OutputStreamWriter writer, final Shop shop, final List<String> languages, final long from, final long to) throws IOException {

        if (shop.isDisabled()) {
            return 0;
        }

        final LocalDateTime now = TimeContext.getLocalDateTime();
        final String urlBase = getShopBaseUrl(shop);

        return appendProducts(writer, shop, urlBase, languages, now, from, to);

    }



    private void appendHomeLoc(final OutputStreamWriter writer, final String urlBase, final List<String> languages) throws IOException {

        writer.write("<!-- Main URL -->\n");

        appendLoc(writer, urlBase, languages);

        writer.write("<!-- End of Main URL -->\n");

    }


    private void appendCategories(final OutputStreamWriter writer, final Shop shop, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        writer.write("<!-- Categories -->\n");

        final Set<Category> categories = shopService.getTopLevelCategories(shop.getShopId());
        for (final Category category : categories) {

            appendCategory(writer, category, urlBase, languages, now);

        }

        writer.write("<!-- End of Categories -->\n");

    }


    private void appendCategory(final OutputStreamWriter writer, final Category category, final String urlBase, final List<String> languages, final LocalDateTime now) throws IOException {

        if (category.isAvailable(now)) {

            appendCategoryLoc(writer, category, languages, urlBase);

            for (final Category child : categoryService.getChildCategories(category.getCategoryId())) {

                appendCategory(writer, child, urlBase, languages, now);

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

        if (content.isAvailable(now)) {

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


    private long appendProducts(final OutputStreamWriter writer, final Shop shop, final String urlBase, final List<String> languages, final LocalDateTime now, long from, long to) throws IOException {

        writer.write("<!-- Products -->\n");

        final List<Warehouse> warehouses = warehouseService.getByShopId(shop.getShopId(), false);
        final Map<Long, String> idToCode = warehouses.stream().collect(Collectors.toMap(Warehouse::getWarehouseId, Warehouse::getCode));

        final long[] count = { 0 };

        skuWarehouseService.findByCriteriaIterator(" where e.warehouse in (?1)", new Object[]{ warehouses }, (inventory) -> {

            try {
                if (inventory.isAvailable(now)) {

                    count[0]++;

                    if (count[0] >= from) {

                        final String fcPath = WebParametersKeys.FULFILMENT_CENTRE_ID + "/" + idToCode.get(inventory.getWarehouse().getWarehouseId()) + "/";
                        final Product product = productService.getProductBySkuCode(inventory.getSkuCode());
                        if (product != null) {

                            if (product.isMultiSkuProduct()) {

                                for (final ProductSku sku : product.getSku()) {

                                    if (inventory.getSkuCode().equals(sku.getCode())) {
                                        appendSkuLoc(writer, sku, languages, urlBase + fcPath);
                                        break;
                                    }

                                }

                            } else {

                                appendProductLoc(writer, product, languages, urlBase + fcPath);

                            }
                        }
                    }
                }
            } catch (IOException ioe) {

                LOG.error("Error generating sitemap for " + shop.getCode(), ioe);
                return false; // no point to continue if we have IO failure

            }

            return count[0] < to;
        });

        writer.write("<!-- End of Products -->\n");

        return count[0] - from;
    }

    private void appendProductLoc(final OutputStreamWriter writer, final Product product, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(product.getSeo(), product.getProductId(), urlBase, WebParametersKeys.PRODUCT_ID), languages);
    }

    private void appendSkuLoc(final OutputStreamWriter writer, final ProductSku product, final List<String> languages, final String urlBase) throws IOException {
        appendLoc(writer, seoUrl(product.getSeo(), product.getSkuId(), urlBase, WebParametersKeys.SKU_ID), languages);
    }


    private void appendLoc(final OutputStreamWriter writer, final String loc, final List<String> languages) throws IOException {
        writer.write("<url><loc><![CDATA[");
        writer.write(loc);
        writer.write("]]></loc>");
        if (languages.size() > 1) {
            for (final String language : languages) {
                writer.write("<xhtml:link rel=\"alternate\" hreflang=\"");
                writer.write(language);
                writer.write("\" href=\"");
                writer.write(alternativeUrl(loc, language));
                writer.write("\" />");
            }
        }
        writer.write("<changefreq>daily</changefreq></url>\n");
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
        final String urlBase = shop.getDefaultShopPreferredUrl();
        if (urlBase.endsWith("/")) {
            return urlBase + contextPath.substring(1) + "/";
        }
        return urlBase + this.contextPath + "/";
    }

    private interface SitemapXmlCallback {

        void appendShopUrls(OutputStreamWriter writer, Shop shop, List<String> supportedLanguages) throws IOException;

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
