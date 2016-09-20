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

import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoTaxService {

    /**
     * Get all taxes in the system, filtered by criteria and according to rights, up to max
     * @return list of taxes
     * @throws Exception
     */
    List<VoTax> getFilteredTax(String shopCode, String currency, String filter, int max) throws Exception;

    /**
     * Get tax by id.
     *
     * @param id tax id
     * @return tax vo
     * @throws Exception
     */
    VoTax getTaxById(long id) throws Exception;

    /**
     * Update given tax.
     *
     * @param vo tax to update
     * @return updated instance
     * @throws Exception
     */
    VoTax updateTax(VoTax vo) throws Exception;

    /**
     * Create new tax
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoTax createTax(VoTax vo) throws Exception;

    /**
     * Remove tax by id.
     *
     * @param id tax id
     * @throws Exception
     */
    void removeTax(long id) throws Exception;





    /**
     * Get all taxes in the system, filtered by criteria and according to rights, up to max
     * @return list of taxes
     * @throws Exception
     */
    List<VoTaxConfig> getFilteredTaxConfig(long taxId, String filter, int max) throws Exception;

    /**
     * Get tax by id.
     *
     * @param id tax id
     * @return tax vo
     * @throws Exception
     */
    VoTaxConfig getTaxConfigById(long id) throws Exception;

    /**
     * Create new tax
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoTaxConfig createTaxConfig(VoTaxConfig vo) throws Exception;

    /**
     * Remove tax by id.
     *
     * @param id tax id
     * @throws Exception
     */
    void removeTaxConfig(long id) throws Exception;


}
