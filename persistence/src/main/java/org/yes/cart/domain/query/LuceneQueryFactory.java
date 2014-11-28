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

package org.yes.cart.domain.query;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface LuceneQueryFactory {

    /**
     * Get the combined from query chain query.
     * The current query will be last in this chain.
     * Chain will be truncated if <code>currentQuery</code> already present in
     * list
     *
     * @param navigationContext navigation context.
     * @param param        additional filter parameter
     * @param value        additional filter value(s)
     * @return combined from chain query
     */
    NavigationContext getSnowBallQuery(NavigationContext navigationContext, String param, Object value);

    /**
     * Get the queries chain.
     * <p/>
     * Note about search: Search can be performed on entire shop or in particular category.
     * Entire search if size of categories is one and first category is 0
     * otherwise need to use all sub categories, that belong to given category list.
     *
     * @param shopId                the current shop id
     * @param requestParameters     web request parameters
     * @param categories            given category ids
     * @return ordered by cookie name list of cookies
     */
    NavigationContext getFilteredNavigationQueryChain(Long shopId, List<Long> categories, Map<String, List> requestParameters);

}
