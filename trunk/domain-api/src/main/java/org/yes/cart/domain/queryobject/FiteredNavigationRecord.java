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

import org.yes.cart.domain.entity.Rankable;

/**
 * Filtered navigation record.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface FiteredNavigationRecord extends Rankable {

    /**
     * Get attribute code.
     *
     * @return attribute code.
     */
    String getCode();

    /**
     * Set attribute code.
     *
     * @param code attribute code.
     */
    void setCode(String code);

    /**
     * Get attribute value, value depends from type: S - single value, R - range of values, separated by hyphen.
     * <p/>
     * Range example: Code -> Author, Value -> (from)Akimoto-(to)Matahari
     *
     * @return attribute code.
     */
    String getValue();

    /**
     * Set value.
     *
     * @param value value to use.
     */
    void setValue(String value);

    /**
     * Get attribute display value
     *
     * @return attribute display value.
     */
    String getDisplayValue();

    /**
     * Set display value.
     *
     * @param displayValue display value to use.
     */
    void setDisplayValue(String displayValue);

    /**
     * Get the count of object, that mach the navigation record. Not set by productService, but by lucene.
     *
     * @return count of object.
     */
    int getCount();


    /**
     * Set count of object, that mach the navigation record.
     *
     * @param itemsCount item count.
     */
    void setCount(int itemsCount);

    /**
     * Set name of attribute.
     *
     * @param name name of attribute.
     */
    void setName(String name);

    /**
     * Get the name of attribute.
     *
     * @return name of attribute.
     */
    String getName();

    /**
     * Rank of attribute record
     *
     * @return rank
     */
    int getRank();

    /**
     * Set rank of atribute record
     *
     * @param rank rank
     */
    void setRank(int rank);

    /**
     * Get type S - single value, R range value
     *
     * @return navigation type
     */
    String getType();


    /**
     * Set the navigation record  type.
     *
     * @param type type of navigation record
     */
    void setType(final String type);

}
