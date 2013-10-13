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
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.EntityCacheKeyStrategy;
import org.yes.cart.bulkimport.service.support.LookUpQuery;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.domain.entity.Identifiable;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:43 AM
 */
public class ColumnLookUpQueryCacheKeyStrategyTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testKeyForMaster() throws Exception {

        final LookUpQueryParameterStrategy sqlStrategy = mockery.mock(LookUpQueryParameterStrategy.class, "sqlStrategy");
        final LookUpQuery query = mockery.mock(LookUpQuery.class, "query");
        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        mockery.checking(new Expectations() {{
            allowing(master).getId(); will(returnValue(10L));
            allowing(codeColumn).getColumnIndex(); will(returnValue(3));
            allowing(codeColumn).getName(); will(returnValue("code"));
            allowing(codeColumn).getLookupQuery(); will(returnValue("queryString"));
            allowing(codeColumn).isUseMasterObject(); will(returnValue(true));
            one(sqlStrategy).getQuery(descriptor, master, tuple, adapter, "queryString"); will(returnValue(query));
            one(query).getQueryString(); will(returnValue("queryString"));
            one(query).getParameters(); will(returnValue(new Object[] { "p1", "p2" }));
        }});

        final EntityCacheKeyStrategy strategy = new ColumnLookUpQueryCacheKeyStrategy(sqlStrategy);
        final String key = strategy.keyFor(descriptor, codeColumn, master, tuple, adapter);

        assertEquals("code_3_queryString_p1_p2_10", key);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testKeyForMasterNull() throws Exception {

        final LookUpQueryParameterStrategy sqlStrategy = mockery.mock(LookUpQueryParameterStrategy.class, "sqlStrategy");
        final LookUpQuery query = mockery.mock(LookUpQuery.class, "query");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        mockery.checking(new Expectations() {{
            allowing(codeColumn).getColumnIndex(); will(returnValue(3));
            allowing(codeColumn).getName(); will(returnValue("code"));
            allowing(codeColumn).getLookupQuery(); will(returnValue("queryString"));
            allowing(codeColumn).isUseMasterObject(); will(returnValue(true));
            one(sqlStrategy).getQuery(descriptor, null, tuple, adapter, "queryString"); will(returnValue(query));
            one(query).getQueryString(); will(returnValue("queryString"));
            one(query).getParameters(); will(returnValue(new Object[] { "p1", "p2" }));
        }});

        final EntityCacheKeyStrategy strategy = new ColumnLookUpQueryCacheKeyStrategy(sqlStrategy);
        final String key = strategy.keyFor(descriptor, codeColumn, null, tuple, adapter);

        assertEquals("code_3_queryString_p1_p2_unknownMaster", key);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testKeyForNoMaster() throws Exception {

        final LookUpQueryParameterStrategy sqlStrategy = mockery.mock(LookUpQueryParameterStrategy.class, "sqlStrategy");
        final LookUpQuery query = mockery.mock(LookUpQuery.class, "query");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            allowing(codeColumn).getColumnIndex(); will(returnValue(3));
            allowing(codeColumn).getName(); will(returnValue("code"));
            allowing(codeColumn).getLookupQuery(); will(returnValue("queryString"));
            allowing(codeColumn).isUseMasterObject(); will(returnValue(false));
            one(sqlStrategy).getQuery(descriptor, null, tuple, adapter, "queryString"); will(returnValue(query));
            one(query).getQueryString(); will(returnValue("queryString"));
            one(query).getParameters(); will(returnValue(new Object[] { "p1", "p2" }));
        }});

        final EntityCacheKeyStrategy strategy = new ColumnLookUpQueryCacheKeyStrategy(sqlStrategy);
        final String key = strategy.keyFor(descriptor, codeColumn, null, tuple, adapter);

        assertEquals("code_3_queryString_p1_p2", key);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testKeyForNoMasterNoParams() throws Exception {

        final LookUpQueryParameterStrategy sqlStrategy = mockery.mock(LookUpQueryParameterStrategy.class, "sqlStrategy");
        final LookUpQuery query = mockery.mock(LookUpQuery.class, "query");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            allowing(codeColumn).getColumnIndex(); will(returnValue(3));
            allowing(codeColumn).getName(); will(returnValue("code"));
            allowing(codeColumn).getLookupQuery(); will(returnValue("queryString"));
            allowing(codeColumn).isUseMasterObject(); will(returnValue(false));
            one(sqlStrategy).getQuery(descriptor, null, tuple, adapter, "queryString"); will(returnValue(query));
            one(query).getQueryString(); will(returnValue("queryString"));
            one(query).getParameters(); will(returnValue(new Object[0]));
        }});

        final EntityCacheKeyStrategy strategy = new ColumnLookUpQueryCacheKeyStrategy(sqlStrategy);
        final String key = strategy.keyFor(descriptor, codeColumn, null, tuple, adapter);

        assertEquals("code_3_queryString", key);

        mockery.assertIsSatisfied();

    }
}
