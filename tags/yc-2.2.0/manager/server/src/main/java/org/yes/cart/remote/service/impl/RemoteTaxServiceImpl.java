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

package org.yes.cart.remote.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteTaxService;
import org.yes.cart.service.dto.DtoTaxService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/11/2014
 * Time: 10:09
 */
public class RemoteTaxServiceImpl extends AbstractRemoteService<TaxDTO>
        implements RemoteTaxService {

    private final DtoTaxService dtoTaxService;
    private final FederationFacade federationFacade;

    public RemoteTaxServiceImpl(final GenericDTOService<TaxDTO> taxDTOGenericDTOService,
                                final FederationFacade federationFacade) {
        super(taxDTOGenericDTOService);
        this.federationFacade = federationFacade;
        this.dtoTaxService = (DtoTaxService) taxDTOGenericDTOService;
    }

    /** {@inheritDoc} */
    public List<TaxDTO> findByParameters(final String code,
                                         final String shopCode,
                                         final String currency)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<TaxDTO> all = new ArrayList<TaxDTO>(dtoTaxService.findByParameters(code, shopCode, currency));
        federationFacade.applyFederationFilter(all, TaxDTO.class);
        return all;
    }

    /** {@inheritDoc} */
    public List<TaxDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<TaxDTO> all = new ArrayList<TaxDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, TaxDTO.class);
        return all;
    }

    /** {@inheritDoc} */
    public TaxDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxDTO tax = super.getById(id);
        if (tax == null || federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
            return tax;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public TaxDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxDTO tax = super.getById(id, converters);
        if (tax == null || federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
            return tax;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public TaxDTO create(final TaxDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getShopCode(), ShopDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public TaxDTO update(final TaxDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getShopCode(), ShopDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(id); // checks access
        super.remove(id);
    }
}
