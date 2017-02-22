/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.hibernate.criterion.Restrictions;
import org.joda.time.DateMidnight;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.vo.VoPromotion;
import org.yes.cart.domain.vo.VoPromotionCoupon;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPromotionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/09/2016
 * Time: 09:30
 */
public class VoPromotionServiceImpl implements VoPromotionService {

    private final DtoPromotionService dtoPromotionService;
    private final DtoPromotionCouponService dtoPromotionCouponService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoPromotionServiceImpl(final DtoPromotionService dtoPromotionService,
                                  final DtoPromotionCouponService dtoPromotionCouponService,
                                  final FederationFacade federationFacade,
                                  final VoAssemblySupport voAssemblySupport) {
        this.dtoPromotionService = dtoPromotionService;
        this.dtoPromotionCouponService = dtoPromotionCouponService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /**
     * {@inheritDoc}
     */
    public List<VoPromotion> getFilteredPromotion(final String shopCode, final String currency, final String filter, final int max) throws Exception {

        final List<VoPromotion> list = new ArrayList<>();

        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {

            final List<PromotionDTO> dtos = dtoPromotionService.findBy(shopCode, currency, filter, 0, max);
            return voAssemblySupport.assembleVos(VoPromotion.class, PromotionDTO.class, dtos);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<VoPromotion> getFilteredPromotion(final String shopCode, final String currency, final String filter, final List<String> types, final List<String> actions, final int max) throws Exception {

        final List<VoPromotion> list = new ArrayList<>();

        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {

            final List<PromotionDTO> dtos = dtoPromotionService.findBy(shopCode, currency, filter, types, actions, 0, max);
            return voAssemblySupport.assembleVos(VoPromotion.class, PromotionDTO.class, dtos);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public VoPromotion getPromotionById(final long id) throws Exception {
        final PromotionDTO dto = dtoPromotionService.getById(id);
        if (federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            return voAssemblySupport.assembleVo(VoPromotion.class, PromotionDTO.class, new VoPromotion(), dto);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoPromotion updatePromotion(final VoPromotion vo) throws Exception {
        final PromotionDTO dto = dtoPromotionService.getById(vo.getPromotionId());
        if (dto != null && !dto.isEnabled() && federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            dtoPromotionService.update(
                    voAssemblySupport.assembleDto(PromotionDTO.class, VoPromotion.class, dto, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getPromotionById(vo.getPromotionId());
    }

    /**
     * {@inheritDoc}
     */
    public VoPromotion createPromotion(final VoPromotion vo) throws Exception {
        if (federationFacade.isManageable(vo.getShopCode(), ShopDTO.class)) {
            PromotionDTO dto = dtoPromotionService.getNew();
            dto = dtoPromotionService.create(
                    voAssemblySupport.assembleDto(PromotionDTO.class, VoPromotion.class, dto, vo)
            );
            return getPromotionById(dto.getPromotionId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removePromotion(final long id) throws Exception {

        getPromotionById(id); // check access
        dtoPromotionService.remove(id);

    }

    /**
     * {@inheritDoc}
     */
    public void updateDisabledFlag(final long id, final boolean disabled) throws Exception {

        final PromotionDTO dto = dtoPromotionService.getById(id);
        if (dto != null && federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            dto.setEnabled(!disabled);
            dtoPromotionService.update(dto);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /**
     * {@inheritDoc}
     */
    public List<VoPromotionCoupon> getFilteredPromotionCoupons(final long promotionId, final String filter, final int max) throws Exception {

        getPromotionById(promotionId); // check access

        final List<PromotionCouponDTO> dtos = dtoPromotionCouponService.findBy(promotionId, filter, 0, max);
        return voAssemblySupport.assembleVos(VoPromotionCoupon.class, PromotionCouponDTO.class, dtos);

    }

    /**
     * {@inheritDoc}
     */
    public List<VoPromotionCoupon> createPromotionCoupons(final VoPromotionCoupon vo) throws Exception {

        getPromotionById(vo.getPromotionId()); // check access

        final Date now = new Date();

        dtoPromotionCouponService.create(
                voAssemblySupport.assembleDto(PromotionCouponDTO.class, VoPromotionCoupon.class, dtoPromotionCouponService.getNew(), vo)
        );


        final List<PromotionCouponDTO> dtos = dtoPromotionCouponService.findBy(vo.getPromotionId(), now);
        return voAssemblySupport.assembleVos(VoPromotionCoupon.class, PromotionCouponDTO.class, dtos);

    }

    /**
     * {@inheritDoc}
     */
    public void removePromotionCoupon(final long id) throws Exception {

        final PromotionCouponDTO couponDTO = dtoPromotionCouponService.getById(id);
        if (couponDTO != null) {
            getPromotionById(couponDTO.getPromotionId()); // check access
            dtoPromotionCouponService.remove(id);
        }

    }
}
