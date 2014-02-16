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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:11 PM
 */
public class PromotionServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testCrud() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);

        List<Promotion> allPromotions, activePromotions;

        allPromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", false);
        assertNotNull(allPromotions);
        assertTrue(allPromotions.isEmpty());
        activePromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", true);
        assertNotNull(activePromotions);
        assertTrue(activePromotions.isEmpty());


        Promotion sku10PercentOff = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        sku10PercentOff.setCode("SKU10%OFF");
        sku10PercentOff.setCanBeCombined(false);
        sku10PercentOff.setCurrency("EUR");
        sku10PercentOff.setShopCode("SHOP10");
        sku10PercentOff.setName("10% off SKU-001, SKU-002");
        sku10PercentOff.setDescription("10% discount to purchases of SKU-001, SKU-002");
        sku10PercentOff.setEligibilityCondition("['SKU-001','SKU-002'].contains(product.code)");
        sku10PercentOff.setPromoType(Promotion.TYPE_ITEM);
        sku10PercentOff.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        sku10PercentOff.setPromoActionContext("10");

        promotionService.create(sku10PercentOff);

        allPromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", false);
        assertNotNull(allPromotions);
        assertEquals(1, allPromotions.size());
        activePromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", true);
        assertNotNull(activePromotions);
        assertTrue(activePromotions.isEmpty());

        sku10PercentOff.setEnabled(true);
        sku10PercentOff = promotionService.update(sku10PercentOff);

        allPromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", false);
        assertNotNull(allPromotions);
        assertEquals(1, allPromotions.size());
        activePromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", true);
        assertNotNull(activePromotions);
        assertEquals(1, activePromotions.size());

        promotionService.delete(sku10PercentOff);

        allPromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", false);
        assertNotNull(allPromotions);
        assertTrue(allPromotions.isEmpty());
        activePromotions = promotionService.getPromotionsByShopCode("SHOP10", "EUR", true);
        assertNotNull(activePromotions);
        assertTrue(activePromotions.isEmpty());

    }


}
