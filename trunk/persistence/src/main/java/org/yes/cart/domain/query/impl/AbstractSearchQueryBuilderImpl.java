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
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.yes.cart.domain.query.SearchQueryBuilder;

/**
 * Template for all query builders.
 *
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 23:00
 */
public abstract class AbstractSearchQueryBuilderImpl implements SearchQueryBuilder {


    /**
     * Create fuzzy query.
     *
     * @param field field name
     * @param value value
     * @param minimumSimilarity similarity (e.g. 0.5f for 10 letter word requires 5 letter match)
     *
     * @return fuzzy query
     */
    protected Query createFuzzyQuery(final String field, final String value, final float minimumSimilarity) {
        return new FuzzyQuery(new Term(field, value), minimumSimilarity);
    }

    /**
     * Create fuzzy query.
     *
     * @param field field name
     * @param value value
     * @param minimumSimilarity similarity (e.g. 0.5f for 10 letter word requires 5 letter match)
     * @param boost importance of this criteria (default 1.0f)
     *
     * @return fuzzy query with boost
     */
    protected Query createFuzzyQuery(final String field, final String value, final float minimumSimilarity, final float boost) {
        final Query query = createFuzzyQuery(field, value, minimumSimilarity);
        query.setBoost(boost);
        return query;
    }

    /**
     * Create term query.
     *
     * @param field field name
     * @param value value
     *
     * @return term query
     */
    protected Query createTermQuery(final String field, final String value) {
        return new TermQuery(new Term(field, value));
    }

    /**
     * Create term query with boost.
     *
     * @param field field name
     * @param value value
     * @param boost importance of this criteria (default 1.0f)
     *
     * @return term query with boost
     */
    protected Query createTermQuery(final String field, final String value, final float boost) {
        final Query query = createTermQuery(field, value);
        query.setBoost(boost);
        return query;
    }

    /**
     * Create range query.
     *
     * @param field field name
     * @param low from value (inclusive)
     * @param high to value (exclusive)
     *
     * @return range query
     */
    protected Query createRangeQuery(final String field, final String low, final String high) {
        return new TermRangeQuery(field, low, high, true, false);
    }

    /**
     * Create range query.
     *
     * @param field field name
     * @param low from value (inclusive)
     * @param high to value (exclusive)
     * @param boost importance of this criteria (default 1.0f)
     *
     * @return range query with boost
     */
    protected Query createRangeQuery(final String field, final String low, final String high, final float boost) {
        final Query query = createRangeQuery(field, low, high);
        query.setBoost(boost);
        return query;
    }

    /**
     * Check if raw value is empty.
     *
     * @param rawValue raw
     *
     * @return true if string value representation is not blank
     */
    protected boolean isEmptyValue(Object rawValue) {
        return rawValue == null || StringUtils.isBlank(String.valueOf(rawValue));
    }

    /**
     * Escape raw value using Lucene standard escape utils.
     *
     * @param value unescaped value
     *
     * @return safe value
     */
    protected String escapeValue(String value) {
        // Looks like there is double escaping happening in hibernate search, so leave as is
        // return QueryParser.escape(value);
        return value;
    }

    /**
     * Escape raw value using Lucene standard escape utils.
     *
     * @param value unescaped value
     *
     * @return safe value
     */
    protected String escapeValue(Object value) {
        // Looks like there is double escaping happening in hibernate search, so leave as is
        // return QueryParser.escape(String.valueOf(value));
        return String.valueOf(value);
    }


}
