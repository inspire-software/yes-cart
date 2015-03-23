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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 11:56
 */
public interface ContentServiceFacade {


    /**
     * Get category if it belongs to given shop.
     *
     * @param contentId category id
     * @param shopId     shop id
     *
     * @return category or null
     */
    Category getContent(long contentId, long shopId);



    /**
     * Get content body for a specific content. If the content belong to given shop then the body is
     * returned for specified locale, otherwise 'null'.
     *
     * See CONTENT_BODY setting.
     *
     * @param contentId content id
     * @param shopId    shop id
     * @param locale locale to get body for
     *
     * @return body or null if none exists for this locale or if content is not available
     */
    String getContentBody(long contentId, long shopId, String locale);

    /**
     * Get content body for content with given URI. The contentUri is either suffix or full URI.
     * This method will attempt first to find shop specific content for given URI by prefixing
     * URI with shop code. Then if content is not found failover will try to find content using
     * URI as is.
     *
     * For example we have content include "terms-and-conditions" in our theme. The to include
     * terms and conditions text we need to invoke getContentBody("terms-and-conditions", shopId, locale).
     * In multi shop storefront this presents a problem since no two content object can have same URI.
     * Therefore this method with first try to resolve the shop code, say "SHOP10". Then if will try to
     * resolve content body "SHOP10_terms-and-conditions". If this content does not exist or does not
     * belong to given shop the method with continue to resolve body of "terms-and-conditions".
     *
     * This way each shop can specify own terms and conditions content and still use the same theme.
     *
     * See CONTENT_BODY setting.
     *
     * @param contentUri content URI
     * @param shopId     shop id
     * @param locale locale to get body for
     *
     * @return body or null if none exists for this locale or if content is not available
     */
    String getContentBody(String contentUri, long shopId, String locale);

    /**
     * Get content body for a specific content. If the content belong to given shop then the body is
     * returned for specified locale, otherwise 'null'.
     *
     * See CONTENT_BODY setting.
     *
     * @param contentId content id
     * @param shopId     shop id
     * @param locale locale to get body for
     * @param context runtime content passed from web module (e.g. current category, product, shopping cart etc)
     *
     * @return body or null if none exists for this locale or if content is not available
     */
    String getDynamicContentBody(long contentId, long shopId, String locale, Map<String, Object> context);

    /**
     * Get content body for content with given URI. The contentUri is either suffix or full URI.
     * This method will attempt first to find shop specific content for given URI by prefixing
     * URI with shop code. Then if content is not found failover will try to find content using
     * URI as is.
     *
     * For example we have content include "terms-and-conditions" in our theme. The to include
     * terms and conditions text we need to invoke getContentBody("terms-and-conditions", shopId, locale).
     * In multi shop storefront this presents a problem since no two content object can have same URI.
     * Therefore this method with first try to resolve the shop code, say "SHOP10". Then if will try to
     * resolve content body "SHOP10_terms-and-conditions". If this content does not exist or does not
     * belong to given shop the method with continue to resolve body of "terms-and-conditions".
     *
     * This way each shop can specify own terms and conditions content and still use the same theme.
     *
     * See CONTENT_BODY setting.
     *
     * @param contentUri content URI
     * @param shopId     shop id
     * @param locale locale to get body for
     * @param context runtime content passed from web module (e.g. current category, product, shopping cart etc)
     *
     * @return body or null if none exists for this locale or if content is not available
     */
    String getDynamicContentBody(String contentUri, long shopId, String locale, Map<String, Object> context);



    /**
     * Get current category menu, or top categories if category is not specified or does
     * not belong to this shop.
     *
     * @param currentContentId current category (optional)
     * @param shopId            current shop
     *
     * @return list of sub categories (or top shop categories)
     */
    List<Category> getCurrentContentMenu(final long currentContentId, long shopId);


    /**
     * Get category viewing image size configuration.
     *
     * @param contentId category PK
     * @param shopId     current shop
     *
     * @return first width, second height
     */
    Pair<String, String> getContentListImageSizeConfig(long contentId, long shopId);


    /**
     * Get pagination options configuration.
     *
     * @param contentId category PK
     * @param shopId     current shop
     *
     * @return pagination options
     */
    List<String> getItemsPerPageOptionsConfig(long contentId, long shopId);


    /**
     * Get number of columns options configuration.
     *
     * @param contentId  category PK
     * @param shopId     current shop
     *
     * @return category columns options
     */
    int getContentListColumnOptionsConfig(long contentId, long shopId);




}
