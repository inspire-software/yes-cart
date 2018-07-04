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

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 04/07/2018
 * Time: 07:50
 */
public class StringToMappingConverterTest {

    @Test
    public void testConvert() throws Exception {

        final StringToMappingConverter converter = new StringToMappingConverter();

        assertEquals("", converter.convertToDto(null, null));
        assertEquals("a=av\n", converter.convertToDto(Collections.singletonMap("a", "av"), null));
        assertEquals("a=av\nb=bv\n", converter.convertToDto(new HashMap() {{
            put("a", "av");
            put("b", "bv");
        }}, null));

        Map vals = (Map) converter.convertToEntity(null, null, null);
        assertNotNull(vals);
        assertTrue(vals.isEmpty());

        vals = (Map) converter.convertToEntity("", null, null);
        assertNotNull(vals);
        assertTrue(vals.isEmpty());

        vals = (Map) converter.convertToEntity("  \n #some comment \n  ", null, null);
        assertNotNull(vals);
        assertTrue(vals.isEmpty());

        vals = (Map) converter.convertToEntity("  \n #some comment \n  a   = av\n b =   bv", null, null);
        assertNotNull(vals);
        assertFalse(vals.isEmpty());
        assertEquals(2, vals.size());
        assertEquals("av", vals.get("a"));
        assertEquals("bv", vals.get("b"));


    }

}