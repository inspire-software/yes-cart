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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionService;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22-Oct-2013
 * Time: 14:12:54
 */
public class DtoPromotionCouponServiceImplTezt extends BaseCoreDBTestCase {

    private DtoPromotionService dtoPromotionService;
    private DtoPromotionCouponService dtoPromotionCouponService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoPromotionService = (DtoPromotionService) ctx().getBean(ServiceSpringKeys.DTO_PROMOTION_SERVICE);
        dtoPromotionCouponService = (DtoPromotionCouponService) ctx().getBean(ServiceSpringKeys.DTO_PROMOTION_COUPON_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testGetCouponsByPromotionId() throws Exception {
        PromotionDTO promotionDTO = getPromotionDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        dtoPromotionCouponService.create(getSingleCouponDTO(promotionDTO));
        dtoPromotionCouponService.create(getMultiCouponDTO(promotionDTO));



        // retrieve specific
        List<PromotionCouponDTO> coupons = dtoPromotionCouponService.getCouponsByPromotionId(promotionDTO.getPromotionId());

        assertNotNull(coupons);
        assertEquals(11, coupons.size());

        for (final PromotionCouponDTO coupon : coupons) {
            dtoPromotionCouponService.remove(coupon.getPromotioncouponId());
        }

        // retrieve non-existent
        coupons = dtoPromotionCouponService.getCouponsByPromotionId(123123L);

        assertNotNull(coupons);
        assertEquals(0, coupons.size());

        dtoPromotionService.remove(promotionDTO.getPromotionId());
    }

    @Test
    public void testCreate() throws Exception {

        PromotionDTO promotionDTO = getPromotionDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        dtoPromotionCouponService.create(getMultiCouponDTO(promotionDTO));

        final List<PromotionCouponDTO> coupons = dtoPromotionCouponService.getCouponsByPromotionId(promotionDTO.getPromotionId());
        assertNotNull(coupons);
        assertEquals(10, coupons.size());

        for (final PromotionCouponDTO coupon : coupons) {
            assertNotNull(coupon.getCode());
            assertEquals(promotionDTO.getPromotionId(), coupon.getPromotionId());
            assertEquals(1, coupon.getUsageLimit());
            assertEquals(0, coupon.getUsageLimitPerCustomer());
            assertEquals(0, coupon.getUsageCount());

            dtoPromotionCouponService.remove(coupon.getPromotioncouponId());
        }

        dtoPromotionService.remove(promotionDTO.getPromotionId());

    }

    @Test
    public void testUpdate() throws Exception {
        PromotionDTO promotionDTO = getPromotionDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        dtoPromotionCouponService.create(getSingleCouponDTO(promotionDTO));

        final List<PromotionCouponDTO> coupons = dtoPromotionCouponService.getCouponsByPromotionId(promotionDTO.getPromotionId());
        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        try {
            dtoPromotionCouponService.update(coupons.get(0));
            fail();
        } catch (UnsupportedOperationException uoe) {
            // Good
        }

        dtoPromotionCouponService.remove(coupons.get(0).getPromotioncouponId());
        dtoPromotionService.remove(promotionDTO.getPromotionId());
    }

    @Test
    public void testRemove() throws Exception {
        PromotionDTO promotionDTO = getPromotionDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        dtoPromotionCouponService.create(getSingleCouponDTO(promotionDTO));

        final List<PromotionCouponDTO> coupons = dtoPromotionCouponService.getCouponsByPromotionId(promotionDTO.getPromotionId());
        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        dtoPromotionCouponService.remove(coupons.get(0).getPromotioncouponId());

        assertTrue(dtoPromotionCouponService.getCouponsByPromotionId(promotionDTO.getPromotionId()).isEmpty());
        dtoPromotionService.remove(promotionDTO.getPromotionId());

    }

    private PromotionCouponDTO getSingleCouponDTO(PromotionDTO promotionDTO) {
        PromotionCouponDTO singleDTO = dtoFactory.getByIface(PromotionCouponDTO.class);
        singleDTO.setCode("DTOCOUPON-001");
        singleDTO.setPromotionId(promotionDTO.getPromotionId());
        singleDTO.setUsageLimit(10);
        singleDTO.setUsageLimitPerCustomer(1);
        return singleDTO;
    }

    private PromotionCouponDTO getMultiCouponDTO(PromotionDTO promotionDTO) {
        PromotionCouponDTO multiDTO = dtoFactory.getByIface(PromotionCouponDTO.class);
        multiDTO.setPromotionId(promotionDTO.getPromotionId());
        multiDTO.setUsageLimit(10);
        return multiDTO;
    }

    private PromotionDTO getPromotionDto() {
        PromotionDTO promotionDTO = dtoFactory.getByIface(PromotionDTO.class);
        promotionDTO.setShopCode("SHOP10");
        promotionDTO.setCurrency("EUR");
        promotionDTO.setCode("DTOTESTCOUPON1");
        promotionDTO.setCouponTriggered(true);
        promotionDTO.setPromoType(Promotion.TYPE_ORDER);
        promotionDTO.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        promotionDTO.setEligibilityCondition("order.amount > 100");
        promotionDTO.setPromoActionContext("10");
        promotionDTO.setCanBeCombined(true);
        promotionDTO.setEnabled(true);
        promotionDTO.setEnabledFrom(new Date());
        promotionDTO.setEnabledTo(new Date(System.currentTimeMillis() + 864000000l)); // +10days

        promotionDTO.setTag("tag1");
        promotionDTO.setName("Test name");
        promotionDTO.setDescription("Test description");

        return promotionDTO;
    }
}
