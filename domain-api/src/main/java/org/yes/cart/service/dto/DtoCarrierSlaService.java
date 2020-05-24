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

import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCarrierSlaService extends GenericDTOService<CarrierSlaDTO> {


    /**
     * Get carrier SLA list by criteria.
     *
     * @param filter filter
     *
     * @return list
     *
     * @throws UnmappedInterfaceException error
     * @throws UnableToCreateInstanceException error
     */
    SearchResult<CarrierSlaDTO> findCarrierSlas(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get shipping SLA by carrier Id.
     *
     * @param carrierId given carrier id
     * @return list of SLA, that belongs to given carrier id
     */
    List<CarrierSlaDTO> findByCarrier(long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get shipping SLA by carrier Id.
     *
     * @param filter filter for partial match.
     * @param page page number starting from 0
     * @param pageSize size of page
     *
     * @return list of SLA, that belongs to given carrier id
     */
    List<CarrierSlaDTO> findBy(String filter, int page, int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
