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

package org.yes.cart.promotion.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.*;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-29
 * Time: 8:15 AM
 */
public class PromotionContextFactoryImpl implements PromotionContextFactory {

    private final ShopService shopService;
    private final PromotionService promotionService;
    private final PromotionConditionParser promotionConditionParser;
    private final Map<String, Map<String, PromotionAction>> promotionActionMap;
    private final PromotionApplicationStrategy strategy;

    public PromotionContextFactoryImpl(final ShopService shopService,
                                       final PromotionService promotionService,
                                       final PromotionConditionParser promotionConditionParser,
                                       final Map<String, Map<String, PromotionAction>> promotionActionMap,
                                       final PromotionApplicationStrategy strategy) {
        this.shopService = shopService;
        this.promotionService = promotionService;
        this.promotionConditionParser = promotionConditionParser;
        this.promotionActionMap = promotionActionMap;
        this.strategy = strategy;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "promotionService-factoryGetInstance")
    public PromotionContext getInstance(final String shopCode, final String currency) {

        final PromotionContextImpl ctx = new PromotionContextImpl(shopCode, strategy);
        final List<Promotion> active = promotionService.getPromotionsByShopCode(shopCode, currency, true);
        for (final Promotion promotion : active) {
            final PromotionCondition condition = promotionConditionParser.parse(promotion);
            final Map<String, PromotionAction> actionByTypeMap = promotionActionMap.get(promotion.getPromoType());
            if (actionByTypeMap != null) {
                final PromotionAction action = actionByTypeMap.get(promotion.getPromoAction());
                if (action != null) {
                    ctx.addPromotion(promotion, condition, action);
                } else {
                    ShopCodeContext.getLog(this).warn(
                            "No action mapping for promotion: {}, type: {}, action {}",
                            new Object[] { promotion.getCode(), promotion.getPromoType(), promotion.getPromoAction() });
                }
            } else {
                ShopCodeContext.getLog(this).warn(
                        "No action mapping for promotion: {}, type: {}", promotion.getCode(), promotion.getPromoType());
            }
        }
        return ctx;
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "promotionService-factoryGetInstance"
    }, key = "#shopCode", condition = "#ensureNew == true")
    public PromotionContext getInstance(final String shopCode, final String currency, final boolean ensureNew) {
        return proxy().getInstance(shopCode, currency);
    }

    /** {@inheritDoc} */
    public PromotionContext getInstance(final long shopId, final String currency) {
        final Shop shop = shopService.getById(shopId);
        return proxy().getInstance(shop.getCode(), currency);
    }

    /** {@inheritDoc} */
    public PromotionContext getInstance(final long shopId, final String currency, final boolean ensureNew) {
        final Shop shop = shopService.getById(shopId);
        return getInstance(shop.getCode(), currency, ensureNew);
    }

    private PromotionContextFactory proxy;

    private PromotionContextFactory proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * Spring IoC.
     *
     * @return self
     */
    public PromotionContextFactory getSelf() {
        return null;
    }
}
