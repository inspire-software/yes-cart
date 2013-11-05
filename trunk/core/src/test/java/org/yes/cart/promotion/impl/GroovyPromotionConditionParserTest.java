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

package org.yes.cart.promotion.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionCondition;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-10-28
 * Time: 9:46 AM
 */
public class GroovyPromotionConditionParserTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testParseGroovyConditionTrue() throws Exception {

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final Class cl = parser.parseGroovyCondition(1L, "ABC#", "shoppingCart != null");

        assertEquals("PromotionABC_", cl.getSimpleName());

        final PromotionCondition condition = ((PromotionCondition) cl.newInstance());

        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", new Object());
        }});

        assertTrue(result);

    }

    @Test
    public void testParseGroovyConditionFalse() throws Exception {

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final Class cl = parser.parseGroovyCondition(1L, "ABC#", "shoppingCart != null; return false");

        assertEquals("PromotionABC_", cl.getSimpleName());

        final PromotionCondition condition = ((PromotionCondition) cl.newInstance());

        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", new Object());
        }});

        assertFalse(result);

    }


    @Test
    public void testParseCache() throws Exception {

        final Promotion promotion = mockery.mock(Promotion.class, "promotion");

        mockery.checking(new Expectations() {{
            allowing(promotion).getPromotionId(); will(returnValue(1L));
            allowing(promotion).getCode(); will(returnValue("ABC#"));
            allowing(promotion).getEligibilityCondition(); will(returnValue("shoppingCart != null"));
        }});

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final PromotionCondition condition = parser.parse(promotion);

        assertFalse(condition instanceof NullPromotionCondition);
        assertEquals("PromotionABC_", condition.getClass().getSimpleName());
        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(new HashMap<String, Object>() {{
            put("shoppingCart", new Object());
        }});

        assertTrue(result);

    }

    @Test
    public void testBrokenCondition() throws Exception {

        final Promotion promotion = mockery.mock(Promotion.class, "promotion");

        mockery.checking(new Expectations() {{
            allowing(promotion).getPromotionId(); will(returnValue(1L));
            allowing(promotion).getCode(); will(returnValue("ABC#"));
            allowing(promotion).getEligibilityCondition(); will(returnValue("#?/*"));
        }});

        GroovyPromotionConditionParser parser = new GroovyPromotionConditionParser();

        final PromotionCondition condition = parser.parse(promotion);

        assertTrue(condition instanceof NullPromotionCondition);
        assertEquals(1L, condition.getPromotionId());
        assertEquals("ABC#", condition.getPromotionCode());

        final boolean result = condition.isEligible(null);

        assertFalse(result);


    }
}
