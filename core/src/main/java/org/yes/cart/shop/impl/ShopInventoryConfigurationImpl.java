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

package org.yes.cart.shop.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.ProductAvailabilityStrategy;

import java.util.List;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class ShopInventoryConfigurationImpl extends AbstractShopConfigurationImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ShopInventoryConfigurationImpl.class);

    private ProductAvailabilityStrategy productAvailabilityStrategy;

    public ShopInventoryConfigurationImpl(final String shopCode, final ShopService shopService) {
        super(shopCode, shopService);
    }

    void registerCustomProductAvailabilityStrategy(final Shop shop, final List<Shop> subs) {
        if (productAvailabilityStrategy != null) {
            configureShop(shop.getShopId(), productAvailabilityStrategy);
            if (CollectionUtils.isNotEmpty(subs)) {
                for (final Shop sub : subs) {
                    configureShop(sub.getShopId(), productAvailabilityStrategy);
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected void doConfigurations(final Shop shop, final List<Shop> subs) {
        this.registerCustomProductAvailabilityStrategy(shop, subs);
    }

    /**
     * Spring IoC.
     *
     * @param productAvailabilityStrategy strategy
     */
    public void setProductAvailabilityStrategy(final ProductAvailabilityStrategy productAvailabilityStrategy) {
        this.productAvailabilityStrategy = productAvailabilityStrategy;
    }

}
