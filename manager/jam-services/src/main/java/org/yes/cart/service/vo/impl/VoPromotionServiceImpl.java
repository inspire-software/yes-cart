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

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.vo.VoCart;
import org.yes.cart.domain.vo.VoPromotion;
import org.yes.cart.domain.vo.VoPromotionCoupon;
import org.yes.cart.domain.vo.VoPromotionTest;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPromotionService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void removePromotion(final long id) throws Exception {

        final PromotionDTO dto = dtoPromotionService.getById(id);
        if (dto != null && !dto.isEnabled() && federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            dtoPromotionCouponService.removeAll(id);
            dtoPromotionService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public List<VoPromotionCoupon> getFilteredPromotionCoupons(final long promotionId, final String filter, final int max) throws Exception {

        getPromotionById(promotionId); // check access

        final List<PromotionCouponDTO> dtos = dtoPromotionCouponService.findBy(promotionId, filter, 0, max);
        return voAssemblySupport.assembleVos(VoPromotionCoupon.class, PromotionCouponDTO.class, dtos);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoPromotionCoupon> createPromotionCoupons(final VoPromotionCoupon vo) throws Exception {

        getPromotionById(vo.getPromotionId()); // check access

        final Instant now = TimeContext.getTime();

        dtoPromotionCouponService.create(
                voAssemblySupport.assembleDto(PromotionCouponDTO.class, VoPromotionCoupon.class, dtoPromotionCouponService.getNew(), vo)
        );


        final List<PromotionCouponDTO> dtos = dtoPromotionCouponService.findBy(vo.getPromotionId(), now);
        return voAssemblySupport.assembleVos(VoPromotionCoupon.class, PromotionCouponDTO.class, dtos);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePromotionCoupon(final long id) throws Exception {

        final PromotionCouponDTO couponDTO = dtoPromotionCouponService.getById(id);
        if (couponDTO != null) {
            getPromotionById(couponDTO.getPromotionId()); // check access
            dtoPromotionCouponService.remove(id);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoCart testPromotions(final String shopCode,
                                 final String currency,
                                 final VoPromotionTest testData) {

        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {

            final Map<String, BigDecimal> skuCodes = new HashMap<>();
            if (StringUtils.isNotBlank(testData.getSku())) {
                for (final String code : Arrays.asList(StringUtils.split(testData.getSku(), ','))) {
                    final String[] skuAndQty = StringUtils.split(code.trim(), '=');
                    try {
                        skuCodes.put(skuAndQty[0], skuAndQty.length > 1 ? new BigDecimal(skuAndQty[1]) : BigDecimal.ONE);
                    } catch (Exception exp) {
                        skuCodes.put(skuAndQty[0], BigDecimal.ONE);
                    }
                }
            }

            final List<String> couponCodes = new ArrayList<>();
            if (StringUtils.isNotBlank(testData.getCoupons())) {
                for (final String code : Arrays.asList(StringUtils.split(testData.getCoupons(), ','))) {
                    couponCodes.add(code.trim());
                }
            }

            final ShoppingCart cart = dtoPromotionService.testPromotions(
                    shopCode, currency,
                    testData.getLanguage(),
                    testData.getCustomer(),
                    testData.getSupplier(),
                    skuCodes,
                    testData.getShipping(),
                    couponCodes,
                    testData.getTime()
            );

            return voAssemblySupport.assembleVo(VoCart.class, ShoppingCart.class, new VoCart(), cart);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
