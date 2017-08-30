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
import org.yes.cart.search.query.ProductSearchQueryBuilder;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:26
 */
public class AttributeSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final long customerShopId, final String parameter, final Object value) {

        if (isEmptyValue(value) || StringUtils.isBlank(parameter)) {
            return null;
        }

        final String searchValue = String.valueOf(value);

        final String escapedParameter = escapeValue(parameter);

        if (searchValue.contains(Constants.RANGE_NAVIGATION_DELIMITER)) { // value range navigation
            final String[] attrValues = StringUtils.splitByWholeSeparatorPreserveAllTokens(searchValue, Constants.RANGE_NAVIGATION_DELIMITER);

            final BooleanQuery.Builder aggregatedQuery = new BooleanQuery.Builder();

            final Long searchValueLo = attrValues[0].length() > 0 ? NumberUtils.toLong(attrValues[0]) : null;
            final Long searchValueHi = attrValues[1].length() > 0 ? NumberUtils.toLong(attrValues[1]) : null;

            aggregatedQuery.add(createRangeQuery(escapedParameter + "_range", searchValueLo, searchValueHi, 3.5f), BooleanClause.Occur.MUST);

            return aggregatedQuery.build();

        }

        final BooleanQuery.Builder aggregatedQuery = new BooleanQuery.Builder();

        final String ftSearchValue = escapeValue(searchValue);

        aggregatedQuery.add(createTermQuery(escapedParameter, ftSearchValue.toLowerCase(), 3.5f), BooleanClause.Occur.MUST);

        return aggregatedQuery.build();

    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final long customerShopId, final String parameter, final Object value) {
        return createStrictQuery(shopId, customerShopId, parameter, value);
    }
}
