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
import org.springframework.util.CollectionUtils;
import org.yes.cart.search.ShopSearchSupportService;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;

import java.util.*;

/**
 * User: denispavlov
 * Date: 18/11/2014
 * Time: 00:00
 */
public class ProductTagSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder<Query> {

    private static final String TAG_NEWARRIVAL = "newarrival";

    private final ThreadLocal<Calendar> format = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };

    private final ShopSearchSupportService shopSearchSupportService;

    public ProductTagSearchQueryBuilder(final ShopSearchSupportService shopSearchSupportService) {
        this.shopSearchSupportService = shopSearchSupportService;
    }


    /**
     * {@inheritDoc}
     */
    public List<Query> createQueryChain(final NavigationContext<Query> navigationContext, final String parameter, final Object value) {

        if (value instanceof Collection) {

            final Collection singleValues = (Collection) value;
            if (singleValues.size() > 1) {

                final BooleanQuery.Builder aggregatedQuery = new BooleanQuery.Builder();

                boolean hasClause = false;
                for (final Object item : singleValues) {

                    final Query clause = createTagQuery(navigationContext, escapeValue(item));
                    if (clause != null) {
                        aggregatedQuery.add(clause, BooleanClause.Occur.SHOULD);
                        hasClause = true;
                    }

                }

                if (hasClause) {
                    return Collections.<Query>singletonList(aggregatedQuery.build());
                }

        } else if (singleValues.size() == 1) {

                final Query clause = createTagQuery(navigationContext, escapeValue(singleValues.iterator().next()));
                if (clause != null) {
                    return Collections.<Query>singletonList(clause);
                }

            }
            return null;
        }

        final Query clause = createTagQuery(navigationContext, escapeValue(value));
        if (clause != null) {
            return Collections.<Query>singletonList(clause);
        }
        return null;

    }

    private Query createTagQuery(final NavigationContext<Query> navigationContext, final String tag) {

        if (TAG_NEWARRIVAL.equals(tag)) {

            final Long fromDate = getDateOnly(earliestNewArrivalDate(navigationContext));

            final BooleanQuery.Builder aggregateQuery = new BooleanQuery.Builder();

            // newarrival tag is top priority 20 points
            aggregateQuery.add(createTermQuery(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL, 20f), BooleanClause.Occur.SHOULD);
            aggregateQuery.add(createRangeQuery(PRODUCT_CREATED_FIELD, fromDate, null), BooleanClause.Occur.SHOULD);

            return aggregateQuery.build();

        }


        if (isEmptyValue(tag)) {
            return null;
        }

        return createTermQuery(PRODUCT_TAG_FIELD, tag, 20f);

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


    private Date earliestNewArrivalDate(final NavigationContext<Query> navigationContext) {

        Date beforeDays = new Date();
        if (CollectionUtils.isEmpty(navigationContext.getCategories())) {

            beforeDays = shopSearchSupportService.getCategoryNewArrivalDate(0L, navigationContext.getShopId());

        } else {
            for (final Long categoryId : navigationContext.getCategories()) {
                Date catBeforeDays = shopSearchSupportService.getCategoryNewArrivalDate(categoryId, navigationContext.getShopId());
                if (catBeforeDays.before(beforeDays)) {
                    beforeDays = catBeforeDays; // get the earliest
                }
            }
        }
        return beforeDays;
    }


}
