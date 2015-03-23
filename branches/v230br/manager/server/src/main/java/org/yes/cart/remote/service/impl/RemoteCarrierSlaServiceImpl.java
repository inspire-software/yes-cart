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
import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCarrierSlaService;
import org.yes.cart.service.dto.DtoCarrierSlaService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCarrierSlaServiceImpl
        extends AbstractRemoteService<CarrierSlaDTO>
        implements RemoteCarrierSlaService {

    private final FederationFacade federationFacade;

    /**
     * Construct remote service.
     *
     * @param carrierSlaDTOGenericDTOService carrier sla dto service to use
     * @param federationFacade federation facade
     */
    public RemoteCarrierSlaServiceImpl(final GenericDTOService<CarrierSlaDTO> carrierSlaDTOGenericDTOService,
                                       final FederationFacade federationFacade) {
        super(carrierSlaDTOGenericDTOService);
        this.federationFacade = federationFacade;
    }


    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> findByCarrier(final long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(carrierId, CarrierDTO.class)) {
            return ((DtoCarrierSlaService) getGenericDTOService()).findByCarrier(carrierId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<CarrierSlaDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new AccessDeniedException("Access is denied");
    }

    /**
     * {@inheritDoc}
     */
    public CarrierSlaDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final CarrierSlaDTO sla = super.getById(id);
        if (sla != null) {
            if (federationFacade.isManageable(sla.getCarrierId(), CarrierDTO.class)) {
                return sla;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public CarrierSlaDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final CarrierSlaDTO sla = super.getById(id, converters);
        if (sla != null) {
            if (federationFacade.isManageable(sla.getCarrierId(), CarrierDTO.class)) {
                return sla;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public CarrierSlaDTO create(final CarrierSlaDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getCarrierId(), CarrierDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CarrierSlaDTO update(final CarrierSlaDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getCarrierId(), CarrierDTO.class)) {
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
}
