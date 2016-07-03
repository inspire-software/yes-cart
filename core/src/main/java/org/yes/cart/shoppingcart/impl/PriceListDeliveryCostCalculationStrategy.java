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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class PriceListDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    private static final BigDecimal SINGLE = new BigDecimal(1).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal MULTI = new BigDecimal(2).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private static final Total ZERO_TOTAL = new TotalImpl();

    private final CarrierSlaService carrierSlaService;
    private final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver;
    private final PricingPolicyProvider pricingPolicyProvider;

    public PriceListDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService,
                                                    final PricingPolicyProvider pricingPolicyProvider,
                                                    final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver) {
        this.carrierSlaService = carrierSlaService;
        this.deliveryCostRegionalPriceResolver = deliveryCostRegionalPriceResolver;
        this.pricingPolicyProvider = pricingPolicyProvider;
    }

    /** {@inheritDoc} */
    public Total calculate(final MutableShoppingCart cart) {

        cart.removeShipping();

        if (cart.getCarrierSlaId() != null) {
            final CarrierSla carrierSla = carrierSlaService.getById(cart.getCarrierSlaId());
            if (carrierSla != null && CarrierSla.FIXED.equals(carrierSla.getSlaType())) {

                final String carrierSlaId = carrierSla.getGuid();

                final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                        cart.getShoppingContext().getShopCode(), cart.getCurrencyCode(), cart.getCustomerEmail(),
                        cart.getShoppingContext().getCountryCode(),
                        cart.getShoppingContext().getStateCode()
                );

                final BigDecimal qty = cart.getOrderInfo().isMultipleDelivery() ? MULTI : SINGLE;

                final SkuPrice price = getSkuPrice(cart, carrierSlaId, policy, qty);

                if (price != null && price.getSkuPriceId() > 0L) {

                    final BigDecimal salePrice = MoneyUtils.minPositive(price.getRegularPrice(), price.getSalePriceForCalculation());

                    cart.addShippingToCart(carrierSlaId, qty);
                    cart.setShippingPrice(carrierSlaId, salePrice, salePrice);
                    final BigDecimal deliveryCost = salePrice.multiply(qty).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);

                    return new TotalImpl(
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            false,
                            null,
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost,
                            false,
                            null,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost
                    );

                }

            }
        }
        return ZERO_TOTAL;
    }

    protected SkuPrice getSkuPrice(final MutableShoppingCart cart, final String carrierSlaId, final PricingPolicyProvider.PricingPolicy policy, final BigDecimal qty) {

        return deliveryCostRegionalPriceResolver.getSkuPrice(cart, carrierSlaId, policy, qty);

    }

}
