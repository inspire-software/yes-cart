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
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemotePromotionService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 7:23 PM
 */
public class RemotePromotionServiceImpl extends AbstractRemoteService<PromotionDTO>
        implements RemotePromotionService {

    private final DtoPromotionService dtoPromotionService;
    private final FederationFacade federationFacade;

    public RemotePromotionServiceImpl(final GenericDTOService<PromotionDTO> promotionDTOGenericDTOService,
                                      final FederationFacade federationFacade) {
        super(promotionDTOGenericDTOService);
        this.federationFacade = federationFacade;
        this.dtoPromotionService = (DtoPromotionService) promotionDTOGenericDTOService;
    }

    /** {@inheritDoc} */
    public List<PromotionDTO> findByParameters(final String code,
                                               final String shopCode,
                                               final String currency,
                                               final String tag,
                                               final String type,
                                               final String action,
                                               final Boolean enabled)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<PromotionDTO> all = new ArrayList<PromotionDTO>(dtoPromotionService.findByParameters(code, shopCode, currency, tag, type, action, enabled));
        federationFacade.applyFederationFilter(all, PromotionDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public List<PromotionDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<PromotionDTO> all = new ArrayList<PromotionDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, PromotionDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public PromotionDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = super.getById(id);
        if (promo == null || federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return promo;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PromotionDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = super.getById(id, converters);
        if (promo == null || federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return promo;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PromotionDTO create(final PromotionDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getShopCode(), ShopDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PromotionDTO update(final PromotionDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
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
