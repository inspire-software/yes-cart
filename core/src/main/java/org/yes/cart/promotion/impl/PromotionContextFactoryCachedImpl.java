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

package org.yes.cart.promotion.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 15:22
 */
public class PromotionContextFactoryCachedImpl implements PromotionContextFactory {

    private final PromotionContextFactory factory;

    public PromotionContextFactoryCachedImpl(final PromotionContextFactory factory) {
        this.factory = factory;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "promotionService-factoryGetInstance")
    public PromotionContext getInstance(final String shopCode, final String currency) {
        return factory.getInstance(shopCode, currency);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "promotionService-factoryGetInstance"
    }, key = "#shopCode", condition = "#ensureNew == true")
    public PromotionContext getInstance(final String shopCode, final String currency, final boolean ensureNew) {
        return factory.getInstance(shopCode, currency, ensureNew);
    }

    /** {@inheritDoc} */
    public PromotionContext getInstance(final long shopId, final String currency) {
        return factory.getInstance(shopId, currency);
    }

    /** {@inheritDoc} */
    public PromotionContext getInstance(final long shopId, final String currency, final boolean ensureNew) {
        return factory.getInstance(shopId, currency, ensureNew);
    }
}
