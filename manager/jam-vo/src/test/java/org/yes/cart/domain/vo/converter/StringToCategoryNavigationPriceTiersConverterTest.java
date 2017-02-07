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

package org.yes.cart.domain.vo.converter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoCategoryNavigationPriceTier;
import org.yes.cart.domain.vo.VoCategoryNavigationPriceTiers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 30/08/2016
 * Time: 12:23
 */
public class StringToCategoryNavigationPriceTiersConverterTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testConvert() throws Exception {

        final CategoryDTO dto = this.context.mock(CategoryDTO.class, "dto");
        final CategoryDTO dto2 = this.context.mock(CategoryDTO.class, "dto2");

        final String xml = new Scanner(new File("src/test/resources/price_tier_example.xml")).useDelimiter("\\Z").next();

        this.context.checking(new Expectations() {{
            allowing(dto).getNavigationByPriceTiers();
            will(returnValue(xml));
            allowing(dto).setNavigationByPriceTiers(with(any(String.class)));
        }});

        final StringToCategoryNavigationPriceTiersConverter converter = new StringToCategoryNavigationPriceTiersConverter();

        final VoCategoryNavigationPriceTiers rl = (VoCategoryNavigationPriceTiers) converter.convertToDto(dto, null);

        assertNotNull(rl);
        assertEquals(2, rl.getTiers().size());

        final Map<String, Integer> rlMap = new HashMap<>();
        int rli = 0;
        for (final MutablePair<String, List<VoCategoryNavigationPriceTier>> rlItem : rl.getTiers()) {
            rlMap.put(rlItem.getFirst(), rli);
        }

        assertTrue(rlMap.containsKey("EUR"));
        assertTrue(rlMap.containsKey("RUS"));

        final List<VoCategoryNavigationPriceTier> tiers = (rl.getTiers().get(rlMap.get("EUR"))).getSecond();
        assertNotNull(tiers);
        assertEquals(5, tiers.size());
        assertEquals("0", tiers.get(0).getFrom().toPlainString());
        assertEquals("100", tiers.get(0).getTo().toPlainString());
        assertEquals("100", tiers.get(1).getFrom().toPlainString());
        assertEquals("300", tiers.get(1).getTo().toPlainString());

        final String xmlStr = (String) converter.convertToEntity(rl, dto, null);

        assertNotNull(xmlStr);

        this.context.checking(new Expectations() {{
            allowing(dto2).getNavigationByPriceTiers();
            will(returnValue(xmlStr));
        }});

        final VoCategoryNavigationPriceTiers rl2 = (VoCategoryNavigationPriceTiers) converter.convertToDto(dto, null);

        assertNotNull(rl2);
        assertEquals(2, rl2.getTiers().size());

        final Map<String, Integer> rl2Map = new HashMap<>();
        int rl2i = 0;
        for (final MutablePair<String, List<VoCategoryNavigationPriceTier>> rlItem : rl.getTiers()) {
            rl2Map.put(rlItem.getFirst(), rl2i);
        }

        assertTrue(rl2Map.containsKey("EUR"));
        assertTrue(rl2Map.containsKey("RUS"));

        final List<VoCategoryNavigationPriceTier> tiers2 = (rl.getTiers().get(rl2Map.get("EUR"))).getSecond();

        assertNotNull(tiers2);
        assertEquals(5, tiers2.size());
        assertEquals("0", tiers2.get(0).getFrom().toPlainString());
        assertEquals("100", tiers2.get(0).getTo().toPlainString());
        assertEquals("100", tiers2.get(1).getFrom().toPlainString());
        assertEquals("300", tiers2.get(1).getTo().toPlainString());

        this.context.assertIsSatisfied();

    }

}