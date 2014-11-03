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
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteTaxConfigService;
import org.yes.cart.service.dto.DtoTaxConfigService;
import org.yes.cart.service.dto.DtoTaxService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/11/2014
 * Time: 10:25
 */
public class RemoteTaxConfigServiceImpl extends AbstractRemoteService<TaxConfigDTO>
        implements RemoteTaxConfigService {

    private final DtoTaxConfigService dtoTaxConfigService;
    private final DtoTaxService dtoTaxService;
    private final FederationFacade federationFacade;

    public RemoteTaxConfigServiceImpl(final GenericDTOService<TaxConfigDTO> taxConfigDTOGenericDTOService,
                                      final DtoTaxService dtoTaxService,
                                      final FederationFacade federationFacade) {
        super(taxConfigDTOGenericDTOService);
        this.dtoTaxService = dtoTaxService;
        this.federationFacade = federationFacade;
        this.dtoTaxConfigService = (DtoTaxConfigService) taxConfigDTOGenericDTOService;
    }

    /** {@inheritDoc} */
    public List<TaxConfigDTO> findByTaxId(final long taxId,
                                          final String countryCode,
                                          final String stateCode,
                                          final String productCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxDTO tax = dtoTaxService.getById(taxId);
        if (federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
            return dtoTaxConfigService.findByTaxId(taxId, countryCode, stateCode, productCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public List<TaxConfigDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new AccessDeniedException("Access is denied");
    }

    /** {@inheritDoc} */
    public TaxConfigDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxConfigDTO cfg = super.getById(id);
        if (cfg != null) {
            final TaxDTO tax = dtoTaxService.getById(cfg.getTaxId());
            if (federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
                return cfg;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public TaxConfigDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxConfigDTO cfg = super.getById(id, converters);
        if (cfg != null) {
            final TaxDTO tax = dtoTaxService.getById(cfg.getTaxId());
            if (federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
                return cfg;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public TaxConfigDTO create(final TaxConfigDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxDTO tax = dtoTaxService.getById(instance.getTaxId());
        if (federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public TaxConfigDTO update(final TaxConfigDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final TaxDTO tax = dtoTaxService.getById(instance.getTaxId());
        if (federationFacade.isManageable(tax.getShopCode(), ShopDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(id); // check access
        super.remove(id);
    }
}
