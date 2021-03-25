/*
 * Copyright 2009 Inspire-Software.com
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.*;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-29
 * Time: 8:15 AM
 */
public class PromotionContextFactoryImpl implements PromotionContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionContextFactoryImpl.class);

    private final ShopService shopService;
    private final PromotionService promotionService;
    private final PromotionConditionParser promotionConditionParser;
    private final Map<String, Map<String, PromotionAction>> promotionActionMap;
    private final PromotionApplicationStrategy strategy;
    private final PromotionConditionSupport conditionSupport;
    private final PricingPolicyProvider pricingPolicyProvider;

    public PromotionContextFactoryImpl(final ShopService shopService,
                                       final PromotionService promotionService,
                                       final PromotionConditionParser promotionConditionParser,
                                       final LinkedHashMapBean<String, Map<String, PromotionAction>> promotionActionMap,
                                       final PromotionApplicationStrategy strategy,
                                       final PromotionConditionSupport conditionSupport,
                                       final PricingPolicyProvider pricingPolicyProvider) {
        this.shopService = shopService;
        this.promotionService = promotionService;
        this.promotionConditionParser = promotionConditionParser;
        this.promotionActionMap = promotionActionMap;
        this.strategy = strategy;
        this.conditionSupport = conditionSupport;
        this.pricingPolicyProvider = pricingPolicyProvider;
    }

    /** {@inheritDoc} */
    @Override
    public PromotionContext getInstance(final String shopCode, final String currency) {

        final PromotionContextImpl ctx = new PromotionContextImpl(shopCode, currency, strategy, conditionSupport, pricingPolicyProvider);

        final Shop currentShop = shopService.getShopByCode(shopCode);
        if (currentShop != null) {
            final List<Promotion> active = new ArrayList<>(promotionService.getPromotionsByShopCode(shopCode, currency, true));
            if (currentShop.getMaster() != null && !currentShop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PROMOTIONS)) {
                active.addAll(promotionService.getPromotionsByShopCode(currentShop.getMaster().getCode(), currency, true));
            }

            for (final Promotion promotion : active) {
                final PromotionCondition condition = promotionConditionParser.parse(promotion);
                final Map<String, PromotionAction> actionByTypeMap = promotionActionMap.get(promotion.getPromoType());
                if (actionByTypeMap != null) {
                    final PromotionAction action = actionByTypeMap.get(promotion.getPromoAction());
                    if (action != null) {
                        ctx.addPromotion(promotion, condition, action);
                    } else {
                        LOG.warn(
                                "No action mapping for promotion: {}, type: {}, action {}",
                                promotion.getCode(), promotion.getPromoType(), promotion.getPromoAction());
                    }
                } else {
                    LOG.warn(
                            "No action mapping for promotion: {}, type: {}", promotion.getCode(), promotion.getPromoType());
                }
            }
        }
        return ctx;
    }

    /** {@inheritDoc} */
    @Override
    public void refresh(final String shopCode, final String currency) {
        // not supported
    }
    
}
