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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;

import java.util.List;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public abstract class AbstractShopConfigurationImpl extends AbstractConfigurationImpl {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractShopConfigurationImpl.class);

    private final ShopService shopService;

    public AbstractShopConfigurationImpl(final SystemService systemService,
                                         final ShopService shopService) {
        super(systemService);
        this.shopService = shopService;
    }


    /** {@inheritDoc} */
    @Override
    protected void onConfigureEvent(final Properties properties) {
        final List<Shop> shops = this.shopService.getNonSubShops();
        for (final Shop shop : shops) {
            final List<Shop> subs = this.shopService.getSubShopsByMaster(shop.getShopId());
            this.doConfigurations(shop, subs, properties);
        }
    }

    /**
     * Perform configurations necessary.
     *  @param shop shop
     * @param subs its subs (if any)
     * @param properties
     */
    protected abstract void doConfigurations(final Shop shop, final List<Shop> subs, final Properties properties);

}
