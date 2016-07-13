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

package org.yes.cart.domain.query;

import org.yes.cart.domain.misc.Pair;

import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/03/2016
 * Time: 16:26
 */
public interface ShopSearchSupportService {

    /**
     * Get category id applicable for given current category. Depending on whether category permits
     * search in subcategories or not this method returns one of more category PK.
     *
     * categoryId is always returned as part of list
     *
     * @param categoryId current category PK
     * @param shopId     current shop
     *
     * @return list of searchable categories,
     */
    Pair<List<Long>, Boolean> getSearchCategoriesIds(long categoryId, long shopId);

    /**
     * Number of days that counts towards newarrival products
     *
     * @param categoryId current category
     * @param shopId current shop
     *
     * @return max number of days
     */
    Date getCategoryNewArrivalDate(long categoryId, long shopId);


}
