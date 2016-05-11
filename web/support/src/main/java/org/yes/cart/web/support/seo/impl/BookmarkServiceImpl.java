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


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ProductService;
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

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingCategoryId = categoryService.findCategoryIdBySeoUri(bookmark);
            if (existingCategoryId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                CATEGORY_ENCODE_CACHE.put(existingCategoryId.toString(), bookmark);
                CATEGORY_ENCODE_CACHE.put(bookmark, bookmark);
                CATEGORY_DECODE_CACHE.put(bookmark, existingCategoryId.toString());
                seoData = bookmark;
            }

            if (seoData == null) {
                final long categoryId = NumberUtils.toLong(bookmark, 0L);
                if (categoryId > 0L) {
                    final String categorySeoUri = categoryService.findSeoUriByCategoryId(categoryId);
                    if (StringUtils.isNotBlank(categorySeoUri)) {
                        // This is a valid category and categorySeoUri will be either SEO URI or categoryId as String
                        CATEGORY_ENCODE_CACHE.put(bookmark, categorySeoUri);
                        CATEGORY_ENCODE_CACHE.put(categorySeoUri, categorySeoUri);
                        CATEGORY_DECODE_CACHE.put(categorySeoUri, bookmark);
                        seoData = categorySeoUri;
                    } // else This is may be a numeric SEO uri

                }
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
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForCategory(uri);
            if (seo != null) {
                id = getStringFromValueWrapper(CATEGORY_DECODE_CACHE.get(seo));
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

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingContentId = contentService.findContentIdBySeoUri(bookmark);
            if (existingContentId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                CONTENT_ENCODE_CACHE.put(existingContentId.toString(), bookmark);
                CONTENT_ENCODE_CACHE.put(bookmark, bookmark);
                CONTENT_DECODE_CACHE.put(bookmark, existingContentId.toString());
                seoData = bookmark;
            }

            if (seoData == null) {
                final long contentId = NumberUtils.toLong(bookmark, 0L);
                if (contentId > 0L) {
                    final String contentSeoUri = contentService.findSeoUriByContentId(contentId);
                    if (StringUtils.isNotBlank(contentSeoUri)) {
                        // This is a valid category and contentSeoUri will be either SEO URI or contentId as String
                        CONTENT_ENCODE_CACHE.put(bookmark, contentSeoUri);
                        CONTENT_ENCODE_CACHE.put(contentSeoUri, contentSeoUri);
                        CONTENT_DECODE_CACHE.put(contentSeoUri, bookmark);
                        seoData = contentSeoUri;
                    } // else This is may be a numeric SEO uri
                }
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
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForContent(uri);
            if (seo != null) {
                id = getStringFromValueWrapper(CONTENT_DECODE_CACHE.get(seo));
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

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingProductId = productService.findProductIdBySeoUri(bookmark);
            if (existingProductId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                PRODUCT_ENCODE_CACHE.put(existingProductId.toString(), bookmark);
                PRODUCT_ENCODE_CACHE.put(bookmark, bookmark);
                PRODUCT_DECODE_CACHE.put(bookmark, existingProductId.toString());
                seoData = bookmark;
            }

            if (seoData == null) {
                final long productId = NumberUtils.toLong(bookmark, 0L);
                if (productId > 0L) {
                    final String productSeoUri = productService.findSeoUriByProductId(productId);
                    if (StringUtils.isNotBlank(productSeoUri)) {
                        // This is a valid category and contentSeoUri will be either SEO URI or productId as String
                        PRODUCT_ENCODE_CACHE.put(bookmark, productSeoUri);
                        PRODUCT_ENCODE_CACHE.put(productSeoUri, productSeoUri);
                        PRODUCT_DECODE_CACHE.put(productSeoUri, bookmark);
                        seoData = productSeoUri;
                    }
                }
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
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForProduct(uri);
            if (seo != null) {
                id = getStringFromValueWrapper(PRODUCT_DECODE_CACHE.get(seo));
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

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingSkuId = productService.findProductSkuIdBySeoUri(bookmark);
            if (existingSkuId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                SKU_ENCODE_CACHE.put(existingSkuId.toString(), bookmark);
                SKU_ENCODE_CACHE.put(bookmark, bookmark);
                SKU_DECODE_CACHE.put(bookmark, existingSkuId.toString());
                seoData = bookmark;
            }

            if (seoData == null) {
                final long skuId = NumberUtils.toLong(bookmark, 0L);
                if (skuId > 0L) {
                    final String productSkuUri = productService.findSeoUriByProductSkuId(skuId);
                    if (StringUtils.isNotBlank(productSkuUri)) {
                        // This is a valid category and contentSeoUri will be either SEO URI or skuId as String
                        SKU_ENCODE_CACHE.put(bookmark, productSkuUri);
                        SKU_ENCODE_CACHE.put(productSkuUri, productSkuUri);
                        SKU_DECODE_CACHE.put(productSkuUri, bookmark);
                        seoData = productSkuUri;
                    }
                }
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
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForSku(uri);
            if (seo != null) {
                id = getStringFromValueWrapper(SKU_DECODE_CACHE.get(seo));
            }
        }


        return id;

    }
}
