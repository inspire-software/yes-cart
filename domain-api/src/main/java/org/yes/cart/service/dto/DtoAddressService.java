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

import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAddressService extends GenericDTOService<AddressDTO> {

    /**
     * Get customers addresses
     *
     * @param customerId customer id
     * @return list of addresses
     */
    List<AddressDTO> getAddressesByCustomerId(long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get customers addresses
     *
     * @param customerId customer id
     * @param formattingShopId shop to which this customer relates
     * @param lang language
     * @return list of addresses
     */
    List<Pair<Long, String>> getFormattedAddressesByCustomerId(long customerId, long formattingShopId, String lang)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get address form.
     * @param customerId customer id
     * @param formattingShopId shop to which this customer relates
     * @param addressType address type
     * @return form fields (same as on SF)
     */
    List<AttrValueCustomerDTO> getAddressForm(long customerId, long formattingShopId, String addressType)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;
}
