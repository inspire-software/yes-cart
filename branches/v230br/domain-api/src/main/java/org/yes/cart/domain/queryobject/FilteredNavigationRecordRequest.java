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

package org.yes.cart.domain.queryobject;

import org.yes.cart.domain.misc.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This is configuration for desired FilteredNavigationRecord data.
 *
 * User: denispavlov
 * Date: 05/12/2014
 * Time: 12:19
 */
public interface FilteredNavigationRecordRequest extends Serializable {

    /**
     * Facet name by which this group of records should be identified.
     *
     * @return facet name
     */
    String getFacetName();

    /**
     * Full text index field on which to collect counts.
     *
     * @return field name
     */
    String getField();

    /**
     * True if value of this field in index is multi value.
     * For example if a product has several SKU with different COLOR then
     * this will be a multi value field on product.
     *
     * @return multi value flag
     */
    boolean isMultiValue();

    /**
     * True if value of this field should be grouped into buckets.
     * For example price navigation will be grouped in ranges.
     *
     * @return range flag
     */
    boolean isRangeValue();

    /**
     * If this is a range value facet then this list of pairs specified then
     * list of buckets with pair.first as lo and pair.second as hi value.
     *
     * @return bucket ranges
     */
    List<Pair<String, String>> getRangeValues();

}
