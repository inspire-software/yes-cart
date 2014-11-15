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
import org.apache.lucene.search.TermRangeQuery;
import org.springframework.util.CollectionUtils;
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
                                    final long shopId,
                                    final String currencyCore,
                                    final BigDecimal lowBorder,
                                    final BigDecimal hiBorder) {

        SkuPriceBridge skuPriceBridge = new SkuPriceBridge();

        final BooleanQuery booleanQuery = new BooleanQuery();

        if (CollectionUtils.isEmpty(categories)) {
            BooleanQuery currentQuery = new BooleanQuery();
            currentQuery.add(new TermQuery(new Term(PRODUCT_SHOP_FIELD, String.valueOf(shopId))),
                    BooleanClause.Occur.MUST);

            final TermRangeQuery price = new TermRangeQuery(
                    PRODUCT_SKU_PRICE,
                    skuPriceBridge.objectToString(shopId, currencyCore, lowBorder),
                    skuPriceBridge.objectToString(shopId, currencyCore, hiBorder),
                    true,
                    true);
            // price.setBoost(3f); - no need for boost since this will be applied to all products in result
            currentQuery.add(price, BooleanClause.Occur.MUST);

            booleanQuery.add(currentQuery, BooleanClause.Occur.SHOULD);
        } else {
            for (Long category : categories) {
                BooleanQuery currentQuery = new BooleanQuery();
                currentQuery.add(new TermQuery(new Term(PRODUCT_CATEGORY_FIELD, category.toString())),
                        BooleanClause.Occur.MUST);

                final TermRangeQuery price = new TermRangeQuery(
                        PRODUCT_SKU_PRICE,
                        skuPriceBridge.objectToString(shopId, currencyCore, lowBorder),
                        skuPriceBridge.objectToString(shopId, currencyCore, hiBorder),
                        true,
                        true);
                // price.setBoost(3f); - no need for boost since this will be applied to all products in result
                currentQuery.add(price, BooleanClause.Occur.MUST);

                booleanQuery.add(currentQuery, BooleanClause.Occur.SHOULD);

            }
        }
        return booleanQuery;
    }


}
