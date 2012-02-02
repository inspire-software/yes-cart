package org.yes.cart.domain.query.impl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TermQuery;
import org.yes.cart.domain.entity.bridge.SkuPriceBridge;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * Builds the range by price query for product in partucular categories and shop.
 * Need shop, because proces depends from shop.
 *
 * Unfortunately hibernate search not base on lucene 2.9 hence NumericRangeQuery can be used.
 * This http://wiki.apache.org/lucene-java/SearchNumericalFields article will lead you
 * why and how price range query build is implemented.
 *
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * <p/>

 *
 */
public class PriceSearchQueryBuilderImpl implements ProductSearchQueryBuilder {


    /**
     * Creates a boolean query for set of categories
     *
     * @param categories set of categorires
     * @param shopId shop id
     * @param currencyCore the currency 3 chars code
     * @param lowBorder low price inclusive in usual money format
     * @param hiBorder high price border in usual money format
     * @return constructed BooleanQuery
     */
    public BooleanQuery createQuery(final List<Long> categories,
                                    final Long shopId,
                                    final String currencyCore,
                                    final BigDecimal lowBorder,
                                    final BigDecimal hiBorder) {

        SkuPriceBridge skuPriceBridge = new SkuPriceBridge();

        final BooleanQuery booleanQuery = new BooleanQuery();

        for (Long category : categories) {
            BooleanQuery currentQuery = new BooleanQuery();
            currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                    BooleanClause.Occur.MUST);

            currentQuery.add(new TermRangeQuery(
                    PRODUCT_SKU_PRICE,
                    skuPriceBridge.objectToString(shopId, currencyCore, lowBorder),
                    skuPriceBridge.objectToString(shopId, currencyCore, hiBorder),
                    true,
                    true),
                    BooleanClause.Occur.MUST);

             booleanQuery.add(currentQuery, BooleanClause.Occur.SHOULD);

        }
        return booleanQuery;
    }


}
