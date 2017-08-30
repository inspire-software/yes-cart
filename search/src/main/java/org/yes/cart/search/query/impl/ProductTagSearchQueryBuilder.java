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

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

import java.util.Calendar;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 18/11/2014
 * Time: 00:00
 */
public class ProductTagSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    public static final String TAG_NEWARRIVAL = "newarrival";

    private final ThreadLocal<Calendar> format = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };


    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final long customerShopId, final String parameter, final Object value) {

        if (value instanceof Date) {

            final Long fromDate = getDateOnly((Date) value);

            final BooleanQuery.Builder aggregateQuery = new BooleanQuery.Builder();

            // newarrival tag is top priority 20 points
            aggregateQuery.add(createTermQuery(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL, 20f), BooleanClause.Occur.SHOULD);
            aggregateQuery.add(createRangeQuery(PRODUCT_CREATED_FIELD, fromDate, null), BooleanClause.Occur.SHOULD);

            return aggregateQuery.build();

        }


        if (isEmptyValue(value)) {
            return null;
        }

        return createTermQuery(PRODUCT_TAG_FIELD, escapeValue(value), 20f);

    }

    protected Long getDateOnly(final Date value) {
        final Calendar calendar = format.get();
        calendar.setTime(value);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final long customerShopId, final String parameter, final Object value) {
        return createStrictQuery(shopId, customerShopId, parameter, value);
    }

}
