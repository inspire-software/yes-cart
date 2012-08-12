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
import org.yes.cart.domain.dto.CustomerWishListDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerWishListDTOImpl;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerWishListService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerWishListServiceImpl
    extends AbstractDtoServiceImpl<CustomerWishListDTO, CustomerWishListDTOImpl, CustomerWishList>
        implements DtoCustomerWishListService {

    /**
     * construct service.
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerWishListGenericService    {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoCustomerWishListServiceImpl(final DtoFactory dtoFactory,
                                          final GenericService<CustomerWishList> customerWishListGenericService,
                                          final AdaptersRepository adaptersRepository) {
        super(dtoFactory, customerWishListGenericService, adaptersRepository);
    }




    /** {@inheritDoc} */
    public Class<CustomerWishListDTO> getDtoIFace() {
        return CustomerWishListDTO.class;
    }

    /** {@inheritDoc} */
    public Class<CustomerWishListDTOImpl> getDtoImpl() {
        return CustomerWishListDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<CustomerWishList> getEntityIFace() {
        return CustomerWishList.class;
    }

    /** {@inheritDoc} */
    public List<CustomerWishListDTO> getByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getDTOs(((CustomerWishListService)service).getByCustomerId(customerId));
    }
}
