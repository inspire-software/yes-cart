package org.yes.cart.domain.query.impl;

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
 * Simple products in category query builder.
 *
 */
public class ProductsInCategoryQueryBuilderImpl implements ProductSearchQueryBuilder {



    /**
     * Creates a boolean query for set of categories
     * @param categories set of categorires
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final List<Long> categories) {

        BooleanQuery booleanQuery = new BooleanQuery();

        if (categories != null && !categories.isEmpty()) {

            BooleanClause.Occur occur = categories.size() > 1 ? BooleanClause.Occur.SHOULD : BooleanClause.Occur.MUST;

            for (Long category : categories) {
                booleanQuery.add(
                                new TermQuery( new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                                occur
                        );
            }
            

        }


        return booleanQuery;

    }





    /**
     * Create boolean query for given category
     * @param categoryId given category id
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final Long categoryId) {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(
                        new TermQuery( new Term(PRODUCT_CATEGORY_FIELD, categoryId.toString())),
                        BooleanClause.Occur.MUST
                );
        return booleanQuery;
    }


}
