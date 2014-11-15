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

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/11/2014
 * Time: 18:40
 */
public class ProductsInShopQueryBuilderImpl implements ProductSearchQueryBuilder {


    /**
     * Create boolean query for given shop
     * @param shopId given shop id
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final Long shopId) {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(
                        new TermQuery( new Term(PRODUCT_SHOP_FIELD, shopId.toString())),
                        BooleanClause.Occur.MUST
                );
        return booleanQuery;
    }


}
