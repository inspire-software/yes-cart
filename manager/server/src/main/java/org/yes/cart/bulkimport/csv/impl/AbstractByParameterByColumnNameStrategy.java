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
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.domain.entity.Identifiable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 9:29 AM
 */
public abstract class AbstractByParameterByColumnNameStrategy implements LookUpQueryParameterStrategy {

    private static final String GUID        = "{GUID}";
    private static final String MASTER      = "{masterObject}";
    private static final String MASTER_ID   = "{masterObjectId}";

    private static final Pattern MATCH_COLUMNS_IN_SQL_TEMPLATE = Pattern.compile("(\\{[a-zA-Z\\d]*\\})");

    protected final void replaceColumnNamesInTemplate(final String queryTemplate,
                                                      final StringBuilder query,
                                                      final List<Object> params,
                                                      final CsvImportDescriptor descriptor,
                                                      final Object masterObject,
                                                      final ImportTuple tuple,
                                                      final ValueAdapter adapter) {

        if (queryTemplate == null || queryTemplate.length() == 0) {
            throw new IllegalArgumentException("No look up query for a field in tuple: " + tuple);
        }

        final Matcher matcher = MATCH_COLUMNS_IN_SQL_TEMPLATE.matcher(queryTemplate);

        int lastIndex = 0;
        int paramCount = 1;
        while (matcher.find()) {
            final String columnName = matcher.group(0);
            query.append(queryTemplate.substring(lastIndex, matcher.start(0)));
            lastIndex = matcher.end(0);
            if (MASTER.equals(columnName)) {
                addParameter(paramCount, masterObject, query, params);
            } else if (MASTER_ID.equals(columnName)) {
                if (masterObject != null) {
                    addParameter(paramCount, ((Identifiable) masterObject).getId(), query, params);
                } else {
                    addParameter(paramCount, 0L, query, params);
                }
            } else if (GUID.equals(columnName)) {
                addParameter(paramCount, java.util.UUID.randomUUID().toString(), query, params);
            } else {
                final String realColumnName = columnName.substring(1, columnName.length() - 1);
                final ImportColumn column = descriptor.getImportColumn(realColumnName);
                if (column != null) {
                    addParameter(paramCount, tuple.getColumnValue(column, adapter), query, params);
                } else {
                    addParameter(paramCount, null, query, params);
                }
            }
            paramCount++;
        }
        query.append(queryTemplate.substring(lastIndex));
    }

    /**
     * @param index parameter index
     * @param param actual param value
     * @param query current query builder
     * @param params current params list
     */
    protected abstract void addParameter(final int index,
                                         final Object param,
                                         final StringBuilder query,
                                         final List<Object> params);


}
