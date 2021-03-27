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

package org.yes.cart.bulkcommon.service.support.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.service.support.csv.impl.ColumnValueLookUpQueryParameterStrategyValueProviderImpl;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategyValueProvider;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.utils.spring.LinkedHashMapBeanImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:06 AM
 */
public class AbstractByParameterByColumnNameStrategyTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testReplace() {
        final AbstractByParameterByColumnNameStrategy<ImpExDescriptor, ImpExTuple, Object> strategy = new AbstractByParameterByColumnNameStrategy<ImpExDescriptor, ImpExTuple, Object>() {
            @Override
            protected boolean addParameter(final int index,
                                        final boolean wrappedInQuotes, final Object param,
                                        final StringBuilder query,
                                        final List<Object> params) {
                query.append('?').append(index);
                params.add(param);
                return true;
            }

            @Override
            public LookUpQuery getQuery(final ImpExDescriptor descriptor,
                                        final Object masterObject,
                                        final ImpExTuple tuple,
                                        final Object adapter,
                                        final String queryTemplate) {
                return null;
            }
        };

        strategy.setProviders(new LinkedHashMapBeanImpl<>(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }}));
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());

        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final CsvImportColumn nameColumn = mockery.mock(CsvImportColumn.class, "nameColumn");
        final CsvImportTuple tuple = mockery.mock(CsvImportTuple.class, "tuple");
        final CsvValueAdapter adapter = mockery.mock(CsvValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            oneOf(master).getId(); will(returnValue(10L));
            oneOf(descriptor).getColumn("code"); will(returnValue(codeColumn));
            oneOf(tuple).getColumnValue(codeColumn, adapter); will(returnValue("A'''BC"));
            oneOf(descriptor).getColumn("parent.name"); will(returnValue(nameColumn));
            oneOf(tuple).getColumnValue(nameColumn, adapter); will(returnValue("P001"));
        }});

        final StringBuilder query = new StringBuilder();
        final List params = new ArrayList();
        strategy.replaceColumnNamesInTemplate(
                "select * from Entity e where e.parentId = {masterObjectId} and e.code = '{code}' and e.name = {parent.name} ",
                query, params, descriptor, master, tuple, adapter);

        assertEquals(query.toString(), "select * from Entity e where e.parentId = ?1 and e.code = ?2 and e.name = ?3 ");
        assertEquals(params.size(), 3);
        assertEquals(params.get(0), Long.valueOf(10L));
        assertEquals(params.get(1), "A'''BC"); // escaped value
        assertEquals(params.get(2), "P001");

        mockery.assertIsSatisfied();
    }


}
