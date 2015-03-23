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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 10:54
 */
public interface DtoTaxConfigService extends GenericDTOService<TaxConfigDTO> {

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
    List<TaxConfigDTO> findByTaxId(long taxId,
                                   String countryCode,
                                   String stateCode,
                                   String productCode)

            throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
