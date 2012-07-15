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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.util.MoneyUtils;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 26, 2011
 * Time: 2:54:21 PM
 */
public class SkuPriceMatcher implements DtoToEntityMatcher<SkuPriceDTO, SkuPrice> {

    /**
     * {@inheritDoc}
     */
    public boolean match(final SkuPriceDTO priceDTO, final SkuPrice skuPrice) {

        return priceDTO != null && skuPrice != null
                && priceDTO.getCurrency() != null && skuPrice.getCurrency() != null
                && priceDTO.getCurrency().equals(skuPrice.getCurrency())
                && MoneyUtils.isFirstEqualToSecond(priceDTO.getQuantityTier(), skuPrice.getQuantity());
    }
}
