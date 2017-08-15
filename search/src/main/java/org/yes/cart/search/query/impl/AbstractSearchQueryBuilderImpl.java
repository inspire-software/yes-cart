/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.search.query.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.yes.cart.search.query.SearchQueryBuilder;

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
     * @param maxEdits max number of mismatches (between 0 <= X <= 2, where 0 is exact)
     *
     * @return fuzzy query
     */
    protected Query createFuzzyQuery(final String field, final String value, final int maxEdits) {
        // If the search value is less than 3 char then fuzzy does not make sense
        if (maxEdits <= 0 || value.length() < 3) {
            return new TermQuery(new Term(field, value));
        }
        // 2 edits is the maximum supported in Lucene 6.5.x
        // allow 2 only for more than 5 char words (otherwise too many matches)
        if (maxEdits >= 2 && value.length() > 4) {
            return new FuzzyQuery(new Term(field, value), 2);
        }
        // allow only 1 if under 5
        return new FuzzyQuery(new Term(field, value), 1);
    }

    /**
     * Create fuzzy query.
     *
     * @param field field name
     * @param value value
     * @param maxEdits max number of mismatches (between 0 <= X <= 2, where 0 is exact)
     * @param boost importance of this criteria (default 1.0f)
     *
     * @return fuzzy query with boost
     */
    protected Query createFuzzyQuery(final String field, final String value, final int maxEdits, final float boost) {
        final Query query = createFuzzyQuery(field, value, maxEdits);
        return new BoostQuery(query, boost);
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
        return new BoostQuery(query, boost);
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
        return TermRangeQuery.newStringRange(field, low, high, true, false);
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
    protected Query createRangeQuery(final String field, final Long low, final Long high) {
        return LongPoint.newRangeQuery(field, low != null ? low : Long.MIN_VALUE, high != null ? Math.addExact(high, -1) : Long.MAX_VALUE);
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
    protected Query createRangeQuery(final String field, final Long low, final Long high, final float boost) {
        final Query query = createRangeQuery(field, low, high);
        return new BoostQuery(query, boost);
    }

    /**
     * Create range query.
     *
     * @param field field name
     * @param value value
     *
     * @return range query
     */
    protected Query createNumericQuery(final String field, final long value) {
        return LongPoint.newExactQuery(field, value);
    }

    /**
     * Create range query.
     *
     * @param field field name
     * @param value value
     * @param boost importance of this criteria (default 1.0f)
     *
     * @return range query with boost
     */
    protected Query createNumericQuery(final String field, final long value, final float boost) {
        final Query query = createNumericQuery(field, value);
        return new BoostQuery(query, boost);
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
        return String.valueOf(value);
    }


}
