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

import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class PriceTierTreeImpl implements PriceTierTree {

    private Map<String, List<PriceTierNode>> priceMap;

    /**
     * {@inheritDoc}
     */
    public List<PriceTierNode> getPriceTierNodes(final String currency) {
        if (priceMap == null) {
            priceMap = new HashMap<String, List<PriceTierNode>>();
        }
        return priceMap.get(currency);
    }

    /**
     * {@inheritDoc}
     */
    public List<PriceTierNode> addPriceTierNode(final String currency, final List<PriceTierNode> priceTierNodes) {
        if (priceMap == null) {
            priceMap = new HashMap<String, List<PriceTierNode>>();
        }
        priceMap.put(currency, priceTierNodes);
        return priceTierNodes;
    }
}
