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

package org.yes.cart.web.service.rest.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yes.cart.web.support.seo.BookmarkService;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 08:38
 */
@Service("restBookmarkMixin")
public class BookmarkMixin {

    @Autowired
    private BookmarkService bookmarkService;


    /**
     * Resolve category PK from string.
     *
     * @param category either PK or URI
     *
     * @return PK for category
     */
    public long resolveCategoryId(final String category) {
        final long categoryId = NumberUtils.toLong(category, 0L);
        if (categoryId > 0L) {
            bookmarkService.saveBookmarkForCategory(category);
            return categoryId;
        }
        final String categoryIdStr = bookmarkService.getCategoryForURI(category);
        return NumberUtils.toLong(categoryIdStr, 0L);
    }

    /**
     * Resolve content PK from string.
     *
     * @param content either PK or URI
     *
     * @return PK for content
     */
    public long resolveContentId(final String content) {
        final long contentId = NumberUtils.toLong(content, 0L);
        if (contentId > 0L) {
            bookmarkService.saveBookmarkForContent(content);
            return contentId;
        }
        final String contentIdStr = bookmarkService.getContentForURI(content);
        return NumberUtils.toLong(contentIdStr, 0L);
    }


    /**
     * Resolve product PK from string.
     *
     * @param product either PK or URI
     *
     * @return PK for product
     */
    public long resolveProductId(final String product) {
        final long productId = NumberUtils.toLong(product, 0L);
        if (productId > 0L) {
            bookmarkService.saveBookmarkForProduct(product);
            return productId;
        }
        final String productIdStr = bookmarkService.getProductForURI(product);
        return NumberUtils.toLong(productIdStr, 0L);
    }


    /**
     * Resolve product SKU PK from string.
     *
     * @param sku either PK or URI
     *
     * @return PK for product SKU
     */
    public long resolveSkuId(final String sku) {
        final long skuId = NumberUtils.toLong(sku, 0L);
        if (skuId > 0L) {
            bookmarkService.saveBookmarkForProduct(sku);
            return skuId;
        }
        final String skuIdStr = bookmarkService.getSkuForURI(sku);
        return NumberUtils.toLong(skuIdStr, 0L);
    }


}
