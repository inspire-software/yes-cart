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

package org.yes.cart.domain.queryobject.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/12/2014
 * Time: 12:27
 */
public class FilteredNavigationRecordRequestImpl implements FilteredNavigationRecordRequest {

    private final String facetName;
    private final String field;
    private final boolean multiValue;
    private final boolean rangeValue;
    private final List<Pair<String, String>> rangeValues;


    /**
     * Discrete value facet.
     *
     * @param facetName facet name
     * @param field     facet field
     */
    public FilteredNavigationRecordRequestImpl(final String facetName,
                                               final String field) {
        this(facetName, field, false, null);
    }

    /**
     * Discrete value facet.
     *
     * @param facetName  facet name
     * @param field      facet field
     * @param multiValue multi value flag
     */
    public FilteredNavigationRecordRequestImpl(final String facetName,
                                               final String field,
                                               final boolean multiValue) {
        this(facetName, field, multiValue, null);
    }

    /**
     * Range value facet.
     *
     * @param facetName   facet name
     * @param field       facet field
     * @param rangeValues bucket ranges
     */
    public FilteredNavigationRecordRequestImpl(final String facetName,
                                               final String field,
                                               final List<Pair<String, String>> rangeValues) {
        this(facetName, field, false, rangeValues);
    }


    /**
     * Range value facet.
     *
     * @param facetName   facet name
     * @param field       facet field
     * @param multiValue  multi value flag
     * @param rangeValues bucket ranges
     */
    public FilteredNavigationRecordRequestImpl(final String facetName,
                                               final String field,
                                               final boolean multiValue,
                                               final List<Pair<String, String>> rangeValues) {
        this.facetName = facetName;
        this.field = field;
        this.multiValue = multiValue;
        this.rangeValue = rangeValues != null && !rangeValues.isEmpty();
        this.rangeValues = rangeValues;
    }

    /**
     * {@inheritDoc}
     */
    public String getFacetName() {
        return facetName;
    }

    /**
     * {@inheritDoc}
     */
    public String getField() {
        return field;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMultiValue() {
        return multiValue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRangeValue() {
        return rangeValue;
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, String>> getRangeValues() {
        return rangeValues;
    }

    @Override
    public String toString() {
        return "FilteredNavigationRecordRequestImpl{" +
                "facetName='" + facetName + '\'' +
                ", field='" + field + '\'' +
                ", multiValue=" + multiValue +
                ", rangeValue=" + rangeValue +
                ", rangeValues=" + rangeValues +
                '}';
    }
}
