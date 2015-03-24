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

import org.yes.cart.domain.entity.Tax;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 19:23
 */
public interface TaxService extends GenericService<Tax> {

    /**
     * Get by PK.
     *
     * @param pk PK.
     *
     * @return tax
     */
    Tax getById(long pk);

    /**
     * Get all taxes by shop code and currency.
     *
     * @param shopCode shop code
     * @param currency currency
     *
     * @return list of taxes
     */
    List<Tax> getTaxesByShopCode(String shopCode, String currency);

    /**
     * Manager search function to find taxes by given parameters.
     *
     * @param code tax code
     * @param shopCode optional shop code
     * @param currency optional currency
     *
     * @return taxes that satisfy criteria
     */
    List<Tax> findByParameters(String code,
                               String shopCode,
                               String currency);

}
