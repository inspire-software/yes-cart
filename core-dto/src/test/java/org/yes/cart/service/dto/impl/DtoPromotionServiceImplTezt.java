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
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.utils.DateUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    public void testFindPromotions() throws Exception {
        PromotionDTO p1 = getDto();
        p1 = dtoPromotionService.create(p1);
        final String shopCode = p1.getShopCode();
        final String currency = p1.getCurrency();
        PromotionDTO p2 = getDto();
        p2.setEnabled(false);
        p2 = dtoPromotionService.create(p2);
        PromotionDTO p3 = getDto();
        p3.setPromoType(Promotion.TYPE_ITEM);
        p3 = dtoPromotionService.create(p3);

        // retrieve all
        final SearchContext all = createSearchContext("code", false, 0, 10,
                "shopCode", shopCode,
                "currency", currency
        );
        List<PromotionDTO> promos = dtoPromotionService.findPromotions(all).getItems();

        assertNotNull(promos);
        assertEquals(5, promos.size());


        // basic
        final SearchContext basic = createSearchContext("code", false, 0, 10,
                "filter", p1.getCode(),
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(basic).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // basic with types
        final SearchContext basicWithTypes = createSearchContext("code", false, 0, 10,
                "filter", p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(basicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // all enabled
        final SearchContext enabledAllWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "++",
                "types", Arrays.asList(Promotion.TYPE_ORDER, Promotion.TYPE_ITEM),
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(enabledAllWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(2, promos.size());
        assertTrue(promos.stream().allMatch(PromotionDTO::isEnabled));

        // all disabled
        final SearchContext disabledAllWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "--",
                "types", Arrays.asList(Promotion.TYPE_ORDER, Promotion.TYPE_ITEM),
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(disabledAllWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(3, promos.size());
        assertTrue(promos.stream().noneMatch(PromotionDTO::isEnabled));

        // condition
        final SearchContext conditionWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "?order.amount",
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(conditionWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(2, promos.size());
        assertTrue(promos.stream().allMatch(promo -> promo.getEligibilityCondition().contains("order.amount")
                && Promotion.TYPE_ORDER.equals(promo.getPromoType())
                && Promotion.ACTION_PERCENT_DISCOUNT.equals(promo.getPromoAction())));

        // enabled condition
        final SearchContext enabledWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "+?order.amount",
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(enabledWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());
        assertEquals(p1.getCode(), promos.get(0).getCode());

        // disabled condition
        final SearchContext disabledWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "-?order.amount",
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(disabledWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());
        assertEquals(p2.getCode(), promos.get(0).getCode());


        // code
        final SearchContext codeWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "#" + p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(codeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // enabled code
        final SearchContext enabledCodeWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "+#" + p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(enabledCodeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled code
        final SearchContext disabledCodeWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "-#" + p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(disabledCodeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // enabled basic
        final SearchContext enabledBasicWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "+" + p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(enabledBasicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled basic
        final SearchContext disabledBasicWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "-" + p1.getCode(),
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(disabledBasicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // time search
        final SearchContext timeWithTypes = createSearchContext("code", false, 0, 10,
                "filter", "<2017",
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(timeWithTypes).getItems();

        assertNotNull(promos);
        assertTrue(promos.isEmpty());

        final SearchContext time2WithTypes = createSearchContext("code", false, 0, 10,
                "filter", "2017<",
                "types", Promotion.TYPE_ORDER,
                "actions", Promotion.ACTION_PERCENT_DISCOUNT,
                "shopCode", shopCode,
                "currency", currency
        );
        promos = dtoPromotionService.findPromotions(time2WithTypes).getItems();

        assertNotNull(promos);
        assertEquals(2, promos.size());

        dtoPromotionService.remove(p1.getPromotionId());
        dtoPromotionService.remove(p2.getPromotionId());
        dtoPromotionService.remove(p3.getPromotionId());
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
        promotionDTO.setShopCode("SHOIP1");
        promotionDTO.setCurrency("EUR");
        promotionDTO.setCode(UUID.randomUUID().toString());
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
