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
 * User: Denis Lozenko
 * Date: 11/10/14
 * Time: 12:15 PM
 */
public class ProductSkuCodeQueryBuilderImpl implements ProductSearchQueryBuilder {
    /**
     * Create boolean query for given products
     * @param skuCode given sku codes
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final String skuCode) {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(
                        new TermQuery( new Term(SKU_PRODUCT_CODE_FIELD, skuCode.trim())),
                        BooleanClause.Occur.MUST
                );
        return booleanQuery;
    }


    /**
     * Create boolean query for given products
     * @param skuCodes given sku codes
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final Collection<String> skuCodes) {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (final String skuCode : skuCodes) {
            booleanQuery.add(
                    new TermQuery( new Term(SKU_PRODUCT_CODE_FIELD, skuCode.trim())),
                    BooleanClause.Occur.SHOULD
            );
        }
        return booleanQuery;
    }


}
