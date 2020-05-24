/*
 * Copyright 2009 Inspire-Software.com
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

import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 19/09/2019
 * Time: 11:42
 */
public class I18NModelConverterTest {

    @Test
    public void testConvert() throws Exception {

        final I18NStringConverter converter = new I18NStringConverter();

        final Map<String, String> empty = (Map<String, String>) converter.convertToDto(null, null);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        final String i18nString = "EN#~#Some text#~#RU#~#Текст#~#xx#~#model failover#~#";
        final Map<String, String> map = (Map<String, String>) converter.convertToDto(i18nString, null);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals("Some text", map.get("EN"));

        final String result = (String) converter.convertToEntity(map, null, null);
        assertNotNull(result);
        assertEquals(i18nString, result);


    }
}