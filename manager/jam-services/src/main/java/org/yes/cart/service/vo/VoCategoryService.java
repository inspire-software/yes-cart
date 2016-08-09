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

import org.yes.cart.domain.vo.VoCategory;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public interface VoCategoryService {

    /**
     * Get all categories in the system, filtered according to rights
     * @return
     * @throws Exception
     */
    List<VoCategory> getAll() throws Exception;

    /**
     * Get all categories in the system, filtered by criteria and according to rights, up to max
     * @return
     * @throws Exception
     */
    List<VoCategory> getFiltered(String filter, int max) throws Exception;

    /**
     * Get category by id.
     *
     * @param id
     * @return category vo
     * @throwsException
     */
    VoCategory getById(long id) throws Exception;

    /**
     * Create new category..
     * @param voCategory category
     * @return persistent version
     * @throws Exception
     */
    VoCategory create(VoCategory voCategory)  throws Exception;

}
