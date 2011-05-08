package org.yes.cart.domain.query.impl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

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

}
