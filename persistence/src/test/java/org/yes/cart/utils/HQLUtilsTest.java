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

package org.yes.cart.utils;

import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 03/10/2017
 * Time: 15:05
 */
public class HQLUtilsTest {

    @Test
    public void testCriteriaIlikeAnywhere() throws Exception {

        assertNull(HQLUtils.criteriaIlikeAnywhere(null));
        assertNull(HQLUtils.criteriaIlikeAnywhere("  "));
        assertEquals("%abc%", HQLUtils.criteriaIlikeAnywhere("aBc"));
        assertEquals("% abc %", HQLUtils.criteriaIlikeAnywhere(" aBc "));

    }

    @Test
    public void testCriteriaLikeAnywhere() throws Exception {

        assertNull(HQLUtils.criteriaLikeAnywhere(null));
        assertNull(HQLUtils.criteriaLikeAnywhere("  "));
        assertEquals("%aBc%", HQLUtils.criteriaLikeAnywhere("aBc"));
        assertEquals("% aBc %", HQLUtils.criteriaLikeAnywhere(" aBc "));

    }

    @Test
    public void testCriteriaIeq() throws Exception {

        assertNull(HQLUtils.criteriaIeq(null));
        assertNull(HQLUtils.criteriaIeq("  "));
        assertEquals("abc", HQLUtils.criteriaIeq("aBc"));
        assertEquals(" abc ", HQLUtils.criteriaIeq(" aBc "));

    }

    @Test
    public void testCriteriaEq() throws Exception {

        assertNull(HQLUtils.criteriaEq(null));
        assertNull(HQLUtils.criteriaEq("  "));
        assertEquals("aBc", HQLUtils.criteriaEq("aBc"));
        assertEquals(" aBc ", HQLUtils.criteriaEq(" aBc "));

    }

    @Test
    public void testCriteriaInTest() throws Exception {

        Object test = HQLUtils.criteriaInTest(null);
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(0), ((TypedParameterValue) test).getValue());

        test = HQLUtils.criteriaInTest(Collections.emptyList());
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(0), ((TypedParameterValue) test).getValue());

        test = HQLUtils.criteriaInTest(Collections.singletonList("aBc"));
        assertTrue(test instanceof TypedParameterValue);
        assertTrue(((TypedParameterValue) test).getType() instanceof IntegerType);
        assertEquals(Integer.valueOf(1), ((TypedParameterValue) test).getValue());

    }

    @Test
    public void testCriteriaIn() throws Exception {

        assertNotNull(HQLUtils.criteriaIn(null));
        assertArrayEquals(new String[] { "x" }, HQLUtils.criteriaIn(null).toArray());
        assertNotNull(HQLUtils.criteriaIn(Collections.emptyList()));
        assertArrayEquals(new String[] { "x" }, HQLUtils.criteriaIn(Collections.emptyList()).toArray());
        final Collection coll = Collections.singletonList("aBc");
        assertSame(coll, HQLUtils.criteriaIn(coll));

    }

}