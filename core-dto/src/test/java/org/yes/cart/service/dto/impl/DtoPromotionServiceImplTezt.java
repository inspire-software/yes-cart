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

import java.util.*;

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
        final SearchContext all = new SearchContext(Collections.emptyMap(), 0, 10, "code", false, "filter", "types", "actions");
        List<PromotionDTO> promos = dtoPromotionService.findPromotions(shopCode, currency, all).getItems();

        assertNotNull(promos);
        assertEquals(5, promos.size());


        // basic
        final SearchContext basic = new SearchContext(Collections.singletonMap("filter", Collections.singletonList(p1.getCode())), 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, basic).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // basic with types
        final Map<String, List> basicWithTypesParams = new HashMap<>();
        basicWithTypesParams.put("filter", Collections.singletonList(p1.getCode()));
        basicWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        basicWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext basicWithTypes = new SearchContext(basicWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, basicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // all enabled
        final Map<String, List> enabledAllWithTypesParams = new HashMap<>();
        enabledAllWithTypesParams.put("filter", Collections.singletonList("++"));
        enabledAllWithTypesParams.put("types", Arrays.asList(Promotion.TYPE_ORDER, Promotion.TYPE_ITEM));
        final SearchContext enabledAllWithTypes = new SearchContext(enabledAllWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, enabledAllWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(2, promos.size());
        assertTrue(promos.stream().allMatch(PromotionDTO::isEnabled));

        // all disabled
        final Map<String, List> disabledAllWithTypesParams = new HashMap<>();
        disabledAllWithTypesParams.put("filter", Collections.singletonList("--"));
        enabledAllWithTypesParams.put("types", Arrays.asList(Promotion.TYPE_ORDER, Promotion.TYPE_ITEM));
        final SearchContext disabledAllWithTypes = new SearchContext(disabledAllWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, disabledAllWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(3, promos.size());
        assertTrue(promos.stream().noneMatch(PromotionDTO::isEnabled));

        // condition
        final Map<String, List> conditionWithTypesParams = new HashMap<>();
        conditionWithTypesParams.put("filter", Collections.singletonList("?order.amount"));
        conditionWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        conditionWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext conditionWithTypes = new SearchContext(conditionWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, conditionWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(2, promos.size());
        assertTrue(promos.stream().allMatch(promo -> promo.getEligibilityCondition().contains("order.amount")
                && Promotion.TYPE_ORDER.equals(promo.getPromoType())
                && Promotion.ACTION_PERCENT_DISCOUNT.equals(promo.getPromoAction())));

        // enabled condition
        final Map<String, List> enabledWithTypesParams = new HashMap<>();
        enabledWithTypesParams.put("filter", Collections.singletonList("+?order.amount"));
        enabledWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        enabledWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext enabledWithTypes = new SearchContext(enabledWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, enabledWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());
        assertEquals(p1.getCode(), promos.get(0).getCode());

        // disabled condition
        final Map<String, List> disabledWithTypesParams = new HashMap<>();
        disabledWithTypesParams.put("filter", Collections.singletonList("-?order.amount"));
        disabledWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        disabledWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext disabledWithTypes = new SearchContext(disabledWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, disabledWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());
        assertEquals(p2.getCode(), promos.get(0).getCode());


        // code
        final Map<String, List> codeWithTypesParams = new HashMap<>();
        codeWithTypesParams.put("filter", Collections.singletonList("#" + p1.getCode()));
        codeWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        codeWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext codeWithTypes = new SearchContext(codeWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, codeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // enabled code
        final Map<String, List> enabledCodeWithTypesParams = new HashMap<>();
        enabledCodeWithTypesParams.put("filter", Collections.singletonList("+#" + p1.getCode()));
        enabledCodeWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        enabledCodeWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext enabledCodeWithTypes = new SearchContext(enabledCodeWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, enabledCodeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled code
        final Map<String, List> disabledCodeWithTypesParams = new HashMap<>();
        disabledCodeWithTypesParams.put("filter", Collections.singletonList("-#" + p1.getCode()));
        disabledCodeWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        disabledCodeWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext disabledCodeWithTypes = new SearchContext(disabledCodeWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, disabledCodeWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // enabled basic
        final Map<String, List> enabledBasicWithTypesParams = new HashMap<>();
        enabledBasicWithTypesParams.put("filter", Collections.singletonList("+" + p1.getCode()));
        enabledBasicWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        enabledBasicWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext enabledBasicWithTypes = new SearchContext(enabledBasicWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, enabledBasicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(1, promos.size());

        // disabled basic
        final Map<String, List> disabledBasicWithTypesParams = new HashMap<>();
        disabledBasicWithTypesParams.put("filter", Collections.singletonList("-" + p1.getCode()));
        disabledBasicWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        disabledBasicWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext disabledBasicWithTypes = new SearchContext(disabledBasicWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, disabledBasicWithTypes).getItems();

        assertNotNull(promos);
        assertEquals(0, promos.size());

        // time search
        final Map<String, List> timeWithTypesParams = new HashMap<>();
        timeWithTypesParams.put("filter", Collections.singletonList("<2017"));
        timeWithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        timeWithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext timeWithTypes = new SearchContext(timeWithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, timeWithTypes).getItems();

        assertNotNull(promos);
        assertTrue(promos.isEmpty());

        final Map<String, List> time2WithTypesParams = new HashMap<>();
        time2WithTypesParams.put("filter", Collections.singletonList("2017<"));
        time2WithTypesParams.put("types", Collections.singletonList(Promotion.TYPE_ORDER));
        time2WithTypesParams.put("actions", Collections.singletonList(Promotion.ACTION_PERCENT_DISCOUNT));
        final SearchContext time2WithTypes = new SearchContext(time2WithTypesParams, 0, 10, "name", false, "filter", "types", "actions");
        promos = dtoPromotionService.findPromotions(shopCode, currency, time2WithTypes).getItems();

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
