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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Query builder to create query to search by tag values
 * User: igora Igor Azarny
 * Date: 22-apr-12
 * Time: 6:08 PM
 */
public class TagSearchQueryBuilder implements ProductSearchQueryBuilder {

    public static final String TAG_NEWARRIVAL = "newarrival";

    /**
     * Create boolean query for search by tag value in given categories
     *
     * @param categories given categories (use specific categories)
     * @param shop given shop (optimisation if no specific cat is required)
     * @param newArrival start of new arrivals
     * @return boolean query to perform search.
     */
    public BooleanQuery createQuery(final List<Long> categories, final long shop, final Date newArrival) {

        final SimpleDateFormat toMinutes = new SimpleDateFormat("yyyyMMdd0000");
        final String fromDate = toMinutes.format(newArrival);

        BooleanQuery query = new BooleanQuery();
        if (CollectionUtils.isEmpty(categories)) {

            BooleanQuery currentQuery = new BooleanQuery();

            currentQuery.add(new TermQuery(new Term(PRODUCT_SHOP_FIELD, String.valueOf(shop))),
                    BooleanClause.Occur.MUST);

            final TermQuery tag = new TermQuery(new Term(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL));
            tag.setBoost(1.5f); // more boost for direct tag
            currentQuery.add(tag, BooleanClause.Occur.SHOULD);

            final TermRangeQuery time = new TermRangeQuery(PRODUCT_CREATED_FIELD, fromDate, null, true, true);
            // time.setBoost(1f); - this will be applied to all items so no boost
            currentQuery.add(time, BooleanClause.Occur.SHOULD);

            query.add(currentQuery, BooleanClause.Occur.SHOULD);

        } else {
            for (Long category : categories) {
                BooleanQuery currentQuery = new BooleanQuery();

                currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                        BooleanClause.Occur.MUST);

                final TermQuery tag = new TermQuery(new Term(PRODUCT_TAG_FIELD, TAG_NEWARRIVAL));
                tag.setBoost(1.5f);  // more boost for direct tag
                currentQuery.add(tag, BooleanClause.Occur.SHOULD);

                final TermRangeQuery time = new TermRangeQuery(PRODUCT_CREATED_FIELD, fromDate, null, true, true);
                // time.setBoost(1f); - this will be applied to all items so no boost
                currentQuery.add(time, BooleanClause.Occur.SHOULD);


                query.add(currentQuery, BooleanClause.Occur.SHOULD);
            }

        }
        return query;
    }

    /**
     * Create boolean query for search by tag value in given categories
     *
     * @param categories given categories
     * @param shop given shop (optimisation if no specific cat is required)
     * @param tagvalue   singe tag value
     * @return boolean query to perform search.
     */
    public BooleanQuery createQuery(final List<Long> categories, final long shop, final String tagvalue) {
        BooleanQuery query = new BooleanQuery();
        if (StringUtils.isNotBlank(tagvalue)) {
            if (CollectionUtils.isEmpty(categories)) {

                BooleanQuery currentQuery = new BooleanQuery();

                currentQuery.add(new TermQuery(new Term(PRODUCT_SHOP_FIELD, String.valueOf(shop))),
                        BooleanClause.Occur.MUST);

                currentQuery.add(new TermQuery(new Term(PRODUCT_TAG_FIELD, tagvalue)),
                        BooleanClause.Occur.MUST
                );
                query.add(currentQuery, BooleanClause.Occur.SHOULD);

            } else {

                for (Long category : categories) {

                    BooleanQuery currentQuery = new BooleanQuery();
                    currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                            BooleanClause.Occur.MUST
                    );
                    currentQuery.add(new TermQuery(new Term(PRODUCT_TAG_FIELD, tagvalue)),
                            BooleanClause.Occur.MUST
                    );
                    query.add(currentQuery, BooleanClause.Occur.SHOULD);
                }

            }
        }
        return query;
    }


}
