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
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemotePromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.dto.GenericDTOService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 7:23 PM
 */
public class RemotePromotionCouponServiceImpl extends AbstractRemoteService<PromotionCouponDTO>
        implements RemotePromotionCouponService {

    private final DtoPromotionCouponService dtoPromotionCouponService;
    private final DtoPromotionService dtoPromotionService;
    private final FederationFacade federationFacade;


    public RemotePromotionCouponServiceImpl(final GenericDTOService<PromotionCouponDTO> promotionCouponDTOGenericDTOService,
                                            final DtoPromotionService dtoPromotionService,
                                            final FederationFacade federationFacade) {
        super(promotionCouponDTOGenericDTOService);
        this.dtoPromotionService = dtoPromotionService;
        this.federationFacade = federationFacade;
        this.dtoPromotionCouponService = (DtoPromotionCouponService) promotionCouponDTOGenericDTOService;
    }

    /** {@inheritDoc} */
    public List<PromotionCouponDTO> getCouponsByPromotionId(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = dtoPromotionService.getById(promotionId);
        if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return dtoPromotionCouponService.getCouponsByPromotionId(promotionId);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /** {@inheritDoc} */
    public byte[] getCouponsByPromotionIdExport(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = dtoPromotionService.getById(promotionId);
        if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return dtoPromotionCouponService.getCouponsByPromotionIdExport(promotionId);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /** {@inheritDoc} */
    public List<PromotionCouponDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new AccessDeniedException("ACCESS DENIED");
    }

    /** {@inheritDoc} */
    public PromotionCouponDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionCouponDTO coupon = super.getById(id);
        if (coupon != null) {
            final PromotionDTO promo = dtoPromotionService.getById(coupon.getPromotionId());
            if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
                return coupon;
            } else {
                throw new AccessDeniedException("ACCESS DENIED");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public PromotionCouponDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionCouponDTO coupon = super.getById(id, converters);
        if (coupon != null) {
            final PromotionDTO promo = dtoPromotionService.getById(coupon.getPromotionId());
            if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
                return coupon;
            } else {
                throw new AccessDeniedException("ACCESS DENIED");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public PromotionCouponDTO create(final PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = dtoPromotionService.getById(instance.getPromotionId());
        if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /** {@inheritDoc} */
    public PromotionCouponDTO update(final PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final PromotionDTO promo = dtoPromotionService.getById(instance.getPromotionId());
        if (federationFacade.isManageable(promo.getShopCode(), ShopDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /** {@inheritDoc} */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(id); // check access
        super.remove(id);
    }
}
