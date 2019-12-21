/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoPromotionService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/09/2019
 * Time: 18:38
 */
public class VoPromotionServiceImplTest extends BaseCoreDBTestCase {

    private VoPromotionService voPromotionService;

    @Before
    public void setUp() {
        voPromotionService = (VoPromotionService) ctx().getBean("voPromotionService");
        super.setUp();
    }

    @Test
    public void testGetPromotions() throws Exception {


        VoSearchContext ctxAll = new VoSearchContext();
        ctxAll.setSize(10);

        List<VoPromotion> promoNoFilter = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxAll).getItems();
        assertNotNull(promoNoFilter);
        assertFalse(promoNoFilter.isEmpty());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(Collections.singletonMap("filter", Collections.singletonList("Promo 002")));
        ctxFind.setSize(10);
        List<VoPromotion> promoFind = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxFind).getItems();
        assertNotNull(promoFind);
        assertFalse(promoFind.isEmpty());

        final VoPromotion promo2 = promoFind.get(0);
        assertEquals("PROMO002", promo2.getCode());

        VoSearchContext ctxByCode = new VoSearchContext();
        ctxByCode.setParameters(Collections.singletonMap("filter", Collections.singletonList("#PROMO002")));
        ctxByCode.setSize(10);
        List<VoPromotion> ptExact = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxByCode).getItems();
        assertNotNull(ptExact);
        assertFalse(ptExact.isEmpty());
        assertEquals("PROMO002", ptExact.get(0).getCode());

        VoSearchContext ctxByConditions = new VoSearchContext();
        ctxByConditions.setParameters(Collections.singletonMap("filter", Collections.singletonList("?true")));
        ctxByConditions.setSize(10);
        List<VoPromotion> promoByConditionOrContext1 = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxByConditions).getItems();
        assertNotNull(promoByConditionOrContext1);
        assertFalse(promoByConditionOrContext1.isEmpty());

        VoSearchContext ctxByConditionsEnabled = new VoSearchContext();
        ctxByConditionsEnabled.setParameters(Collections.singletonMap("filter", Collections.singletonList("+?true")));
        ctxByConditionsEnabled.setSize(10);
        List<VoPromotion> promoEnabled = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxByConditionsEnabled).getItems();
        assertNotNull(promoEnabled);
        assertTrue(promoEnabled.isEmpty());

        VoSearchContext ctxByConditionsDisabled = new VoSearchContext();
        ctxByConditionsDisabled.setParameters(Collections.singletonMap("filter", Collections.singletonList("-?true")));
        ctxByConditionsDisabled.setSize(10);
        List<VoPromotion> promoDisabled = voPromotionService.getFilteredPromotion("SHOIP1", "EUR", ctxByConditionsDisabled).getItems();
        assertNotNull(promoDisabled);
        assertFalse(promoDisabled.isEmpty());

    }

    @Test
    public void testProductTypeCRUD() throws Exception {

        final VoPromotion promotion = new VoPromotion();
        promotion.setCode("TESTCRUD");
        promotion.setName("TEST CRUD");
        promotion.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        promotion.setCouponTriggered(true);
        promotion.setShopCode("SHOIP1");
        promotion.setCurrency("EUR");
        promotion.setEligibilityCondition("shoppingCartItemTotal.priceSubTotal > 200.00");
        promotion.setPromoType(Promotion.TYPE_ORDER);
        promotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        promotion.setPromoActionContext("10");
        promotion.setEnabled(false);

        final VoPromotion created = voPromotionService.createPromotion(promotion);
        assertTrue(created.getPromotionId() > 0L);
        assertFalse(created.isEnabled());

        final VoPromotion afterCreated = voPromotionService.getPromotionById(created.getPromotionId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        afterCreated.setName("TEST CRUD UPDATE");

        final VoPromotion updated = voPromotionService.updatePromotion(afterCreated);
        assertEquals("TEST CRUD UPDATE", updated.getName());
        assertFalse(updated.isEnabled());

        VoSearchContext ctxByCode = new VoSearchContext();
        ctxByCode.setParameters(Collections.singletonMap("filter", Collections.singletonList("#TESTCRUD")));
        ctxByCode.setSize(10);

        assertFalse(voPromotionService.getFilteredPromotion("SHOIP1", "EUR",ctxByCode).getItems().isEmpty());

        voPromotionService.updateDisabledFlag(updated.getPromotionId(), false);
        assertTrue(voPromotionService.getPromotionById(updated.getPromotionId()).isEnabled());

        voPromotionService.updateDisabledFlag(updated.getPromotionId(), true);
        assertFalse(voPromotionService.getPromotionById(updated.getPromotionId()).isEnabled());

        VoSearchContext ctxAnyCoupon = new VoSearchContext();
        ctxAnyCoupon.setSize(10);

        final List<VoPromotionCoupon> coupons = voPromotionService.getFilteredPromotionCoupons(updated.getPromotionId(), ctxAnyCoupon).getItems();
        assertNotNull(coupons);
        assertTrue(coupons.isEmpty());

        final VoPromotionCoupon couponsCreate = new VoPromotionCoupon();
        couponsCreate.setPromotionId(updated.getPromotionId());
        couponsCreate.setUsageCount(1);
        couponsCreate.setUsageLimit(5);
        couponsCreate.setUsageLimitPerCustomer(1);

        voPromotionService.createPromotionCoupons(couponsCreate);
        final List<VoPromotionCoupon> afterCouponsCreated = voPromotionService.getFilteredPromotionCoupons(updated.getPromotionId(), ctxAnyCoupon).getItems();
        assertNotNull(afterCouponsCreated);
        assertEquals(5, afterCouponsCreated.size());

        voPromotionService.removePromotionCoupon(afterCouponsCreated.get(0).getPromotioncouponId());

        final List<VoPromotionCoupon> afterCouponRemoved = voPromotionService.getFilteredPromotionCoupons(updated.getPromotionId(), ctxAnyCoupon).getItems();
        assertNotNull(afterCouponRemoved);
        assertEquals(4, afterCouponRemoved.size());

        VoSearchContext ctxCouponByCode = new VoSearchContext();
        ctxCouponByCode.setParameters(Collections.singletonMap("filter", Collections.singletonList(afterCouponRemoved.get(0).getCode())));
        ctxCouponByCode.setSize(10);

        final List<VoPromotionCoupon> couponsFind = voPromotionService.getFilteredPromotionCoupons(updated.getPromotionId(), ctxCouponByCode).getItems();
        assertNotNull(couponsFind);
        assertEquals(1, couponsFind.size());

        voPromotionService.removePromotion(updated.getPromotionId());

        assertTrue(voPromotionService.getFilteredPromotion("SHOIP1", "EUR",ctxCouponByCode).getItems().isEmpty());

    }

    @Test
    public void testTestPromotions() throws Exception {

        voPromotionService.updateDisabledFlag(1L, false);
        voPromotionService.updateDisabledFlag(2L, false);

        final VoPromotionTest test = new VoPromotionTest();
        test.setSku("CC_TEST1=10");
        test.setSupplier("WAREHOUSE_1");
        test.setCustomer("reg@test.com");
        test.setLanguage("en");
        test.setShipping("4_CARRIERSLA");

        final VoCart cart1 = voPromotionService.testPromotions("SHOIP1", "EUR", test);
        assertNotNull(cart1);
        final List<VoCartItem> items1 = cart1.getCartItems();
        assertFalse(items1.isEmpty());
        assertTrue(items1.get(0).isPromoApplied());
        assertEquals("PROMO002", items1.get(0).getAppliedPromo());

        test.setCoupons("PROMO001-002");

        final VoCart cart2 = voPromotionService.testPromotions("SHOIP1", "EUR", test);
        assertNotNull(cart2);
        final List<VoCartItem> items2 = cart2.getCartItems();
        assertFalse(items2.isEmpty());
        assertTrue(items2.get(0).isPromoApplied());
        assertEquals("PROMO001:PROMO001-002,PROMO002", items2.get(0).getAppliedPromo());

        voPromotionService.updateDisabledFlag(1L, true);

        final VoCart cart3 = voPromotionService.testPromotions("SHOIP1", "EUR", test);
        assertNotNull(cart3);
        final List<VoCartItem> items3 = cart3.getCartItems();
        assertFalse(items3.isEmpty());
        assertTrue(items3.get(0).isPromoApplied());
        assertEquals("PROMO002", items3.get(0).getAppliedPromo());

        voPromotionService.updateDisabledFlag(2L, true);

        final VoCart cart4 = voPromotionService.testPromotions("SHOIP1", "EUR", test);
        assertNotNull(cart4);
        final List<VoCartItem> items4 = cart4.getCartItems();
        assertFalse(items4.isEmpty());
        assertFalse(items4.get(0).isPromoApplied());
        assertNull(items4.get(0).getAppliedPromo());

    }
}