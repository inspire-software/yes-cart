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

package org.yes.cart.config.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.PriceResolver;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.TaxProvider;

import java.util.List;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class ShopPricingConfigurationImpl extends AbstractShopConfigurationImpl {

    public ShopPricingConfigurationImpl(final SystemService systemService,
                                        final ShopService shopService) {
        super(systemService, shopService);
    }

    void registerCustomTaxProvider(final Shop shop, final List<Shop> subs, final Properties properties) {

        final TaxProvider taxProvider = determineConfiguration(properties, shop.getCode() + ".taxProvider", TaxProvider.class);

        customise(shop.getCode(), shop.getCode(), TaxProvider.class, taxProvider);
    }

    void registerCustomPricingPolicyProvider(final Shop shop, final List<Shop> subs, final Properties properties) {

        final PricingPolicyProvider pricingPolicyProvider = determineConfiguration(properties, shop.getCode() + ".pricingPolicyProvider", PricingPolicyProvider.class);

        customise(shop.getCode(), shop.getCode(), PricingPolicyProvider.class, pricingPolicyProvider);
    }

    void registerCustomPriceResolver(final Shop shop, final List<Shop> subs, final Properties properties) {

        final PriceResolver priceResolver = determineConfiguration(properties, shop.getCode() + ".priceResolver", PriceResolver.class);

        customise(shop.getCode(), shop.getShopId(), PriceResolver.class, priceResolver);
        if (CollectionUtils.isNotEmpty(subs)) {
            for (final Shop sub : subs) {
                customise(sub.getCode(), sub.getShopId(), PriceResolver.class, priceResolver);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doConfigurations(final Shop shop, final List<Shop> subs, final Properties properties) {
        this.registerCustomTaxProvider(shop, subs, properties);
        this.registerCustomPricingPolicyProvider(shop, subs, properties);
        this.registerCustomPriceResolver(shop, subs, properties);
    }

}
