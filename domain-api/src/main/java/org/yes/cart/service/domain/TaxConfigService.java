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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.TaxConfig;

import java.util.List;
import java.util.Map;

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
     * Find tax configs by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list.
     */
    List<TaxConfig> findTaxConfigs(int start,
                                   int offset,
                                   String sort,
                                   boolean sortDescending,
                                   Map<String, List> filter);

    /**
     * Find tax configs by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findTaxConfigCount(Map<String, List> filter);



}
