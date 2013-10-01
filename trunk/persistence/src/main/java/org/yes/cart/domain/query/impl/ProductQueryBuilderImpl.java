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
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class ProductQueryBuilderImpl implements ProductSearchQueryBuilder {
    /**
     * Create boolean query for given category
     * @param productId given category id
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final String productId) {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(
                        new TermQuery( new Term(PRODUCT_ID_FIELD, productId.trim())),
                        BooleanClause.Occur.MUST
                );
        return booleanQuery;
    }


    /**
     * Create boolean query for given category
     * @param productIds given product ids
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final Collection<String> productIds) {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (final String productId : productIds) {
            booleanQuery.add(
                    new TermQuery( new Term(PRODUCT_ID_FIELD, productId.trim())),
                    BooleanClause.Occur.SHOULD
            );
        }
        return booleanQuery;
    }


}
