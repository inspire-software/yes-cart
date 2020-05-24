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

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.query.impl.HSQLQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy that replaces the column names placeholders with ?1, ?2 ... ?n and assembles
 * values into param array, which is later retrievable through LookUpQuery object.
 *
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:01 AM
 */
public class ImpExColumnLookUpQueryStrategy extends AbstractByParameterByColumnNameStrategy<ImpExDescriptor, ImpExTuple, Object>
          implements LookUpQueryParameterStrategy<ImpExDescriptor, ImpExTuple, Object> {

    @Override
    protected boolean addParameter(final int index,
                                   final boolean wrappedInQuotes,
                                    final Object param,
                                    final StringBuilder query,
                                    final List<Object> params) {
        query.append('?').append(index);
        params.add(param);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public LookUpQuery getQuery(final ImpExDescriptor descriptor,
                                final Object masterObject,
                                final ImpExTuple tuple,
                                final Object adapter,
                                final String queryTemplate) {
        final StringBuilder hsql = new StringBuilder();
        final List params = new ArrayList();
        replaceColumnNamesInTemplate(queryTemplate, hsql, params, descriptor, masterObject, tuple, adapter);
        return new HSQLQuery(hsql.toString(), params.toArray());
    }
}
