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

package org.yes.cart.web.support.seo.impl;

import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:46 PM
 */
public class SitemapXmlServiceImpl implements SitemapXmlService {

    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ContentService contentService;
    private final ProductService productService;
    private final LanguageService languageService;

    public SitemapXmlServiceImpl(final ShopService shopService,
                                 final CategoryService categoryService,
                                 final ContentService contentService,
                                 final ProductService productService,
                                 final LanguageService languageService) {
        this.shopService = shopService;
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.productService = productService;
        this.languageService = languageService;
    }

    /** {@inheritDoc} */
    @Override
    public String generateSitemapXml(final String shopCode) {

        final StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
        xml.append("<!-- YecCart sitemap generator. ").append(new Date()).append(" -->\n");

        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {

            List<String> supportedLanguages = languageService.getSupportedLanguages(shopCode);
            if (supportedLanguages == null) {
                supportedLanguages = Arrays.asList("en", "ru");
            }

            appendShopUrls(xml, shop, supportedLanguages);

        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private void appendShopUrls(final StringBuilder xml, final Shop shop, final List<String> languages) {

        final String urlBase = getShopBaseUrl(shop);

        xml.append("<!-- Main -->\n");

        appendHomeLoc(xml, languages, urlBase);

        xml.append("<!-- Categories -->\n");

        final List<Category> categories = categoryService.getTopLevelCategories(shop);
        for (final Category category : categories) {

            final Set<Category> children = categoryService.getChildCategoriesRecursive(category.getCategoryId());

            if (children != null) {

                for (final Category child : children) {

                    appendCategoryLoc(xml, child, languages, urlBase);

                }

            }

        }

        xml.append("<!-- Content -->\n");

        final Category root = contentService.getRootContent(shop.getShopId(), false);
        if (root != null) {

            final Set<Category> content = contentService.getChildContentRecursive(root.getId());

            if (content != null) {

                for (final Category contentItem : content) {

                    if (contentItem.getCategoryId() != root.getCategoryId()) {

                        appendContentLoc(xml, contentItem, languages, urlBase);

                    }

                }

            }

        }

        xml.append("<!-- Products -->\n");

        for (final Category category : categories) {

            final Set<Category> children = categoryService.getChildCategoriesRecursive(category.getCategoryId());

            if (children != null) {

                for (final Category child : children) {

                    final List<Product> products = productService.getProductByCategory(child.getCategoryId());

                    if (products != null) {

                        for (final Product product : products) {

                            appendProductLoc(xml, product, languages, urlBase);

                            if (product.isMultiSkuProduct()) {

                                for (final ProductSku sku : product.getSku()) {

                                    appendSkuLoc(xml, sku, languages, urlBase);

                                }

                            }
                        }

                    }

                }

            }

        }


    }

    private void appendHomeLoc(final StringBuilder xml, final List<String> languages, final String urlBase) {
        appendLoc(xml, urlBase, languages);
    }

    private void appendCategoryLoc(final StringBuilder xml, final Category category, final List<String> languages, final String urlBase) {
        appendLoc(xml, seoUrl(category.getSeo(), category.getCategoryId(), urlBase, WebParametersKeys.CATEGORY_ID), languages);
    }

    private void appendContentLoc(final StringBuilder xml, final Category category, final List<String> languages, final String urlBase) {
        appendLoc(xml, seoUrl(category.getSeo(), category.getCategoryId(), urlBase, WebParametersKeys.CONTENT_ID), languages);
    }

    private void appendProductLoc(final StringBuilder xml, final Product product, final List<String> languages, final String urlBase) {
        appendLoc(xml, seoUrl(product.getSeo(), product.getProductId(), urlBase, WebParametersKeys.PRODUCT_ID), languages);
    }

    private void appendSkuLoc(final StringBuilder xml, final ProductSku product, final List<String> languages, final String urlBase) {
        appendLoc(xml, seoUrl(product.getSeo(), product.getSkuId(), urlBase, WebParametersKeys.SKU_ID), languages);
    }


    private void appendLoc(final StringBuilder xml, final String loc, final List<String> languages) {
        for (final String urlLang : languages) {
            xml.append("<url><loc>").append(alternativeUrl(loc, urlLang)).append("</loc>");
            for (final String language : languages) {
                xml.append("<xhtml:link rel=\"alternate\" hreflang=\"").append(language).append("\" href=\"")
                        .append(alternativeUrl(loc, language)).append("\" />");
            }
            xml.append("<changefreq>daily</changefreq></url>\n");
        }
    }


    private String seoUrl(final Seo seo, final long pk, final String urlBase, final String namespace) {
        if (seo != null && seo.getUri() != null) {
            return urlBase + namespace + "/" + seo.getUri();
        }
        return urlBase + namespace + "/" + pk;
    }


    private String alternativeUrl(final String original, final String language) {
        if (original.endsWith("/")) {
            return original + ChangeLocaleCartCommandImpl.CMD_KEY + "/" + language;
        }
        return original + "/" + ChangeLocaleCartCommandImpl.CMD_KEY + "/" + language;
    }

    private String getShopBaseUrl(final Shop shop) {
        final String urlBase = shop.getDefaultShopUrl();
        if (urlBase.endsWith("/")) {
            return urlBase + "yes-shop/";
        }
        return urlBase + "/yes-shop/";
    }
}
