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

import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategyValueProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 9:29 AM
 */
public abstract class AbstractByParameterByColumnNameStrategy implements LookUpQueryParameterStrategy {

    private Map<String, LookUpQueryParameterStrategyValueProvider> providers = Collections.emptyMap();
    private LookUpQueryParameterStrategyValueProvider defaultProvider;

    private static final Pattern MATCH_COLUMNS_IN_SQL_TEMPLATE = Pattern.compile("(\\{[a-zA-Z\\d]*\\})");

    protected final void replaceColumnNamesInTemplate(final String queryTemplate,
                                                      final StringBuilder query,
                                                      final List<Object> params,
                                                      final ImportDescriptor descriptor,
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
            final String placeholder = matcher.group(0);
            query.append(queryTemplate.substring(lastIndex, matcher.start(0)));
            lastIndex = matcher.end(0);

            final LookUpQueryParameterStrategyValueProvider specific = providers.get(placeholder);
            final Object value;
            if (specific == null) {
                value = defaultProvider.getPlaceholderValue(
                        placeholder, descriptor, masterObject, tuple, adapter, queryTemplate);
            } else {
                value = specific.getPlaceholderValue(
                        placeholder, descriptor, masterObject, tuple, adapter, queryTemplate);
            }
            addParameter(paramCount, value, query, params);

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


    /**
     * IoC.
     *
     * @param defaultProvider {@link LookUpQueryParameterStrategyValueProvider} to use.
     */
    public void setDefaultProvider(final LookUpQueryParameterStrategyValueProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    /**
     * IoC.
     *
     * @param providers {@link LookUpQueryParameterStrategyValueProvider} to use.
     */
    public void setProviders(final Map<String, LookUpQueryParameterStrategyValueProvider> providers) {
        this.providers = providers;
    }
}
