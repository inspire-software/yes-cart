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

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategyValueProvider;

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

    private static final Pattern MATCH_COLUMNS_IN_SQL_TEMPLATE = Pattern.compile("(([']{0,1})(\\{[a-zA-Z\\d]*\\})([']{0,1}))");

    protected final void replaceColumnNamesInTemplate(final String queryTemplate,
                                                      final StringBuilder query,
                                                      final List<Object> params,
                                                      final ImpExDescriptor descriptor,
                                                      final Object masterObject,
                                                      final ImpExTuple tuple,
                                                      final ValueAdapter adapter) {

        if (queryTemplate == null || queryTemplate.length() == 0) {
            throw new IllegalArgumentException("No look up query for a field in tuple: " + tuple);
        }

        final Matcher matcher = MATCH_COLUMNS_IN_SQL_TEMPLATE.matcher(queryTemplate);

        int lastIndex = 0;
        int paramCount = 1;
        while (matcher.find()) {
            final String placeholder = matcher.group(3);
            final boolean wrappedInQuotes = matcher.group(2).length() > 0 && matcher.group(2).equals(matcher.group(4));
            final boolean startingQuote = !wrappedInQuotes && matcher.group(2).length() > 0;
            final boolean endingQuote = !wrappedInQuotes && matcher.group(4).length() > 0;
            query.append(queryTemplate.substring(lastIndex, startingQuote ? matcher.end(2) : matcher.start(0)));
            lastIndex = endingQuote ? matcher.start(4) : matcher.end(0);

            final LookUpQueryParameterStrategyValueProvider specific = providers.get(placeholder);
            final Object value;
            if (specific == null) {
                value = defaultProvider.getPlaceholderValue(
                        placeholder, descriptor, masterObject, tuple, adapter, queryTemplate);
            } else {
                value = specific.getPlaceholderValue(
                        placeholder, descriptor, masterObject, tuple, adapter, queryTemplate);
            }

            if (addParameter(paramCount, wrappedInQuotes, value, query, params)) {

                paramCount++;

            }
        }
        query.append(queryTemplate.substring(lastIndex));
    }

    /**
     * @param index parameter index
     * @param wrappedInQuotes true if value is wrapped in quotes (' or ") in the template
     * @param param actual param value
     * @param query current query builder
     * @param params current params list
     *
     * @return boolean flag whether the parameter was added to params
     */
    protected abstract boolean addParameter(final int index,
                                            final boolean wrappedInQuotes,
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
