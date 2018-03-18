/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.util.DateUtils;

import java.util.Collections;
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
        dtoPromotionService = (DtoPromotionService) ctx().getBean(DtoServiceSpringKeys.DTO_PROMOTION_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
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
    public void testFindBy() throws Exception {
        PromotionDTO promotionDTO = getDto();
        promotionDTO = dtoPromotionService.create(promotionDTO);

        // retrieve all
        List<PromotionDTO> promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                null,
                null,
                null,
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());


        // basic
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                promotionDTO.getCode(),
                null,
                null,
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());


        // basic with types
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // all enabled
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "++",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // all disabled
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "--",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // condition
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "?order.amount",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // enabled condition
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "+?order.amount",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled condition
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "-?order.amount",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(0, promos.size());


        // code
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "#" + promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // enabled code
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "+#" + promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled code
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "-#" + promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // enabled basic
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "+" + promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled basic
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "-" + promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // time search
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                "<2017",
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertFalse(promos.isEmpty());

        // basic search
        promos = dtoPromotionService.findBy(
                promotionDTO.getShopCode(),
                promotionDTO.getCurrency(),
                promotionDTO.getCode(),
                Collections.singletonList(promotionDTO.getPromoType()),
                Collections.singletonList(promotionDTO.getPromoAction()),
                0,
                10);

        assertNotNull(promos);
        assertEquals(1, promos.size());

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
        promotionDTO.setEnabledFrom(DateUtils.ldtFrom(System.currentTimeMillis() - 1000L));
        promotionDTO.setEnabledTo(DateUtils.ldtFrom(System.currentTimeMillis() + 864000000L)); // +10days

        promotionDTO.setTag("tag1");
        promotionDTO.setName("Test name");
        promotionDTO.setDescription("Test description");

        return promotionDTO;
    }
}
