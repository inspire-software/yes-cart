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
import org.yes.cart.util.DateUtils;
import org.yes.cart.util.TimeContext;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 18/11/2014
 * Time: 00:00
 */
public class ProductTagSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder<Query> {

    private static final String TAG_NEWARRIVAL = "newarrival";

    private final ShopSearchSupportService shopSearchSupportService;

    public ProductTagSearchQueryBuilder(final ShopSearchSupportService shopSearchSupportService) {
        this.shopSearchSupportService = shopSearchSupportService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
                    return Collections.singletonList(aggregatedQuery.build());
                }

        } else if (singleValues.size() == 1) {

                final Query clause = createTagQuery(navigationContext, escapeValue(singleValues.iterator().next()));
                if (clause != null) {
                    return Collections.singletonList(clause);
                }

            }
            return null;
        }

        final Query clause = createTagQuery(navigationContext, escapeValue(value));
        if (clause != null) {
            return Collections.singletonList(clause);
        }
        return null;

    }

    private Query createTagQuery(final NavigationContext<Query> navigationContext, final String tag) {

        if (TAG_NEWARRIVAL.equals(tag)) {

            final ZonedDateTime fromDate = earliestNewArrivalDate(navigationContext);

            final BooleanQuery.Builder aggregateQuery = new BooleanQuery.Builder();

            // newarrival tag is top priority 20 points
            aggregateQuery.add(createTermQuery(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL, 20f), BooleanClause.Occur.SHOULD);
            aggregateQuery.add(createRangeQuery(PRODUCT_CREATED_FIELD, fromDate.toInstant().toEpochMilli(), null), BooleanClause.Occur.SHOULD);

            return aggregateQuery.build();

        }


        if (isEmptyValue(tag)) {
            return null;
        }

        return createTermQuery(PRODUCT_TAG_FIELD, tag, 20f);

    }


    private ZonedDateTime earliestNewArrivalDate(final NavigationContext<Query> navigationContext) {

        int daysOffset = 1;
        if (CollectionUtils.isEmpty(navigationContext.getCategories())) {

            daysOffset = shopSearchSupportService.getCategoryNewArrivalOffsetDays(0L, navigationContext.getCustomerShopId());

        } else {
            for (final Long categoryId : navigationContext.getCategories()) {
                // get the earliest
                daysOffset = Math.max(daysOffset, shopSearchSupportService.getCategoryNewArrivalOffsetDays(categoryId, navigationContext.getCustomerShopId()));
            }
        }
        return DateUtils.zdtAtStartOfDay(now().plusDays(-daysOffset));
    }

    ZonedDateTime now() {
        return TimeContext.getZonedDateTime();
    }


}
