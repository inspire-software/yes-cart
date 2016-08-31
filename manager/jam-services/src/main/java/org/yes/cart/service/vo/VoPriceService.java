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

import org.yes.cart.domain.vo.VoPriceList;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoPriceService {

    /**
     * Get all price lists in the system, filtered by criteria and according to rights, up to max
     * @return list of price lists
     * @throws Exception
     */
    List<VoPriceList> getFiltered(long shopId, String currency, String filter, int max) throws Exception;

    /**
     * Get price list by id.
     *
     * @param id price list id
     * @return price list vo
     * @throws Exception
     */
    VoPriceList getById(long id) throws Exception;

    /**
     * Update given price list.
     *
     * @param vo price list to update
     * @return updated instance
     * @throws Exception
     */
    VoPriceList update(VoPriceList vo) throws Exception;

    /**
     * Create new price list
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoPriceList create(VoPriceList vo) throws Exception;

    /**
     * Remove price list by id.
     *
     * @param id price list id
     * @throws Exception
     */
    void remove(long id) throws Exception;

}
