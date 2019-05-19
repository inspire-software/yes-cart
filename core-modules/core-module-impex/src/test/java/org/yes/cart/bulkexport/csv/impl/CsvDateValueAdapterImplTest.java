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

package org.yes.cart.bulkexport.csv.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.utils.DateUtils;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 01/02/2018
 * Time: 22:36
 */
public class CsvDateValueAdapterImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void fromRaw() throws Exception {

        final CsvImpExColumn ldColumn = this.mockery.mock(CsvImpExColumn.class, "ldColumn");
        final CsvImpExColumn ldtColumn = this.mockery.mock(CsvImpExColumn.class, "ldtColumn");
        final CsvImpExColumn zdtColumn = this.mockery.mock(CsvImpExColumn.class, "zdtColumn");
        final CsvImpExColumn iColumn = this.mockery.mock(CsvImpExColumn.class, "iColumn");
        final CsvImpExTuple tuple = this.mockery.mock(CsvImpExTuple.class, "tuple");

        final CsvDateValueAdapterImpl adapter = new CsvDateValueAdapterImpl();

        this.mockery.checking(new Expectations() {{
            oneOf(ldColumn).getContext(); will(returnValue("yyyy-MM-dd"));
            oneOf(ldtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
            oneOf(zdtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
            oneOf(iColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
        }});

        assertEquals(
                "2017-06-06",
                adapter.fromRaw(DateUtils.ldParseSDT("2017-06-06"), "DATE", ldColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.ldtParseSDT("2017-06-06 15:30:17"), "DATE", ldtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.zdtParseSDT("2017-06-06 15:30:17"), "DATE", zdtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.iParseSDT("2017-06-06 15:30:17"), "DATE", iColumn, tuple)
        );

        this.mockery.assertIsSatisfied();

    }

}