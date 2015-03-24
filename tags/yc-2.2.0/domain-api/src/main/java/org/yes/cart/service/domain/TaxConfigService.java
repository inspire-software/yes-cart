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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.TaxConfig;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 19:51
 */
public interface TaxConfigService extends GenericService<TaxConfig> {

    /**
     * Get tax PK for given product with given combination of shop, currency, country and state.
     *
     * @param shopCode current shop code
     * @param currency currency
     * @param countryCode 2 letter iso code
     * @param stateCode state code
     * @param productCode product code
     *
     * @return PK for tax code (or null if no configuration can be found)
     */
    Long getTaxIdBy(String shopCode, String currency, String countryCode, String stateCode, String productCode);

    /**
     * Manager search function.
     *
     * @param taxId tax id
     * @param countryCode country code (optional)
     * @param stateCode state code (optional)
     * @param productCode product code (optional)
     *
     * @return list of tax configs
     */
    List<TaxConfig> findByTaxId(long taxId, String countryCode, String stateCode, String productCode);

}
