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

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/02/2017
 * Time: 10:09
 */
public class CSVToListConverterTest {

    @Test
    public void testConvert() throws Exception {

        List<String> csv;

        csv = (List<String>) new CSVToListConverter().convertToDto(null, null);
        assertNotNull(csv);
        assertTrue(csv.isEmpty());
        assertNull(new CSVToListConverter().convertToEntity(csv, null, null));

        csv = (List<String>) new CSVToListConverter().convertToDto("a,b,,c", null);
        assertNotNull(csv);
        assertEquals(3, csv.size());
        assertEquals("a", csv.get(0));
        assertEquals("b", csv.get(1));
        assertEquals("c", csv.get(2));
        assertEquals("a,b,c", new CSVToListConverter().convertToEntity(csv, null, null));

    }
}