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

package org.yes.cart.bulkimport.csv.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.utils.DateUtils;

import static org.junit.Assert.*;

/**
 */
public class CsvDateValueAdapterImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void fromRaw() throws Exception {

        final CsvImpExColumn ldColumn = this.mockery.mock(CsvImpExColumn.class, "ldColumn");
        final CsvImpExColumn ldtColumn = this.mockery.mock(CsvImpExColumn.class, "ldtColumn");
        final CsvImpExTuple tuple = this.mockery.mock(CsvImpExTuple.class, "tuple");

        final CsvDateValueAdapterImpl adapter = new CsvDateValueAdapterImpl();

        this.mockery.checking(new Expectations() {{
            oneOf(ldColumn).getContext(); will(returnValue("yyyy-MM-dd"));
            oneOf(ldtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
            oneOf(ldColumn).getContext(); will(returnValue("yyyy-MM-dd"));
            oneOf(ldtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
        }});

        assertEquals(
                DateUtils.ldParseSDT("2017-06-06"),
                adapter.fromRaw("2017-06-06", "DATE", ldColumn, tuple)
        );
        assertEquals(
                DateUtils.ldParseSDT("2017-06-06"),
                adapter.fromRaw("2017-06-06 15:30:17", "DATE", ldtColumn, tuple)
        );

        assertEquals(
                DateUtils.ldtParseSDT("2017-06-06"),
                adapter.fromRaw("2017-06-06", "DATETIME", ldColumn, tuple)
        );
        assertEquals(
                DateUtils.ldtParseSDT("2017-06-06 15:30:17"),
                adapter.fromRaw("2017-06-06 15:30:17", "DATETIME", ldtColumn, tuple)
        );

        this.mockery.assertIsSatisfied();

    }

}