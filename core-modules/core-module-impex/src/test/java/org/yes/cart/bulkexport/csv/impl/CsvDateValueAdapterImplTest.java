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
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.util.DateUtils;

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

        final ImpExColumn ldColumn = this.mockery.mock(ImpExColumn.class, "ldColumn");
        final ImpExColumn ldtColumn = this.mockery.mock(ImpExColumn.class, "ldtColumn");
        final ImpExColumn zdtColumn = this.mockery.mock(ImpExColumn.class, "zdtColumn");
        final ImpExColumn iColumn = this.mockery.mock(ImpExColumn.class, "iColumn");
        final ImpExTuple tuple = this.mockery.mock(ImpExTuple.class, "tuple");

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
                adapter.fromRaw(DateUtils.ldtParseSDT("2017-06-06 15:30:17"), "DATETIME", ldtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.zdtParseSDT("2017-06-06 15:30:17"), "ZONEDTIME", zdtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.zdtParseSDT("2017-06-06 15:30:17"), "INSTANT", iColumn, tuple)
        );

        this.mockery.assertIsSatisfied();

    }

}