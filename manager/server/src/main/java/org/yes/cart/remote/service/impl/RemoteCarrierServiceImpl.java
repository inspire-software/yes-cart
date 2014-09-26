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
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCarrierService;
import org.yes.cart.service.dto.DtoCarrierService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCarrierServiceImpl
        extends AbstractRemoteService<CarrierDTO>
        implements RemoteCarrierService {

    private final FederationFacade federationFacade;

    /**
     * Construct remote service.
     *
     * @param carrierDTOGenericDTOService carrier sla dto service to use
     * @param federationFacade federation facade
     */
    public RemoteCarrierServiceImpl(final GenericDTOService<CarrierDTO> carrierDTOGenericDTOService,
                                    final FederationFacade federationFacade) {
        super(carrierDTOGenericDTOService);
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO createForShop(final CarrierDTO carrier, final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {

            final CarrierDTO created = super.create(carrier);
            ((DtoCarrierService) getGenericDTOService()).assignToShop(created.getCarrierId(), shopId);

            return created;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<CarrierDTO> findAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CarrierDTO> all = new ArrayList<CarrierDTO>(((DtoCarrierService) getGenericDTOService()).findAllByShopId(shopId));
        federationFacade.applyFederationFilter(all, CarrierDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAssignedCarrierShops(final long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ShopDTO> assigned = new ArrayList<ShopDTO>(((DtoCarrierService) getGenericDTOService()).getAssignedCarrierShops(carrierId));
        federationFacade.applyFederationFilter(assigned, ShopDTO.class);
        return assigned;
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAvailableCarrierShops(final long carrierId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ShopDTO> available = new ArrayList<ShopDTO>(((DtoCarrierService) getGenericDTOService()).getAvailableCarrierShops(carrierId));
        federationFacade.applyFederationFilter(available, ShopDTO.class);
        return available;
    }

    /**
     * {@inheritDoc}
     */
    public void assignToShop(final long carrierId, final long shopId) {
        if (federationFacade.isManageable(carrierId, CarrierDTO.class)
                && federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoCarrierService) getGenericDTOService()).assignToShop(carrierId, shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unassignFromShop(final long carrierId, final long shopId) {
        if (federationFacade.isManageable(carrierId, CarrierDTO.class)
                && federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoCarrierService) getGenericDTOService()).unassignFromShop(carrierId, shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<CarrierDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CarrierDTO> all = new ArrayList<CarrierDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, CarrierDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CarrierDTO.class)) {
            return super.getById(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CarrierDTO.class)) {
            return super.getById(id, converters);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CarrierDTO update(final CarrierDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
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
        if (federationFacade.isManageable(id, CarrierDTO.class)) {
            super.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

}
