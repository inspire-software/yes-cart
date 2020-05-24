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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 10:51
 */
public interface DtoTaxService extends GenericDTOService<TaxDTO> {


    /**
     * Get tax list by criteria.
     *
     * @param filter filter
     *
     * @return list
     *
     * @throws UnmappedInterfaceException error
     * @throws UnableToCreateInstanceException error
     */
    SearchResult<TaxDTO> findTaxes(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;




}
