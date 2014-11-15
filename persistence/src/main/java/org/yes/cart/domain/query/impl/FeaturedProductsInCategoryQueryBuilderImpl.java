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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 9:43 AM
 */
public class FeaturedProductsInCategoryQueryBuilderImpl extends ProductsInCategoryQueryBuilderImpl {

    /**
     * Enhancement of in category query by adding featured="true" clause
     *
     * @param categories set of categories
     * @param shop given shop (optimisation if no specific cat is required)
     *
     * @return featured products in categories query
     */
    @Override
    public BooleanQuery createQuery(final List<Long> categories, final long shop) {

        BooleanQuery inCategories = super.createQuery(categories, shop);

        inCategories.add(new TermQuery(new Term(PRODUCT_FEATURED_FIELD, Boolean.TRUE.toString())),
                BooleanClause.Occur.MUST);

        return inCategories;
    }

    /**
     * Enhancement of in category query by adding featured="true" clause
     *
     * @param categoryId given category id
     *
     * @return featured products in category query
     */
    @Override
    public BooleanQuery createQuery(final Long categoryId) {

        BooleanQuery booleanQuery = new BooleanQuery();

        BooleanQuery inCategories = super.createQuery(categoryId);

        booleanQuery.add(inCategories, BooleanClause.Occur.MUST);
        booleanQuery.add(new TermQuery(new Term(PRODUCT_FEATURED_FIELD, Boolean.TRUE.toString())),
                BooleanClause.Occur.MUST);

        return booleanQuery;
    }
}
