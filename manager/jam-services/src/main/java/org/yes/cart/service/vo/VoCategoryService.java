/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueCategory;
import org.yes.cart.domain.vo.VoCategory;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public interface VoCategoryService {

    /**
     * Get all categories in the system, filtered according to rights
     * @return list of categories
     * @throws Exception
     */
    List<VoCategory> getAll() throws Exception;

    /**
     * Get all categories in the system, filtered by criteria and according to rights, up to max
     * @return list of categories
     * @throws Exception
     */
    List<VoCategory> getFiltered(String filter, int max) throws Exception;

    /**
     * Get category by id.
     *
     * @param id pk
     * @return category vo
     * @throws Exception
     */
    VoCategory getById(long id) throws Exception;

    /**
     * Update category.
     * @param voCategory category
     * @return persistent version
     * @throws Exception
     */
    VoCategory update(VoCategory voCategory)  throws Exception;

    /**
     * Create new category.
     * @param voCategory category
     * @return persistent version
     * @throws Exception
     */
    VoCategory create(VoCategory voCategory)  throws Exception;

    /**
     * Remove category by id.
     *
     * @param id category
     * @throws Exception
     */
    void remove(long id) throws Exception;


    /**
     * Get supported attributes by given category
     * @param categoryId given category id
     * @return attributes
     * @throws Exception
     */
    List<VoAttrValueCategory> getCategoryAttributes(long categoryId) throws Exception;


    /**
     * Update the category attributes.
     *
     * @param vo category attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return category attributes.
     * @throws Exception
     */
    List<VoAttrValueCategory> update(List<MutablePair<VoAttrValueCategory, Boolean>> vo) throws Exception;

}
