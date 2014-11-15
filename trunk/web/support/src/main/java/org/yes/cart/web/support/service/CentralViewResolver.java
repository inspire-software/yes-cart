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

import org.apache.lucene.search.BooleanQuery;

import java.util.List;
import java.util.Map;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 9:35 PM
 */
public interface CentralViewResolver {

    /**
     * Resolve central renderer label.
     *
     * @param parameters            request parameters map
     * @return resolved main panel renderer label if resolved, otherwise null
     */
    String resolveMainPanelRendererLabel(Map parameters);

    /**
     * Construct <code>BooleanQuery</code> by given parameters.
     *
     * @param queriesChain  query chain obtained from url
     * @param shopId        current shop id
     * @param categories    current category subcategories to include
     * @param viewLabel     resolved view label
     * @param itemId        sku or product id
     * @return <code>BooleanQuery</code> in case if renderer label has a registered query builder, otherwise null.
     */
    BooleanQuery getBooleanQuery(BooleanQuery queriesChain,
                                 long shopId,
                                 List<Long> categories,
                                 String viewLabel,
                                 String itemId);



}
