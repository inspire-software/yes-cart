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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:16
 */
public class ProductBrandSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder {

    /**
     * {@inheritDoc}
     */
    public Query createStrictQuery(final long shopId, final String parameter, final Object value) {

        if (value instanceof Collection) {
            final Collection<String> brands = (Collection) value;
            if (brands.size() > 1) {
                final BooleanQuery aggregatedQuery = new BooleanQuery();
                for (final String brand : brands) {
                    aggregatedQuery.add(createTermQuery(BRAND_FIELD, escapeValue(brand.toLowerCase()), 3.5f), BooleanClause.Occur.SHOULD);
                }
                return aggregatedQuery;
            } else if (brands.size() > 1) {
                return createTermQuery(BRAND_FIELD, escapeValue(brands.iterator().next().toLowerCase()), 3.5f);
            } else {
                return null;
            }

        }

        if (isEmptyValue(value)) {
            return null;
        }

        final String searchValue = String.valueOf(value);

        if (searchValue.contains(Constants.RANGE_NAVIGATION_DELIMITER)) { // value range navigation
            final String[] brands = StringUtils.split(searchValue, Constants.RANGE_NAVIGATION_DELIMITER);
            if (brands.length > 1) {
                final BooleanQuery aggregatedQuery = new BooleanQuery();
                for (final String brand : brands) {
                    aggregatedQuery.add(createTermQuery(BRAND_FIELD, escapeValue(brand.toLowerCase()), 3.5f), BooleanClause.Occur.SHOULD);
                }
                return aggregatedQuery;
            } else if (brands.length == 1) {
                return createTermQuery(BRAND_FIELD, escapeValue(brands[0].toLowerCase()), 3.5f);
            } else {
                return null;
            }
        }
        return createTermQuery(BRAND_FIELD, escapeValue(searchValue.toLowerCase()), 3.5f);
    }

    /**
     * {@inheritDoc}
     */
    public Query createRelaxedQuery(final long shopId, final String parameter, final Object value) {

        if (value instanceof Collection) {
            final Collection<String> brands = (Collection) value;
            if (brands.size() > 1) {
                final BooleanQuery aggregatedQuery = new BooleanQuery();
                for (final String brand : brands) {
                    aggregatedQuery.add(createFuzzyQuery(BRAND_FIELD, escapeValue(brand.toLowerCase()), 0.7f, 2.5f), BooleanClause.Occur.SHOULD);
                }
                return aggregatedQuery;
            } else if (brands.size() == 1) {
                return createFuzzyQuery(BRAND_FIELD, escapeValue(brands.iterator().next().toLowerCase()), 0.7f, 2.5f);
            } else {
                return null;
            }

        }

        if (isEmptyValue(value)) {
            return null;
        }

        final String searchValue = String.valueOf(value);

        if (searchValue.contains(Constants.RANGE_NAVIGATION_DELIMITER)) { // value range navigation
            final String[] brands = StringUtils.split(searchValue, Constants.RANGE_NAVIGATION_DELIMITER);
            if (brands.length > 1) {
                final BooleanQuery aggregatedQuery = new BooleanQuery();
                for (final String brand : brands) {
                    aggregatedQuery.add(createFuzzyQuery(BRAND_FIELD, escapeValue(brand.toLowerCase()), 0.7f, 2.5f), BooleanClause.Occur.SHOULD);
                }
                return aggregatedQuery;
            } else if (brands.length == 1) {
                return createFuzzyQuery(BRAND_FIELD, escapeValue(brands[0].toLowerCase()), 0.7f, 2.5f);
            } else {
                return null;
            }
        }
        return createFuzzyQuery(BRAND_FIELD, escapeValue(searchValue.toLowerCase()), 0.7f, 2.5f);
    }
}
