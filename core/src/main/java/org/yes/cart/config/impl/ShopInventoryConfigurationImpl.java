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

package org.yes.cart.config.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.DeliveryTimeEstimationVisitor;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;
import org.yes.cart.shoppingcart.ProductQuantityStrategy;

import java.util.List;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class ShopInventoryConfigurationImpl extends AbstractShopConfigurationImpl {

    public ShopInventoryConfigurationImpl(final SystemService systemService,
                                          final ShopService shopService) {
        super(systemService, shopService);
    }

    void registerCustomProductAvailabilityStrategy(final Shop shop, final List<Shop> subs, final Properties properties) {

        final ProductAvailabilityStrategy pas = determineConfiguration(properties, shop.getCode() + ".productAvailabilityStrategy", ProductAvailabilityStrategy.class);

        customise(shop.getCode(), shop.getShopId(), "productAvailabilityStrategy", ProductAvailabilityStrategy.class, pas);
        if (CollectionUtils.isNotEmpty(subs)) {
            for (final Shop sub : subs) {
                customise(sub.getCode(), sub.getShopId(), "productAvailabilityStrategy", ProductAvailabilityStrategy.class, pas);
            }
        }
    }

    void registerCustomProductQuantityStrategy(final Shop shop, final List<Shop> subs, final Properties properties) {

        final ProductQuantityStrategy pqs = determineConfiguration(properties, shop.getCode() + ".productQuantityStrategy", ProductQuantityStrategy.class);

        customise(shop.getCode(), shop.getShopId(), "productQuantityStrategy", ProductQuantityStrategy.class, pqs);
        if (CollectionUtils.isNotEmpty(subs)) {
            for (final Shop sub : subs) {
                customise(sub.getCode(), sub.getShopId(), "productQuantityStrategy", ProductQuantityStrategy.class, pqs);
            }
        }
    }

    void registerCustomDeliveryTimeEstimationVisitor(final Shop shop, final List<Shop> subs, final Properties properties) {

        final DeliveryTimeEstimationVisitor dtev = determineConfiguration(properties, shop.getCode() + ".deliveryTimeEstimationVisitor", DeliveryTimeEstimationVisitor.class);

        customise(shop.getCode(), shop.getCode(), "deliveryTimeEstimationVisitor", DeliveryTimeEstimationVisitor.class, dtev);
        
    }

    /** {@inheritDoc} */
    @Override
    protected void doConfigurations(final Shop shop, final List<Shop> subs, final Properties properties) {

        this.registerCustomProductAvailabilityStrategy(shop, subs, properties);
        this.registerCustomProductQuantityStrategy(shop, subs, properties);
        this.registerCustomDeliveryTimeEstimationVisitor(shop, subs, properties);

    }

}
