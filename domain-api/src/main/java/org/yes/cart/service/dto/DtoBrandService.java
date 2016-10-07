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

import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoBrandService extends GenericDTOService<BrandDTO>, GenericAttrValueService {

    /**
     * Find brands by name.
     *
     * @param name brand name for partial match.
     *
     * @return list of brands
     */
    List<BrandDTO> findBrands(String name) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Find brands by filter.
     *
     * @param filter filter for partial match.
     * @param page page number starting from 0
     * @param pageSize size of page
     *
     * @return list of brands
     */
    List<BrandDTO> findBy(String filter, int page, int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
