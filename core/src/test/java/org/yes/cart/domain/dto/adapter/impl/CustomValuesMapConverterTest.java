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

package org.yes.cart.domain.dto.adapter.impl;

import org.junit.Test;
import org.yes.cart.domain.entity.impl.CustomerOrderDetAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 21/09/2019
 * Time: 11:19
 */
public class CustomValuesMapConverterTest {

    @Test
    public void testConvert() throws Exception {

        final Map<String, String> i18n = Collections.singletonMap("EN", "Some text");

        final CustomerOrderDetAttributesImpl custom = new CustomerOrderDetAttributesImpl();
        custom.putValue("ABC1", "DEF1", new StringI18NModel(i18n));
        custom.putValue("ABC2", "DEF2", null);
        custom.putValue("ABC3", "DEF3", new NonI18NModel("Def3"));


        final CustomValuesMapConverter converter = new CustomValuesMapConverter();

        final Map<String, Pair<String, Map<String, String>>> empty = (Map) converter.convertToDto(null, null);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        final Map<String, Pair<String, Map<String, String>>> map = (Map) converter.convertToDto(custom.getAllValues(), null);
        assertNotNull(map);
        assertFalse(map.isEmpty());

        final Pair<String, Map<String, String>> abc1 = map.get("ABC1");
        assertNotNull(abc1);
        assertEquals("DEF1", abc1.getFirst());
        final Map<String, String> abc1names = abc1.getSecond();
        assertNotNull(abc1names);
        assertEquals("Some text", abc1names.get("EN"));

        final Pair<String, Map<String, String>> abc2 = map.get("ABC2");
        assertNotNull(abc2);
        assertEquals("DEF2", abc2.getFirst());
        final Map<String, String> abc2names = abc2.getSecond();
        assertNotNull(abc2names);
        assertTrue(abc2names.isEmpty());

        final Pair<String, Map<String, String>> abc3 = map.get("ABC3");
        assertNotNull(abc3);
        assertEquals("DEF3", abc3.getFirst());
        final Map<String, String> abc3names = abc3.getSecond();
        assertNotNull(abc3names);
        assertEquals("Def3", abc3names.get("xx"));


        final Map<String, Pair<String, I18NModel>> result = (Map) converter.convertToEntity(map, null, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        final Pair<String, I18NModel> resultAbc1 = result.get("ABC1");
        assertNotNull(resultAbc1);
        assertEquals("DEF1", resultAbc1.getFirst());
        final I18NModel resultAbc1names = resultAbc1.getSecond();
        assertNotNull(resultAbc1names);
        assertEquals("Some text", resultAbc1names.getValue("EN"));

        final Pair<String, I18NModel> resultAbc2 = result.get("ABC2");
        assertNotNull(resultAbc2);
        assertEquals("DEF2", resultAbc2.getFirst());
        final I18NModel resultAbc2names = resultAbc2.getSecond();
        assertNotNull(resultAbc2names);
        assertTrue(resultAbc2names.getAllValues().isEmpty());

        final Pair<String, I18NModel> resultAbc3 = result.get("ABC3");
        assertNotNull(resultAbc3);
        assertEquals("DEF3", resultAbc3.getFirst());
        final I18NModel resultAbc3names = resultAbc3.getSecond();
        assertNotNull(resultAbc3names);
        assertEquals("Def3", resultAbc3names.getValue("xx"));

    }
}