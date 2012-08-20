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

import com.google.common.collect.MapMaker;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.impl.DecoratorUtil;
import org.yes.cart.web.support.seo.BookmarkService;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:52 PM
 */
public class BookmarkServiceImpl implements BookmarkService {

    private static final ConcurrentMap<String, String> CATEGORY_DECODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();
    private static final ConcurrentMap<String, String> CATEGORY_ENCODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    private static final ConcurrentMap<String, String> SKU_DECODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    private static final ConcurrentMap<String, String> SKU_ENCODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    private static final ConcurrentMap<String, String> PRODUCT_DECODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();
    private static final ConcurrentMap<String, String> PRODUCT_ENCODE_CACHE = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expireAfterWrite(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();

    private final CategoryService categoryService;
    private final ProductService productService;

    public BookmarkServiceImpl(final CategoryService categoryService, final ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /** {@inheritDoc} */
    public String saveBookmarkForCategory(final String bookmark) {

        String seoData = CATEGORY_ENCODE_CACHE.get(bookmark);
        if (seoData == null) {
            final Category category = categoryService.getById(NumberUtils.toLong(bookmark));
            if (category != null) {
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        category.getSeo()
                );
            }
            if (seoData != null) {
                CATEGORY_ENCODE_CACHE.put(bookmark, seoData);
                CATEGORY_DECODE_CACHE.put(seoData, bookmark);
            }
        }
        return seoData;
    }

    /** {@inheritDoc} */
    public String getCategoryForURI(final String uri) {

        String id = CATEGORY_DECODE_CACHE.get(uri);
        if (id == null) {
            final Long catId = categoryService.getCategoryIdBySeoUri(uri);
            if (catId != null) {
                saveBookmarkForCategory(catId.toString());
                return catId.toString();
            }
            return null;
        }
        return id;
    }

    /** {@inheritDoc} */
    public String saveBookmarkForProduct(final String bookmark) {

        String seoData = PRODUCT_ENCODE_CACHE.get(bookmark);
        if (seoData == null) {
            final Product product = productService.getProductById(NumberUtils.toLong(bookmark));
            if (product != null) {
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        product.getSeo()
                );
            }
            if (seoData != null) {
                PRODUCT_ENCODE_CACHE.put(bookmark, seoData);
                PRODUCT_DECODE_CACHE.put(seoData, bookmark);
            }
        }
        return seoData;

    }

    /** {@inheritDoc} */
    public String saveBookmarkForProduct(final Product product) {

        final String bookmark = String.valueOf(product.getId());
        String seoData = PRODUCT_ENCODE_CACHE.get(bookmark);
        if (seoData == null) {
            if (product != null) {
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        product.getSeo()
                );
            }
            if (seoData != null) {
                PRODUCT_ENCODE_CACHE.put(bookmark, seoData);
                PRODUCT_DECODE_CACHE.put(seoData, bookmark);
            }
        }
        return seoData;

    }

    /** {@inheritDoc} */
    public String getProductForURI(final String uri) {

        String id = PRODUCT_DECODE_CACHE.get(uri);
        if (id == null) {
            final Long prodId = productService.getProductIdBySeoUri(uri);
            if (prodId != null) {
                saveBookmarkForProduct(prodId.toString());
                return prodId.toString();
            }
            return null;
        }
        return id;

    }

    /** {@inheritDoc} */
    public String saveBookmarkForSku(final String bookmark) {

        String seoData = SKU_ENCODE_CACHE.get(bookmark);
        if (seoData == null) {
            final ProductSku productSku = productService.getSkuById(NumberUtils.toLong(bookmark));
            if (productSku != null) {
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        productSku.getSeo()
                );
            }
            if (seoData != null) {
                SKU_ENCODE_CACHE.put(bookmark, seoData);
                SKU_DECODE_CACHE.put(seoData, bookmark);
            }
        }
        return seoData;

    }

    /** {@inheritDoc} */
    public String getSkuForURI(final String uri) {

        String id = SKU_DECODE_CACHE.get(uri);
        if (id == null) {
            final Long skuId = productService.getProductSkuIdBySeoUri(uri);
            if (skuId != null) {
                saveBookmarkForSku(skuId.toString());
                return skuId.toString();
            }
            return null;
        }
        return id;

    }
}
