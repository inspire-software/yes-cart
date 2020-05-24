/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.domain.vo.VoShopSummary;

import java.util.List;

/**
 * Created by Igor_Azarny on 3- may -2016.
 */
public interface VoShopCategoryService {

    /**
     * Get all assigned to shop categories.
     *
     * @param shopId shop id
     *
     * @return list of assigned categories
     */
    List<VoCategory> getAllByShopId(final long shopId) throws Exception;

    /**
     * Get summary information for given shop.
     *
     * @param summary summary object to fill data for
     * @param shopId given shop
     * @param lang locale for localised names
     *
     * @throws Exception errors
     */
    void fillShopSummaryDetails(VoShopSummary summary, long shopId, String lang) throws Exception;

    /**
     * Update categories assigned to shop.
     *
     * @param shopId shop id
     * @param voCategories all shop categories
     *
     * @return list of assigned categories
     */
    List<VoCategory> update(final long shopId, List<VoCategory> voCategories) throws Exception;


}
