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

package org.yes.cart.bulkimport.csv.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ValueAdapter;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 2:15 PM
 */
public class CsvImportTupleImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetColumnValue() throws Exception {

        final ImportColumn column = mockery.mock(ImportColumn.class, "column");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        mockery.checking(new Expectations() {{
            allowing(column).getColumnIndex(); will(returnValue(0));
            allowing(column).getValue("A''''''BC", adapter); will(returnValue("A''''''BC"));
            allowing(column).getGroupCount("A''''''BC"); will(returnValue(1));
        }});

        final String[] line = new String[] { "A'''BC", "123" };
        final CsvImportTupleImpl tuple = new CsvImportTupleImpl("file", 1, line);

        assertEquals(tuple.getColumnValue(column, adapter), "A''''''BC");

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetColumnConstant() throws Exception {

        final ImportColumn column = mockery.mock(ImportColumn.class, "column");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        mockery.checking(new Expectations() {{
            allowing(column).getColumnIndex(); will(returnValue(-1));
            allowing(column).getValue(null, adapter); will(returnValue("const"));
        }});

        final String[] line = new String[] { "val1", "val2" };
        final CsvImportTupleImpl tuple = new CsvImportTupleImpl("file", 1, line);

        assertEquals(tuple.getColumnValue(column, adapter), "const");

        mockery.assertIsSatisfied();

    }
}
