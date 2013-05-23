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
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 *
 * Products by brand in particular categories query builder. Used in filtered navigation.
 *
 */
public class BrandSearchQueryBuilder implements ProductSearchQueryBuilder {

    /**
     * Create boolean query for search by brand name in given categories
     * @param categories given categories
     * @param brandName brand name
     * @return boolean query to perform search.
     */
    public BooleanQuery createQuery(final List<Long> categories, final String brandName) {
        BooleanQuery query = new BooleanQuery();
        if (StringUtils.isNotBlank(brandName) && categories != null) {
            for (Long category : categories) {
                BooleanQuery currentQuery = new BooleanQuery();
                currentQuery.add(new TermQuery( new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                                    BooleanClause.Occur.MUST
                            );
                currentQuery.add(new TermQuery( new Term(BRAND_FIELD, brandName.toLowerCase())),
                                    BooleanClause.Occur.MUST
                            );
                query.add(currentQuery, BooleanClause.Occur.SHOULD);
            }
        }
        return query;
    }

}
