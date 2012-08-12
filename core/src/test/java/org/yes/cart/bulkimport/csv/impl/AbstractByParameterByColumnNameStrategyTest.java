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
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.LookUpQuery;
import org.yes.cart.domain.entity.Identifiable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:06 AM
 */
public class AbstractByParameterByColumnNameStrategyTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testReplace() {
        final AbstractByParameterByColumnNameStrategy strategy = new AbstractByParameterByColumnNameStrategy() {
            @Override
            protected void addParameter(final int index,
                                        final Object param,
                                        final StringBuilder query,
                                        final List<Object> params) {
                query.append('?').append(index);
                params.add(param);
            }

            public LookUpQuery getQuery(final ImportDescriptor descriptor,
                                        final Object masterObject,
                                        final ImportTuple tuple,
                                        final ValueAdapter adapter,
                                        final String queryTemplate) {
                return null;
            }
        };

        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId(); will(returnValue(10L));
            one(descriptor).getImportColumn("code"); will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter); will(returnValue("A'''BC"));
        }});

        final StringBuilder query = new StringBuilder();
        final List params = new ArrayList();
        strategy.replaceColumnNamesInTemplate(
                "select * from Entity e where e.parentId = {masterObjectId} and e.code = '{code}' ",
                query, params, descriptor, master, tuple, adapter);

        assertEquals(query.toString(), "select * from Entity e where e.parentId = ?1 and e.code = '?2' ");
        assertEquals(params.size(), 2);
        assertEquals(params.get(0), Long.valueOf(10L));
        assertEquals(params.get(1), "A'''BC"); // escaped value

        mockery.assertIsSatisfied();
    }


}
