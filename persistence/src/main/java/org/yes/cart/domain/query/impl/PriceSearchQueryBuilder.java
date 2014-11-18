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
import org.apache.lucene.search.Query;
import org.yes.cart.domain.entity.bridge.SkuPriceBridge;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 00:21
 */
public class PriceSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    private final PriceNavigation priceNavigation;
    private final SkuPriceBridge skuPriceBridge = new SkuPriceBridge();


    public PriceSearchQueryBuilder(final PriceNavigation priceNavigation) {
        this.priceNavigation = priceNavigation;
    }

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        final String searchValue = String.valueOf(value);
        if (value == null || StringUtils.isBlank(searchValue)) {
            return null;
        }

        final Pair<String, Pair<BigDecimal, BigDecimal>> priceParams =
                priceNavigation.decomposePriceRequestParams(searchValue);

        return createRangeQuery(PRODUCT_SKU_PRICE,
                skuPriceBridge.objectToString(shopId, priceParams.getFirst(), priceParams.getSecond().getFirst()),
                skuPriceBridge.objectToString(shopId, priceParams.getFirst(), priceParams.getSecond().getSecond()));
    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {
        return createStrictQuery(shopId, parameter, value);
    }
}
