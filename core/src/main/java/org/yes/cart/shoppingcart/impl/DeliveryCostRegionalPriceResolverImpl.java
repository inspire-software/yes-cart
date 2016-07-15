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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class DeliveryCostRegionalPriceResolverImpl implements DeliveryCostRegionalPriceResolver {

    private final PriceService priceService;

    public DeliveryCostRegionalPriceResolverImpl(final PriceService priceService) {
        this.priceService = priceService;
    }


    /**
     * {@inheritDoc}
     */
    public SkuPrice getSkuPrice(final ShoppingCart cart, final String carrierSlaId, final PricingPolicyProvider.PricingPolicy policy, final BigDecimal qty) {

        SkuPrice price = null;

        if (StringUtils.isNotBlank(cart.getShoppingContext().getCountryCode()) && StringUtils.isNotBlank(cart.getShoppingContext().getStateCode())) {
            price = priceService.getMinimalPrice(
                    null,
                    carrierSlaId + "_" + cart.getShoppingContext().getCountryCode() + "_" + cart.getShoppingContext().getStateCode(),
                    cart.getShoppingContext().getShopId(),
                    cart.getCurrencyCode(),
                    qty,
                    true,
                    policy.getID());
        }

        if (price != null && price.getSkuPriceId() > 0L) {
            return price; // State price
        }

        if (StringUtils.isNotBlank(cart.getShoppingContext().getCountryCode())) {
            price = priceService.getMinimalPrice(
                    null,
                    carrierSlaId + "_" + cart.getShoppingContext().getCountryCode(),
                    cart.getShoppingContext().getShopId(),
                    cart.getCurrencyCode(),
                    qty,
                    true,
                    policy.getID());
        }


        if (price != null && price.getSkuPriceId() > 0L) {
            return price; // Country price
        }

        // Carrier SLA GUID price
        return priceService.getMinimalPrice(
                null,
                carrierSlaId,
                cart.getShoppingContext().getShopId(),
                cart.getCurrencyCode(),
                qty,
                true,
                policy.getID());

    }

}
