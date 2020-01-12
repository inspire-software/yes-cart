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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

/**
 * Bulk price lists service.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:01 PM
 */
public interface DtoPriceListsService {


    /**
     * Get price list by criteria.
     *
     * @param filter filter
     *
     * @return list
     *
     * @throws UnmappedInterfaceException error
     * @throws UnableToCreateInstanceException error
     */
    SearchResult<PriceListDTO> findPrices(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get by id
     *
     * @param id id
     * @return price list entry
     */
    PriceListDTO getById(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create or update price object.
     *
     * @param price price
     * @return persistent price object
     */
    PriceListDTO createPrice(PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create or update price object.
     *
     * @param price price
     * @return persistent price object
     */
    PriceListDTO updatePrice(PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Remove sku price object by given pk value
     *
     * @param skuPriceId given pk value.
     */
    void removePrice(long skuPriceId) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
