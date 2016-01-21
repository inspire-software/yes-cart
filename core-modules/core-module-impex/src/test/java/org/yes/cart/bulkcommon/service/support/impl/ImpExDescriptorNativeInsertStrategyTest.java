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

package org.yes.cart.bulkcommon.service.support.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.support.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategyValueProvider;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.domain.entity.Identifiable;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:26 AM
 */
public class ImpExDescriptorNativeInsertStrategyTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetQuery() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId();
            will(returnValue(10L));
            one(descriptor).getColumn("code");
            will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter);
            will(returnValue("A''BC"));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = '{code}' ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = ?1 ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 1);
        assertEquals(query.getParameters()[0], "A''BC");

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetQueryNullInQuotes() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId();
            will(returnValue(10L));
            one(descriptor).getColumn("code");
            will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter);
            will(returnValue(null));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = '{code}' ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = '' ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 0);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetQueryNullInI18N() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId(); will(returnValue(10L));
            one(descriptor).getColumn("code"); will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter); will(returnValue(null));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = 'en#~#{code}' ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = 'en#~#' ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 0);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetQueryNullValue() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId(); will(returnValue(10L));
            one(descriptor).getColumn("code"); will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter); will(returnValue(null));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = {code} ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = NULL ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 0);

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetQueryParenthesisValue() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId();
            will(returnValue(10L));
            one(descriptor).getColumn("code");
            will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter);
            will(returnValue("some (code)"));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = '{code}' ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = ?1 ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 1);
        assertEquals(query.getParameters()[0], "some (code)");

        mockery.assertIsSatisfied();

    }



    @Test
    public void testGetQueryCombinedValue() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId();
            will(returnValue(10L));
            one(descriptor).getColumn("code");
            will(returnValue(codeColumn));
            one(tuple).getColumnValue(codeColumn, adapter);
            will(returnValue("some' code"));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "SELECT * FROM TENTITY e WHERE e.PARENT_ID = {masterObjectId} AND e.CODE = '{code}_combined' ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "SELECT * FROM TENTITY e WHERE e.PARENT_ID = 10 AND e.CODE = 'some'' code_combined' ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 0);

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetQueryPartialValue() throws Exception {

        final ImpExDescriptorNativeInsertStrategy strategy = new ImpExDescriptorNativeInsertStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());


        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final ImportTuple tuple = mockery.mock(ImportTuple.class, "tuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            one(master).getId();
            will(returnValue(10L));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "\n                    DELETE FROM TCATEGORYATTRVALUE WHERE GUID = '{masterObjectId}_CATDESC_en'");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "\n                    DELETE FROM TCATEGORYATTRVALUE WHERE GUID = '10_CATDESC_en'");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 0);

        mockery.assertIsSatisfied();

    }
}
