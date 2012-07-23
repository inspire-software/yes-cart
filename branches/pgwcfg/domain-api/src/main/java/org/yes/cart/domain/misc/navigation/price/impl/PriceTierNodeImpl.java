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

package org.yes.cart.domain.misc.navigation.price.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class PriceTierNodeImpl implements PriceTierNode {

    private Pair<BigDecimal, BigDecimal> priceRange;

    private List<PriceTierNode> priceSubRange;

    /**
     * Defualt PriceTierNodeImpl constructor. 
     */
    public PriceTierNodeImpl() {
        super();
    }

    public Pair<BigDecimal, BigDecimal> getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(final Pair<BigDecimal, BigDecimal> priceRange) {
        this.priceRange = priceRange;
    }

    public List<PriceTierNode> getPriceSubRange() {
        return priceSubRange;
    }

    public void setPriceSubRange(final List<PriceTierNode> priceSubRange) {
        this.priceSubRange = priceSubRange;
    }

    public PriceTierNodeImpl(final Pair<BigDecimal, BigDecimal> priceRange) {
        this.priceRange = priceRange;
    }

    public PriceTierNodeImpl(final BigDecimal bottomBorder, final BigDecimal topBorder) {
        this.priceRange = new Pair<BigDecimal, BigDecimal>(bottomBorder, topBorder);
    }
}
