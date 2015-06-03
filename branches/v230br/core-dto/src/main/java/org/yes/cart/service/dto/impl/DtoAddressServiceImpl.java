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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AddressDTOImpl;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAddressService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAddressServiceImpl
    extends AbstractDtoServiceImpl<AddressDTO, AddressDTOImpl, Address>
        implements DtoAddressService {
    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param addressGenericService    {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoAddressServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Address> addressGenericService,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, addressGenericService, adaptersRepository);
    }


    /** {@inheritDoc} */
    public List<AddressDTO> getAddressesByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {        
        return getDTOs(((AddressService)service).getAddressesByCustomerId(customerId));

    }

    /** {@inheritDoc} */
    public Class<AddressDTO> getDtoIFace() {
        return AddressDTO.class;
    }

    /** {@inheritDoc} */
    public Class<AddressDTOImpl> getDtoImpl() {
        return AddressDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Address> getEntityIFace() {
        return Address.class;
    }
}
