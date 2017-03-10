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
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExContext;
import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkimport.model.ImportTuple;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/03/2017
 * Time: 21:18
 */
public class ContextShopCodeLookUpQueryParameterStrategyValueProviderImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetPlaceholderValueNone() throws Exception {

        final ImpExDescriptor descriptor = mockery.mock(ImpExDescriptor.class, "descriptor");
        final ImpExContext context = mockery.mock(ImpExContext.class, "context");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        final ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl provider = new ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl();

        mockery.checking(new Expectations() {{
            allowing(descriptor).getContext(); will(returnValue(context));
            allowing(context).getShopCode(); will(returnValue(null));
            allowing(context).getShopCodeColumn(); will(returnValue(null));
        }});

        assertNull(provider.getPlaceholderValue(null, descriptor, null, null, adapter, null));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetPlaceholderValueShopCode() throws Exception {

        final ImpExDescriptor descriptor = mockery.mock(ImpExDescriptor.class, "descriptor");
        final ImpExContext context = mockery.mock(ImpExContext.class, "context");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        final ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl provider = new ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl();

        mockery.checking(new Expectations() {{
            allowing(descriptor).getContext(); will(returnValue(context));
            allowing(context).getShopCode(); will(returnValue("ABC"));
            allowing(context).getShopCodeColumn(); will(returnValue(null));
        }});

        assertEquals("ABC", provider.getPlaceholderValue(null, descriptor, null, null, adapter, null));

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetPlaceholderValueShopCodeColumn() throws Exception {

        final ImpExDescriptor descriptor = mockery.mock(ImpExDescriptor.class, "descriptor");
        final ImpExContext context = mockery.mock(ImpExContext.class, "context");
        final ImpExColumn shopCode = mockery.mock(ImpExColumn.class, "shopCode");
        final ImportTuple importTuple = mockery.mock(ImportTuple.class, "importTuple");
        final ValueAdapter adapter = mockery.mock(ValueAdapter.class, "adapter");

        final ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl provider = new ContextShopCodeLookUpQueryParameterStrategyValueProviderImpl();

        mockery.checking(new Expectations() {{
            allowing(descriptor).getContext(); will(returnValue(context));
            allowing(context).getShopCode(); will(returnValue(null));
            allowing(context).getShopCodeColumn(); will(returnValue("shopCode"));
            allowing(descriptor).getColumn("shopCode"); will(returnValue(shopCode));
            allowing(importTuple).getColumnValue(with(equal(shopCode)), with(any(ValueAdapter.class))); will(returnValue("CDE"));
        }});

        assertEquals("CDE", provider.getPlaceholderValue(null, descriptor, null, importTuple, adapter, null));

        mockery.assertIsSatisfied();

    }
}