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
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProductAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductAssociationServiceImpl
        extends AbstractRemoteService<ProductAssociationDTO>
        implements RemoteProductAssociationService {

    private final DtoProductAssociationService dtoProductAssociationService;
    private final FederationFacade federationFacade;

    /**
     * Construct service.
     *
     * @param productAssociationDTOGenericDTOService
     *         dto service to use.
     * @param federationFacade federation facade
     */
    public RemoteProductAssociationServiceImpl(final GenericDTOService<ProductAssociationDTO> productAssociationDTOGenericDTOService,
                                               final FederationFacade federationFacade) {
        super(productAssociationDTOGenericDTOService);
        this.federationFacade = federationFacade;
        this.dtoProductAssociationService = (DtoProductAssociationService) productAssociationDTOGenericDTOService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ProductAssociationDTO assoc = super.getById(id);
        if (assoc != null) {
            if (federationFacade.isManageable(assoc.getProductId(), ProductDTO.class)) {
                return assoc;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ProductAssociationDTO assoc = super.getById(id, converters);
        if (assoc != null) {
            if (federationFacade.isManageable(assoc.getProductId(), ProductDTO.class)) {
                return assoc;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO create(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getProductId(), ProductDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO update(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getProductId(), ProductDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(id); // check access
        super.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductAssociationDTO> getProductAssociations(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(productId, ProductDTO.class)) {
            return dtoProductAssociationService.getProductAssociations(productId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductAssociationDTO> getProductAssociationsByProductAssociationType(
            final long productId,
            final String associationCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(productId, ProductDTO.class)) {
            return dtoProductAssociationService.getProductAssociationsByProductAssociationType(productId, associationCode);
        }
        return Collections.emptyList();
    }
}
