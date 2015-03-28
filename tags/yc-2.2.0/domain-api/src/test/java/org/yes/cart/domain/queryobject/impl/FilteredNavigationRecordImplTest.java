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

package org.yes.cart.domain.queryobject.impl;

import org.junit.Test;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * User: denispavlov
 * Date: 04/12/2013
 * Time: 23:41
 */
public class FilteredNavigationRecordImplTest {

    @Test
    public void testClone() throws Exception {

        final FilteredNavigationRecordImpl original = new FilteredNavigationRecordImpl(
                "name", "displayName", "code", "value", "displayValue", 0, 0, "type");

        final FilteredNavigationRecord clone = original.clone();

        assertNotSame(original, clone);
        assertEquals(original, clone);

        clone.setCount(20);
        clone.setName("new name");
        clone.setCode("new code");

        assertEquals("name", original.getName());
        assertEquals("code", original.getCode());
        assertEquals(0, original.getCount());

        assertEquals("new name", clone.getName());
        assertEquals("new code", clone.getCode());
        assertEquals(20, clone.getCount());


    }
}
