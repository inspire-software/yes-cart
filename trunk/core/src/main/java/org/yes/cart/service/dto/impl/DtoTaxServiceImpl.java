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
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxDTOImpl;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.service.dto.DtoTaxService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 10:55
 */
public class DtoTaxServiceImpl
    extends AbstractDtoServiceImpl<TaxDTO, TaxDTOImpl, Tax>
        implements DtoTaxService {

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoTaxServiceImpl(final DtoFactory dtoFactory,
                             final GenericService<Tax> taxGenericService,
                             final AdaptersRepository adaptersRepository) {
        super(dtoFactory, taxGenericService, adaptersRepository);
    }

    /** {@inheritDoc} */
    public List<TaxDTO> findByParameters(final String code,
                                         final String shopCode,
                                         final String currency)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Tax> taxes = ((TaxService) service).findByParameters(code, shopCode, currency);
        final List<TaxDTO> dtos = new ArrayList<TaxDTO>();
        fillDTOs(taxes, dtos);
        return dtos;
    }


    /** {@inheritDoc} */
    public Class<TaxDTO> getDtoIFace() {
        return TaxDTO.class;
    }

    /** {@inheritDoc} */
    public Class<TaxDTOImpl> getDtoImpl() {
        return TaxDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Tax> getEntityIFace() {
        return Tax.class;
    }


}
