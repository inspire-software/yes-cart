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

package org.yes.cart.search.query.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.constants.Constants;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:26
 */
public class AttributeSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder<Query> {

    /**
     * {@inheritDoc}
     */
    public List<Query> createQueryChain(final NavigationContext<Query> navigationContext, final String parameter, final Object value) {

        if (StringUtils.isBlank(parameter)) {
            return null;
        }

        final String escapedParameter = escapeValue(parameter);

        if (value instanceof Collection) {

            final Collection singleValues = (Collection) value;
            if (singleValues.size() > 1) {

                final BooleanQuery.Builder aggregatedQuery = new BooleanQuery.Builder();

                boolean hasClause = false;
                for (final Object item : singleValues) {

                    final Query clause = createAttributeQuery(escapedParameter, escapeValue(item));
                    if (clause != null) {
                        aggregatedQuery.add(clause, BooleanClause.Occur.SHOULD);
                        hasClause = true;
                    }

                }

                if (hasClause) {
                    return Collections.singletonList(aggregatedQuery.build());
                }

            } else if (singleValues.size() == 1) {

                final Query clause = createAttributeQuery(escapedParameter, escapeValue(singleValues.iterator().next()));
                if (clause != null) {
                    return Collections.singletonList(clause);
                }

            }
            return null;
        }

        final Query clause = createAttributeQuery(escapedParameter, escapeValue(value));
        if (clause != null) {
            return Collections.singletonList(clause);
        }
        return null;

    }

    private Query createAttributeQuery(final String parameter, final String value) {

        if (isEmptyValue(value)) {
            return null;
        }

        if (value.contains(Constants.RANGE_NAVIGATION_DELIMITER)) { // value range navigation
            final String[] attrValues = StringUtils.splitByWholeSeparatorPreserveAllTokens(value, Constants.RANGE_NAVIGATION_DELIMITER);

            final Long searchValueLo = attrValues[0].length() > 0 ? NumberUtils.toLong(attrValues[0]) : null;
            final Long searchValueHi = attrValues[1].length() > 0 ? NumberUtils.toLong(attrValues[1]) : null;

            return createRangeQuery(parameter + "_range", searchValueLo, searchValueHi, 3.5f);

        }

        return createTermQuery(parameter, value.toLowerCase(), 3.5f);

    }

}
