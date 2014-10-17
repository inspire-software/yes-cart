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

package org.yes.cart.web.support.seo;

import org.yes.cart.domain.entity.Product;

/**
 * User: denispavlov
 * Date: 12-08-20
 * Time: 5:41 PM
 */
public interface BookmarkService {

    /**
     * Save (or return cached) bookmark for given category context.
     *
     * @param bookmark bookmark parameter
     * @return SEO data
     */
    String saveBookmarkForCategory(String bookmark);

    /**
     * Get category bookmark id by URI
     * @param uri SEO URI
     * @return category id
     */
    String getCategoryForURI(String uri);

    /**
     * Save (or return cached) bookmark for given content context.
     *
     * @param bookmark bookmark parameter
     * @return SEO data
     */
    String saveBookmarkForContent(String bookmark);

    /**
     * Get content bookmark id by URI
     * @param uri SEO URI
     * @return category id
     */
    String getContentForURI(String uri);

    /**
     * Save (or return cached) bookmark for given product context.
     *
     * @param bookmark bookmark parameter
     * @return SEO data
     */
    String saveBookmarkForProduct(String bookmark);

    /**
     * Get product bookmark id by URI
     * @param uri SEO URI
     * @return product id
     */
    String getProductForURI(String uri);

    /**
     * Save (or return cached) bookmark for given SKU context.
     *
     * @param bookmark bookmark parameter
     * @return SEO data
     */
    String saveBookmarkForSku(String bookmark);


    /**
     * Get sku bookmark id by URI
     * @param uri SEO URI
     * @return sku id
     */
    String getSkuForURI(String uri);

}
