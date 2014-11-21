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


import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.impl.DecoratorUtil;
import org.yes.cart.web.support.seo.BookmarkService;

/**
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:52 PM
 */
public class BookmarkServiceImpl implements BookmarkService {

    private final Cache CATEGORY_DECODE_CACHE;
    private final Cache CATEGORY_ENCODE_CACHE;
    private final Cache CONTENT_DECODE_CACHE;
    private final Cache CONTENT_ENCODE_CACHE;
    private final Cache SKU_DECODE_CACHE;
    private final Cache SKU_ENCODE_CACHE;
    private final Cache PRODUCT_DECODE_CACHE;
    private final Cache PRODUCT_ENCODE_CACHE;

    private final CategoryService categoryService;
    private final ContentService contentService;
    private final ProductService productService;

    /**
     * Construct bookmark service.
     *
     * @param categoryService category service
     * @param contentService  content service
     * @param productService  product service
     * @param cacheManager    cache manager to use
     */
    public BookmarkServiceImpl(final CategoryService categoryService,
                               final ContentService contentService,
                               final ProductService productService,
                               final CacheManager cacheManager) {

        this.categoryService = categoryService;
        this.productService = productService;
        this.contentService = contentService;

        CATEGORY_DECODE_CACHE = cacheManager.getCache("web.bookmarkService-seoCategoryDecode");
        CATEGORY_ENCODE_CACHE = cacheManager.getCache("web.bookmarkService-seoCategoryEncode");
        CONTENT_DECODE_CACHE = cacheManager.getCache("web.bookmarkService-seoContentDecode");
        CONTENT_ENCODE_CACHE = cacheManager.getCache("web.bookmarkService-seoContentEncode");
        SKU_DECODE_CACHE = cacheManager.getCache("web.bookmarkService-seoSkuDecode");
        SKU_ENCODE_CACHE = cacheManager.getCache("web.bookmarkService-seoSkuEncode");
        PRODUCT_DECODE_CACHE = cacheManager.getCache("web.bookmarkService-seoProductDecode");
        PRODUCT_ENCODE_CACHE = cacheManager.getCache("web.bookmarkService-seoProductEncode");


    }

    private String getStringFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (String) wrapper.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForCategory(final String bookmark) {

        String seoData = getStringFromValueWrapper(CATEGORY_ENCODE_CACHE.get(bookmark));
        if (seoData == null) {
            final long categoryId = NumberUtils.toLong(bookmark, 0L);
            if (categoryId > 0L) {

                final String categorySeoUri = categoryService.findSeoUriByCategoryId(categoryId);
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        categorySeoUri
                );

            }
            if (seoData != null) {
                CATEGORY_ENCODE_CACHE.put(bookmark, seoData);
                CATEGORY_DECODE_CACHE.put(seoData, bookmark);
            }
        }

        return seoData;
    }

    /**
     * {@inheritDoc}
     */
    public String getCategoryForURI(final String uri) {

        String id = getStringFromValueWrapper(CATEGORY_DECODE_CACHE.get(uri));
        if (id == null) {
            final Long catId = categoryService.findCategoryIdBySeoUri(uri);
            if (catId != null) {
                saveBookmarkForCategory(catId.toString());
                id = catId.toString();
            }
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForContent(final String bookmark) {

        String seoData = getStringFromValueWrapper(CONTENT_ENCODE_CACHE.get(bookmark));
        if (seoData == null) {
            final long contentId = NumberUtils.toLong(bookmark, 0L);
            if (contentId > 0L) {

                final String contentSeoUri = contentService.findSeoUriByContentId(contentId);
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        contentSeoUri
                );

            }
            if (seoData != null) {
                CONTENT_ENCODE_CACHE.put(bookmark, seoData);
                CONTENT_DECODE_CACHE.put(seoData, bookmark);
            }
        }

        return seoData;
    }

    /**
     * {@inheritDoc}
     */
    public String getContentForURI(final String uri) {

        String id = getStringFromValueWrapper(CONTENT_DECODE_CACHE.get(uri));
        if (id == null) {
            final Long conId = contentService.findContentIdBySeoUri(uri);
            if (conId != null) {
                saveBookmarkForContent(conId.toString());
                id = conId.toString();
            }
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForProduct(final String bookmark) {

        String seoData = getStringFromValueWrapper(PRODUCT_ENCODE_CACHE.get(bookmark));
        if (seoData == null) {
            final long productId = NumberUtils.toLong(bookmark, 0L);
            if (productId > 0L) {
                final String productSeoUri = productService.getSeoUriByProductId(productId);
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        productSeoUri
                );
            }
            if (seoData != null) {
                PRODUCT_ENCODE_CACHE.put(bookmark, seoData);
                PRODUCT_DECODE_CACHE.put(seoData, bookmark);
            }
        }

        return seoData;

    }

    /**
     * {@inheritDoc}
     */
    public String getProductForURI(final String uri) {

        String id = getStringFromValueWrapper(PRODUCT_DECODE_CACHE.get(uri));
        if (id == null) {
            final Long prodId = productService.getProductIdBySeoUri(uri);
            if (prodId != null) {
                saveBookmarkForProduct(prodId.toString());
                id = prodId.toString();
            }
        }

        return id;

    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForSku(final String bookmark) {

        String seoData = getStringFromValueWrapper(SKU_ENCODE_CACHE.get(bookmark));
        if (seoData == null) {
            final long skuId = NumberUtils.toLong(bookmark, 0L);
            if (skuId > 0L) {
                final String productSkuUri = productService.getSeoUriByProductSkuId(skuId);
                seoData = DecoratorUtil.encodeId(
                        bookmark,
                        productSkuUri
                );
            }
            if (seoData != null) {
                SKU_ENCODE_CACHE.put(bookmark, seoData);
                SKU_DECODE_CACHE.put(seoData, bookmark);
            }
        }

        return seoData;

    }

    /**
     * {@inheritDoc}
     */
    public String getSkuForURI(final String uri) {

        String id = getStringFromValueWrapper(SKU_DECODE_CACHE.get(uri));
        if (id == null) {
            final Long skuId = productService.getProductSkuIdBySeoUri(uri);
            if (skuId != null) {
                saveBookmarkForSku(skuId.toString());
                id = skuId.toString();
            }
        }


        return id;

    }
}
