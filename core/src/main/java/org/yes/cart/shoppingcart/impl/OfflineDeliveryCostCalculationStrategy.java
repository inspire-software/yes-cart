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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.DeliveryCostRegionalPriceResolver;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;

import java.math.BigDecimal;

/**
 * Offline calculation strategy adds promotion marker "#OFFLINE#" to the price to indicate that price will be calculated
 * after placing the order separately.
 */
public class OfflineDeliveryCostCalculationStrategy extends FreeDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    public OfflineDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService,
                                                  final PricingPolicyProvider pricingPolicyProvider,
                                                  final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver) {
        super(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver);
    }

    @Override
    protected boolean isCarrierSlaTypeApplicable(final CarrierSla carrierSla) {
        return CarrierSla.OFFLINE.equals(carrierSla.getSlaType());
    }

    @Override
    protected void setShippingBucketCost(final MutableShoppingCart cart, final DeliveryBucket bucket, final String carrierSlaGUID, final String carrierSlaName, final BigDecimal qty, final BigDecimal listPrice) {
        super.setShippingBucketCost(cart, bucket, carrierSlaGUID, carrierSlaName, qty, listPrice);
        cart.setShippingPromotion(carrierSlaGUID, bucket, listPrice, "#OFFLINE#");
    }
}
