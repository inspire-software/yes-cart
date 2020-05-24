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

import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;

/**
 * User: denispavlov
 */
public interface VoTaxService {

    /**
     * Get all taxes in the system, filtered by criteria and according to rights, up to max
     *
     * @return list of taxes
     *
     * @throws Exception errors
     */
    VoSearchResult<VoTax> getFilteredTax(VoSearchContext filter) throws Exception;

    /**
     * Get tax by id.
     *
     * @param id tax id
     *
     * @return tax vo
     *
     * @throws Exception errors
     */
    VoTax getTaxById(long id) throws Exception;

    /**
     * Update given tax.
     *
     * @param vo tax to update
     *
     * @return updated instance
     *
     * @throws Exception errors
     */
    VoTax updateTax(VoTax vo) throws Exception;

    /**
     * Create new tax
     *
     * @param vo given instance to persist
     *
     * @return persisted instance
     *
     * @throws Exception errors
     */
    VoTax createTax(VoTax vo) throws Exception;

    /**
     * Remove tax by id.
     *
     * @param id tax id
     *
     * @throws Exception errors
     */
    void removeTax(long id) throws Exception;





    /**
     * Get all taxes in the system, filtered by criteria and according to rights, up to max
     *
     * @return list of taxes
     *
     * @throws Exception errors
     */
    VoSearchResult<VoTaxConfig> getFilteredTaxConfig(VoSearchContext filter) throws Exception;

    /**
     * Get tax by id.
     *
     * @param id tax id
     *
     * @return tax vo
     *
     * @throws Exception errors
     */
    VoTaxConfig getTaxConfigById(long id) throws Exception;

    /**
     * Create new tax
     *
     * @param vo given instance to persist
     *
     * @return persisted instance
     *
     * @throws Exception errors
     */
    VoTaxConfig createTaxConfig(VoTaxConfig vo) throws Exception;

    /**
     * Remove tax by id.
     *
     * @param id tax id
     *
     * @throws Exception errors
     */
    void removeTaxConfig(long id) throws Exception;


}
