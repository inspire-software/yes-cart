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
import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.dto.DtoAttributeGroupService;
import org.yes.cart.service.dto.DtoPromotionService;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22-Oct-2013
 * Time: 14:12:54
 */
public class DtoPromotionServiceImplTezt extends BaseCoreDBTestCase {

    private DtoPromotionService dtoPromotionService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoPromotionService = (DtoPromotionService) ctx().getBean(ServiceSpringKeys.DTO_PROMOTION_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testFindByParameters() throws Exception {
        PromotionDTO promotionDTO = getDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        // retrieve specific
        List<PromotionDTO> promos = dtoPromotionService.findByParameters(
                promotionDTO.getCode(),
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                promotionDTO.getTag(),
                promotionDTO.getPromoType(),
                promotionDTO.getPromoAction(),
                promotionDTO.isEnabled());

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // retrieve all
        promos = dtoPromotionService.findByParameters(
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // retrieve non-existent
        promos = dtoPromotionService.findByParameters(
                "zzzz",
                null,
                null,
                null,
                null,
                null,
                null);

        assertNotNull(promos);
        assertEquals(0, promos.size());

        dtoPromotionService.remove(promotionDTO.getPromotionId());
    }

    @Test
    public void testCreate() throws Exception {
        PromotionDTO promotionDTO = getDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);
        assertTrue(promotionDTO.getPromotionId() > 0);
        dtoPromotionService.remove(promotionDTO.getPromotionId());
    }

    @Test
    public void testUpdate() throws Exception {
        PromotionDTO promotionDTO = getDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);
        assertTrue(promotionDTO.getPromotionId() > 0);
        promotionDTO.setName("other name");
        promotionDTO = dtoPromotionService.update(promotionDTO);
        assertEquals("other name", promotionDTO.getName());
        dtoPromotionService.remove(promotionDTO.getPromotionId());
    }

    @Test
    public void testRemove() throws Exception {
        PromotionDTO promotionDTO = getDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);
        assertTrue(promotionDTO.getPromotionId() > 0);
        long id = promotionDTO.getPromotionId();
        dtoPromotionService.remove(id);
        promotionDTO = dtoPromotionService.getById(id);
        assertNull(promotionDTO);
    }

    private PromotionDTO getDto() {
        PromotionDTO promotionDTO = dtoFactory.getByIface(PromotionDTO.class);
        promotionDTO.setShopCode("SHOP10");
        promotionDTO.setCurrency("EUR");
        promotionDTO.setCode("TESTCODE");
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
