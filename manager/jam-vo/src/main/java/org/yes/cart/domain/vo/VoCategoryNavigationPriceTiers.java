/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: denispavlov
 * Date: 30/08/2016
 * Time: 11:59
 */
public class VoCategoryNavigationPriceTiers {

    private List<MutablePair<String, List<VoCategoryNavigationPriceTier>>> tiers;

    public List<MutablePair<String, List<VoCategoryNavigationPriceTier>>> getTiers() {
        return tiers;
    }

    public void setTiers(final List<MutablePair<String, List<VoCategoryNavigationPriceTier>>> tiers) {
        this.tiers = tiers;
    }
}
