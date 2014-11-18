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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:16
 */
public abstract class AbstractStrictFieldSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    private final String field;
    private final float boost;

    protected AbstractStrictFieldSearchQueryBuilder(final String field) {
        this.field = field;
        this.boost = 1f;
    }

    protected AbstractStrictFieldSearchQueryBuilder(final String field, final float boost) {
        this.field = field;
        this.boost = boost;
    }

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        if (value instanceof Collection) {
            final Collection singleValues = (Collection) value;
            if (singleValues.size() > 1) {
                final BooleanQuery aggregatedQuery = new BooleanQuery();
                for (final Object singleValue : singleValues) {
                    aggregatedQuery.add(createTermQuery(field, escapeValue(singleValue), boost), BooleanClause.Occur.SHOULD);
                }
                return aggregatedQuery;
            } else if (singleValues.size() == 1) {
                return createTermQuery(field, escapeValue(singleValues.iterator().next()), boost);
            } else {
                return null;
            }

        }

        if (isEmptyValue(value)) {
            return null;
        }

        return createTermQuery(field, escapeValue(value), boost);
    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {

        return createStrictQuery(shopId, parameter, value);

    }
}
