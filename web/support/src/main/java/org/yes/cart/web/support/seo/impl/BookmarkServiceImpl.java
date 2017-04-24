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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.domain.misc.Pair;
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

    private static final Logger LOG = LoggerFactory.getLogger(BookmarkServiceImpl.class);

    private final Cache CATEGORY_CACHE;
    private final Cache CONTENT_CACHE;
    private final Cache SKU_CACHE;
    private final Cache PRODUCT_CACHE;

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

        CATEGORY_CACHE = cacheManager.getCache("web.bookmarkService-seoCategory");
        CONTENT_CACHE = cacheManager.getCache("web.bookmarkService-seoContent");
        SKU_CACHE = cacheManager.getCache("web.bookmarkService-seoSku");
        PRODUCT_CACHE = cacheManager.getCache("web.bookmarkService-seoProduct");

    }

    private Pair<Long, String> getPairOfPkAndUriFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (Pair<Long, String>) wrapper.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForCategory(final String bookmark) {

        Pair<Long, String> seoData = getPairOfPkAndUriFromValueWrapper(CATEGORY_CACHE.get(bookmark));

        if (seoData == null) {

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingCategoryId = categoryService.findCategoryIdBySeoUri(bookmark);
            if (existingCategoryId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                final Pair<Long, String> pkAndUri = new Pair<Long, String>(existingCategoryId, bookmark);
                CATEGORY_CACHE.put(existingCategoryId.toString(), pkAndUri);
                CATEGORY_CACHE.put(bookmark, pkAndUri);
                seoData = pkAndUri;
                LOG.trace("Saving category bookmark {} is SEO for {}", bookmark, existingCategoryId);
            }

            if (seoData == null) {
                final long categoryId = NumberUtils.toLong(bookmark, 0L);
                if (categoryId > 0L) {
                    final String categorySeoUri = categoryService.findSeoUriByCategoryId(categoryId);
                    if (StringUtils.isNotBlank(categorySeoUri)) {
                        // This is a valid category and categorySeoUri will be either SEO URI or categoryId as String
                        final Pair<Long, String> pkAndUri = new Pair<Long, String>(categoryId, categorySeoUri);
                        CATEGORY_CACHE.put(bookmark, pkAndUri);
                        CATEGORY_CACHE.put(categorySeoUri, pkAndUri);
                        seoData = pkAndUri;
                        LOG.trace("Saving category bookmark {} is PK for {}", bookmark, categorySeoUri);
                    } // else This is may be a numeric SEO uri

                }
            }
        }

        LOG.trace("Category bookmark {} resolves to {}", bookmark, seoData);

        return seoData != null ? seoData.getSecond() : null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getCategoryForURI(final String uri) {

        Pair<Long, String> id = getPairOfPkAndUriFromValueWrapper(CATEGORY_CACHE.get(uri));
        if (id == null) {
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForCategory(uri);
            if (seo != null) {
                id = getPairOfPkAndUriFromValueWrapper(CATEGORY_CACHE.get(seo));
                LOG.trace("Category URI {} resolves to {} after bookmarking", uri, id);
            }
        } else {
            LOG.trace("Category URI {} resolves to {}", uri, id);
        }

        return id != null ? id.getFirst() : null;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForContent(final String bookmark) {

        Pair<Long, String> seoData = getPairOfPkAndUriFromValueWrapper(CONTENT_CACHE.get(bookmark));

        if (seoData == null) {

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingContentId = contentService.findContentIdBySeoUri(bookmark);
            if (existingContentId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                final Pair<Long, String> pkAndUri = new Pair<Long, String>(existingContentId, bookmark);
                CONTENT_CACHE.put(existingContentId.toString(), pkAndUri);
                CONTENT_CACHE.put(bookmark, pkAndUri);
                seoData = pkAndUri;
                LOG.trace("Saving content bookmark {} is SEO for {}", bookmark, existingContentId);
            }

            if (seoData == null) {
                final long contentId = NumberUtils.toLong(bookmark, 0L);
                if (contentId > 0L) {
                    final String contentSeoUri = contentService.findSeoUriByContentId(contentId);
                    if (StringUtils.isNotBlank(contentSeoUri)) {
                        // This is a valid category and contentSeoUri will be either SEO URI or contentId as String
                        final Pair<Long, String> pkAndUri = new Pair<Long, String>(contentId, contentSeoUri);
                        CONTENT_CACHE.put(bookmark, pkAndUri);
                        CONTENT_CACHE.put(contentSeoUri, pkAndUri);
                        seoData = pkAndUri;
                        LOG.trace("Saving content bookmark {} is PK for {}", bookmark, contentSeoUri);
                    } // else This is may be a numeric SEO uri
                }
            }
        }

        LOG.trace("Content bookmark {} resolves to {}", bookmark, seoData);

        return seoData != null ? seoData.getSecond() : null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getContentForURI(final String uri) {

        Pair<Long, String> id = getPairOfPkAndUriFromValueWrapper(CONTENT_CACHE.get(uri));
        if (id == null) {
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForContent(uri);
            if (seo != null) {
                id = getPairOfPkAndUriFromValueWrapper(CONTENT_CACHE.get(seo));
                LOG.trace("Content URI {} resolves to {} after bookmarking", uri, id);
            }
        } else {
            LOG.trace("Content URI {} resolves to {}", uri, id);
        }

        return id != null ? id.getFirst() : null;
    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForProduct(final String bookmark) {

        Pair<Long, String> seoData = getPairOfPkAndUriFromValueWrapper(PRODUCT_CACHE.get(bookmark));

        if (seoData == null) {

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingProductId = productService.findProductIdBySeoUri(bookmark);
            if (existingProductId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                final Pair<Long, String> pkAndUri = new Pair<Long, String>(existingProductId, bookmark);
                PRODUCT_CACHE.put(existingProductId.toString(), pkAndUri);
                PRODUCT_CACHE.put(bookmark, pkAndUri);
                seoData = pkAndUri;
                LOG.trace("Saving product bookmark {} is SEO for {}", bookmark, existingProductId);
            }

            if (seoData == null) {
                final long productId = NumberUtils.toLong(bookmark, 0L);
                if (productId > 0L) {
                    final String productSeoUri = productService.findSeoUriByProductId(productId);
                    if (StringUtils.isNotBlank(productSeoUri)) {
                        // This is a valid product and productSeoUri will be either SEO URI or productId as String
                        final Pair<Long, String> pkAndUri = new Pair<Long, String>(productId, productSeoUri);
                        PRODUCT_CACHE.put(bookmark, pkAndUri);
                        PRODUCT_CACHE.put(productSeoUri, pkAndUri);
                        seoData = pkAndUri;
                        LOG.trace("Saving product bookmark {} is PK for {}", bookmark, productSeoUri);
                    }
                }
            }
        }

        LOG.trace("Product bookmark {} resolves to {}", bookmark, seoData);

        return seoData != null ? seoData.getSecond() : null;

    }

    /**
     * {@inheritDoc}
     */
    public Long getProductForURI(final String uri) {

        Pair<Long, String> id = getPairOfPkAndUriFromValueWrapper(PRODUCT_CACHE.get(uri));
        if (id == null) {
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForProduct(uri);
            if (seo != null) {
                id = getPairOfPkAndUriFromValueWrapper(PRODUCT_CACHE.get(seo));
                LOG.trace("Product URI {} resolves to {} after bookmarking", uri, id);
            }
        } else {
            LOG.trace("Product URI {} resolves to {}", uri, id);
        }

        return id != null ? id.getFirst() : null;

    }

    /**
     * {@inheritDoc}
     */
    public String saveBookmarkForSku(final String bookmark) {

        Pair<Long, String> seoData = getPairOfPkAndUriFromValueWrapper(SKU_CACHE.get(bookmark));

        if (seoData == null) {

            // Need to check the SEO version first, it could be a numeric SEO uri too
            final Long existingSkuId = productService.findProductSkuIdBySeoUri(bookmark);
            if (existingSkuId != null) {
                // The bookmark is SEO Uri so encoding it is bookmark itself, decoding however should return PK
                final Pair<Long, String> pkAndUri = new Pair<Long, String>(existingSkuId, bookmark);
                SKU_CACHE.put(existingSkuId.toString(), pkAndUri);
                SKU_CACHE.put(bookmark, pkAndUri);
                seoData = pkAndUri;
                LOG.trace("Saving SKU bookmark {} is SEO for {}", bookmark, existingSkuId);
            }

            if (seoData == null) {
                final long skuId = NumberUtils.toLong(bookmark, 0L);
                if (skuId > 0L) {
                    final String productSkuUri = productService.findSeoUriByProductSkuId(skuId);
                    if (StringUtils.isNotBlank(productSkuUri)) {
                        // This is a valid SKU and productSkuUri will be either SEO URI or skuId as String
                        final Pair<Long, String> pkAndUri = new Pair<Long, String>(skuId, productSkuUri);
                        SKU_CACHE.put(bookmark, pkAndUri);
                        SKU_CACHE.put(productSkuUri, pkAndUri);
                        seoData = pkAndUri;
                        LOG.trace("Saving SKU bookmark {} is PK for {}", bookmark, productSkuUri);
                    }
                }
            }
        }

        LOG.trace("SKU bookmark {} resolves to {}", bookmark, seoData);

        return seoData != null ? seoData.getSecond() : null;

    }

    /**
     * {@inheritDoc}
     */
    public Long getSkuForURI(final String uri) {

        Pair<Long, String> id = getPairOfPkAndUriFromValueWrapper(SKU_CACHE.get(uri));
        if (id == null) {
            // resolve bookmarks, then try cache again
            final String seo = saveBookmarkForSku(uri);
            if (seo != null) {
                id = getPairOfPkAndUriFromValueWrapper(SKU_CACHE.get(seo));
                LOG.trace("SKU URI {} resolves to {} after bookmarking", uri, id);
            }
        } else {
            LOG.trace("SKU URI {} resolves to {}", uri, id);
        }

        return id != null ? id.getFirst() : null;

    }

}
