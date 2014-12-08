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

package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:26
 */
public class AttributeSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        if (isEmptyValue(value) || StringUtils.isBlank(parameter)) {
            return null;
        }

        final String searchValue = String.valueOf(value);

        final String escapedParameter = escapeValue(parameter);

        if (searchValue.contains(Constants.RANGE_NAVIGATION_DELIMITER)) { // value range navigation
            final String[] attrValues = StringUtils.splitByWholeSeparatorPreserveAllTokens(searchValue, Constants.RANGE_NAVIGATION_DELIMITER);

            final BooleanQuery aggregatedQuery = new BooleanQuery();

            final String searchValueLo = attrValues[0].length() > 0 ? escapedParameter + escapeValue(attrValues[0]) : null;
            final String searchValueHi = attrValues[1].length() > 0 ? escapedParameter + escapeValue(attrValues[1]) : null;

            aggregatedQuery.add(createTermQuery(ATTRIBUTE_CODE_FIELD, escapedParameter), BooleanClause.Occur.MUST);
            aggregatedQuery.add(createRangeQuery(ATTRIBUTE_VALUE_FIELD, searchValueLo, searchValueHi, 3.5f), BooleanClause.Occur.MUST);

            return aggregatedQuery;

        }

        final BooleanQuery aggregatedQuery = new BooleanQuery();

        final String ftSearchValue = escapedParameter + escapeValue(searchValue);

        aggregatedQuery.add(createTermQuery(ATTRIBUTE_CODE_FIELD, escapedParameter), BooleanClause.Occur.MUST);
        aggregatedQuery.add(createTermQuery(ATTRIBUTE_VALUE_FIELD, ftSearchValue, 3.5f), BooleanClause.Occur.MUST);

        return aggregatedQuery;

    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {
        return createStrictQuery(shopId, parameter, value);
    }
}
