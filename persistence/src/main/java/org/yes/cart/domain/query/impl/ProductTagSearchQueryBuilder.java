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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 18/11/2014
 * Time: 00:00
 */
public class ProductTagSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    public static final String TAG_NEWARRIVAL = "newarrival";

    private final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>();


    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        if (value instanceof Date) {

            final SimpleDateFormat toMinutes = getDateFormat();
            final String fromDate = toMinutes.format((Date) value);

            BooleanQuery aggregateQuery = new BooleanQuery();

            aggregateQuery.add(createTermQuery(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL, 2.5f), BooleanClause.Occur.SHOULD);
            aggregateQuery.add(createRangeQuery(PRODUCT_CREATED_FIELD, fromDate, null, 2f), BooleanClause.Occur.SHOULD);

            return aggregateQuery;

        }


        if (isEmptyValue(value)) {
            return null;
        }

        return createTermQuery(PRODUCT_TAG_FIELD, escapeValue(value), 2.5f);

    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {
        return createStrictQuery(shopId, parameter, value);
    }

    private SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = format.get();
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyyMMdd0000");
            format.set(dateFormat);
        }
        return dateFormat;
    }
}
