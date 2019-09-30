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
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAttrValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 21/09/2019
 * Time: 11:19
 */
public class CustomValuesListConverterTest {

    @Test
    public void testConvert() throws Exception {

        final Map<String, Pair<String, Map<String, String>>> custom = new LinkedHashMap<>();
        custom.put("ABC1", new Pair<>("DEF1", Collections.singletonMap("EN", "Def 1")));
        custom.put("ABC2", new Pair<>("DEF2", null));
        custom.put("ABC3", new Pair<>("DEF3", Collections.singletonMap("xx", "Def 3")));
        custom.put("ABC4", new Pair<>("DEF4", Collections.singletonMap("xx", "SUPPLIER")));


        final CustomValuesListConverter converter = new CustomValuesListConverter();

        final List<VoAttrValue> empty = (List) converter.convertToDto(null, null);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        final List<VoAttrValue> list = (List) converter.convertToDto(custom, null);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        assertEquals("ABC1", list.get(0).getAttribute().getCode());
        assertEquals("ABC1", list.get(0).getAttribute().getName());
        assertFalse(list.get(0).getAttribute().isSecure());
        assertEquals("DEF1", list.get(0).getVal());
        assertEquals("EN", list.get(0).getDisplayVals().get(0).getFirst());
        assertEquals("Def 1", list.get(0).getDisplayVals().get(0).getSecond());

        assertEquals("ABC2", list.get(1).getAttribute().getCode());
        assertEquals("ABC2", list.get(1).getAttribute().getName());
        assertFalse(list.get(1).getAttribute().isSecure());
        assertEquals("DEF2", list.get(1).getVal());
        assertTrue(list.get(1).getDisplayVals().isEmpty());

        assertEquals("ABC3", list.get(2).getAttribute().getCode());
        assertEquals("ABC3", list.get(2).getAttribute().getName());
        assertFalse(list.get(2).getAttribute().isSecure());
        assertEquals("DEF3", list.get(2).getVal());
        assertEquals("xx", list.get(2).getDisplayVals().get(0).getFirst());
        assertEquals("Def 3", list.get(2).getDisplayVals().get(0).getSecond());

        assertEquals("ABC4", list.get(3).getAttribute().getCode());
        assertEquals("ABC4", list.get(3).getAttribute().getName());
        assertTrue(list.get(3).getAttribute().isSecure());
        assertEquals("DEF4", list.get(3).getVal());
        assertEquals("xx", list.get(3).getDisplayVals().get(0).getFirst());
        assertEquals("SUPPLIER", list.get(3).getDisplayVals().get(0).getSecond());

        final Map<String, Pair<String, Map<String, String>>> result = (Map) converter.convertToEntity(list, null, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        final Pair<String, Map<String, String>> resultAbc1 = result.get("ABC1");
        assertNotNull(resultAbc1);
        assertEquals("DEF1", resultAbc1.getFirst());
        final Map<String, String> resultAbc1names = resultAbc1.getSecond();
        assertNotNull(resultAbc1names);
        assertEquals("Def 1", resultAbc1names.get("EN"));

        final Pair<String, Map<String, String>> resultAbc2 = result.get("ABC2");
        assertNotNull(resultAbc2);
        assertEquals("DEF2", resultAbc2.getFirst());
        final Map<String, String> resultAbc2names = resultAbc2.getSecond();
        assertNotNull(resultAbc2names);
        assertTrue(resultAbc2names.isEmpty());

        final Pair<String, Map<String, String>> resultAbc3 = result.get("ABC3");
        assertNotNull(resultAbc3);
        assertEquals("DEF3", resultAbc3.getFirst());
        final Map<String, String> resultAbc3names = resultAbc3.getSecond();
        assertNotNull(resultAbc3names);
        assertEquals("Def 3", resultAbc3names.get("xx"));


    }
}