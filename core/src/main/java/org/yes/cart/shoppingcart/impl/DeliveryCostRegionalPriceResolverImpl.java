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
import org.yes.cart.service.domain.ShopService;
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
    private final ShopService shopService;

    public DeliveryCostRegionalPriceResolverImpl(final PriceService priceService,
                                                 final ShopService shopService) {
        this.priceService = priceService;
        this.shopService = shopService;
    }


    /**
     * {@inheritDoc}
     */
    public SkuPrice getSkuPrice(final ShoppingCart cart, final String carrierSlaBaseCode, final PricingPolicyProvider.PricingPolicy policy, final BigDecimal qty) {

        SkuPrice price = null;

        if (StringUtils.isNotBlank(cart.getShoppingContext().getCountryCode()) && StringUtils.isNotBlank(cart.getShoppingContext().getStateCode())) {
            final String stateSpecificSla = carrierSlaBaseCode + "_" + cart.getShoppingContext().getCountryCode() + "_" + cart.getShoppingContext().getStateCode();
            price = resolveMinimalPrice(cart, policy, stateSpecificSla, qty);
            if (price != null && price.getSkuPriceId() > 0L) {
                return price; // State price
            }
        }

        if (StringUtils.isNotBlank(cart.getShoppingContext().getCountryCode())) {
            final String countrySpecificSla = carrierSlaBaseCode + "_" + cart.getShoppingContext().getCountryCode();
            price = resolveMinimalPrice(cart, policy, countrySpecificSla, qty);
            if (price != null && price.getSkuPriceId() > 0L) {
                return price; // Country price
            }
        }

        // Carrier SLA GUID price
        return resolveMinimalPrice(cart, policy, carrierSlaBaseCode, qty);

    }


    /**
     * We resolve prices from current customer shop first. In simple setup this would be the same as the master.
     * In case current and master differs we are in B2B mode, so we check if we are not in strict profile and
     * attempt to resolve price from master.
     *
     * @param cart       cart
     * @param policy     policy to use
     * @param slaSkuCode sku to resolve price for
     * @param qty        quantity
     *
     * @return resolved SKU price
     */
    protected SkuPrice resolveMinimalPrice(final ShoppingCart cart,
                                           final PricingPolicyProvider.PricingPolicy policy,
                                           final String slaSkuCode,
                                           final BigDecimal qty) {

        final long customerShopId = cart.getShoppingContext().getCustomerShopId();
        final long masterShopId = cart.getShoppingContext().getShopId();
        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String currency = cart.getCurrencyCode();

        return priceService.getMinimalPrice(null, slaSkuCode, customerShopId, fallbackShopId, currency, qty, true, policy.getID());

    }



}
