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

package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.SkuPrice;

import java.util.Comparator;

/**
 * User: denispavlov
 * Date: 29/05/2015
 * Time: 09:42
 */
public class SkuPricePairQuantityComparatorImpl implements Comparator<Pair<String, SkuPrice>> {
    /**
     * {@inheritDoc}
     */
    public int compare(final Pair<String, SkuPrice> skuPrice1, final Pair<String, SkuPrice> skuPrice2) {
        return skuPrice2.getSecond().getQuantity().compareTo(skuPrice1.getSecond().getQuantity());
    }
}
