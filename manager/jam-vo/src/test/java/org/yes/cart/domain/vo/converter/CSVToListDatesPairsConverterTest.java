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
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.MutablePair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/02/2017
 * Time: 10:08
 */
public class CSVToListDatesPairsConverterTest {

    @Test
    public void testConvert() throws Exception {

        final SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_FORMAT);
        List<MutablePair<Date, Date>> csv;

        csv = (List<MutablePair<Date, Date>>) new CSVToListDatesPairsConverter().convertToDto(null, null);
        assertNotNull(csv);
        assertTrue(csv.isEmpty());
        assertNull(new CSVToListConverter().convertToEntity(csv, null, null));

        csv = (List<MutablePair<Date, Date>>) new CSVToListDatesPairsConverter().convertToDto("2017-01-01,2017-01-15:2017-01-17,,2017-01-25", null);
        assertNotNull(csv);
        assertEquals(3, csv.size());
        MutablePair<Date, Date> range;
        range = csv.get(0);
        assertNotNull(range);
        assertEquals(format.parse("2017-01-01"), range.getFirst());
        assertNull(range.getSecond());
        range = csv.get(1);
        assertNotNull(range);
        assertEquals(format.parse("2017-01-15"), range.getFirst());
        assertEquals(format.parse("2017-01-17"), range.getSecond());
        range = csv.get(2);
        assertNotNull(range);
        assertEquals(format.parse("2017-01-25"), range.getFirst());
        assertNull(range.getSecond());

        assertEquals("2017-01-01,2017-01-15:2017-01-17,2017-01-25", new CSVToListDatesPairsConverter().convertToEntity(csv, null, null));


    }

}