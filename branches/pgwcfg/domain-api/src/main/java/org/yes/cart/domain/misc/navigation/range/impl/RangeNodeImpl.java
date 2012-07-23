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

package org.yes.cart.domain.misc.navigation.range.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.RangeNode;

/**
 * Value range implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class RangeNodeImpl implements RangeNode {

    private Pair<String, String> range;

    /**
     * {@inheritDoc}
     */
    public Pair<String, String> getRange() {
        return range;
    }

    /**
     * {@inheritDoc}
     */
    public void setRange(final Pair<String, String> range) {
        this.range = range;
    }

    /**
     * Construct value range. Default constructor
     *
     * @param range value range.
     */
    public RangeNodeImpl() {
	super();
    }

    /**
     * Construct value range.
     *
     * @param range value range.
     */
    public RangeNodeImpl(final Pair<String, String> range) {
        this.range = range;
    }

    /**
     * Construct value range.
     *
     * @param loValue lo value of range
     * @param hiValue hi value of range
     */
    public RangeNodeImpl(final String loValue, final String hiValue) {
        range = new Pair<String, String>(loValue, hiValue);
    }
}
