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

import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.LookUpQuery;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkimport.service.support.impl.HSQLQuery;

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
public class CsvColumnLookUpQueryStrategy extends AbstractByParameterByColumnNameStrategy
          implements LookUpQueryParameterStrategy {

    @Override
    protected void addParameter(final int index,
                                final Object param,
                                final StringBuilder query,
                                final List<Object> params) {
        query.append('?').append(index);
        params.add(param);
    }

    /** {@inheritDoc} */
    public LookUpQuery getQuery(final ImportDescriptor descriptor,
                                final Object masterObject,
                                final ImportTuple tuple,
                                final ValueAdapter adapter,
                                final String queryTemplate) {
        final StringBuilder hsql = new StringBuilder();
        final List params = new ArrayList();
        replaceColumnNamesInTemplate(queryTemplate, hsql, params, (CsvImportDescriptor) descriptor, masterObject, tuple, adapter);
        return new HSQLQuery(hsql.toString(), params.toArray());
    }
}
