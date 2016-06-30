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
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class FreeDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private static final BigDecimal SINGLE = new BigDecimal(1).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal MULTI = new BigDecimal(2).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private static final Total ZERO_TOTAL = new TotalImpl();

    private final CarrierSlaService carrierSlaService;

    public FreeDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    public Total calculate(final MutableShoppingCart cart) {

        cart.removeShipping();

        if (cart.getCarrierSlaId() != null) {
            final CarrierSla carrierSla = carrierSlaService.getById(cart.getCarrierSlaId());
            if (carrierSla != null && CarrierSla.FREE.equals(carrierSla.getSlaType())) {

                final String carrierSlaId = carrierSla.getGuid();
                final BigDecimal listPrice = ZERO;
                final BigDecimal qty = cart.getOrderInfo().isMultipleDelivery() ? MULTI : SINGLE;
                cart.addShippingToCart(carrierSlaId, qty);
                cart.setShippingPrice(carrierSlaId, listPrice, listPrice);
                final BigDecimal deliveryListCost = ZERO;

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
                        deliveryListCost,
                        deliveryListCost,
                        false,
                        null,
                        Total.ZERO,
                        deliveryListCost,
                        deliveryListCost,
                        Total.ZERO,
                        deliveryListCost,
                        deliveryListCost
                );
            }
        }
        return ZERO_TOTAL;
    }

}
