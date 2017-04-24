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
        final Long categoryId = bookmarkService.getCategoryForURI(category);
        return categoryId != null ? categoryId : 0L;
    }

    /**
     * Resolve content PK from string.
     *
     * @param content either PK or URI
     *
     * @return PK for content
     */
    public long resolveContentId(final String content) {
        final Long contentId = bookmarkService.getContentForURI(content);
        return contentId != null ? contentId : 0L;
    }


    /**
     * Resolve product PK from string.
     *
     * @param product either PK or URI
     *
     * @return PK for product
     */
    public long resolveProductId(final String product) {
        final Long productId = bookmarkService.getProductForURI(product);
        return productId != null ? productId : 0L;
    }


    /**
     * Resolve product SKU PK from string.
     *
     * @param sku either PK or URI
     *
     * @return PK for product SKU
     */
    public long resolveSkuId(final String sku) {
        final Long skuId = bookmarkService.getSkuForURI(sku);
        return skuId != null ? skuId : 0L;
    }


}
