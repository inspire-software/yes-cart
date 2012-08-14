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
import org.yes.cart.bulkimport.service.support.impl.NativeSQLQuery;

import java.util.List;

/**
 * Generates a native sql string with all parameter placeholders replaced.
 * No parameter object is available, just plain sql string.
 *
 * User: denispavlov
 * Date: 12-08-08
 * Time: 9:22 AM
 */
public class CsvDescriptorNativeInsertStrategy extends AbstractByParameterByColumnNameStrategy
        implements LookUpQueryParameterStrategy {

    @Override
    protected void addParameter(final int index, final Object param, final StringBuilder query, final List<Object> params) {
        if (param == null) {
            query.append("NULL");
        } else {
            query.append(param);
        }
    }

    /** {@inheritDoc} */
    public LookUpQuery getQuery(final ImportDescriptor descriptor,
                                final Object masterObject,
                                final ImportTuple tuple,
                                final ValueAdapter adapter,
                                final String queryTemplate) {

        final StringBuilder sql = new StringBuilder();
        replaceColumnNamesInTemplate(queryTemplate, sql, null, (CsvImportDescriptor) descriptor, masterObject, tuple, adapter);
        return new NativeSQLQuery(sql.toString());
    }
}
