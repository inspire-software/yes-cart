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
import org.yes.cart.service.order.OrderAssemblerPostProcessor;
import org.yes.cart.shoppingcart.CartContentsValidator;

import java.util.List;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 16:47
 */
public class ShopCartConfigurationImpl extends AbstractShopConfigurationImpl {

    public ShopCartConfigurationImpl(final SystemService systemService,
                                     final ShopService shopService) {
        super(systemService, shopService);
    }

    void registerCustomCartContentsValidator(final Shop shop, final List<Shop> subs, final Properties properties) {

        final CartContentsValidator validator = determineConfiguration(properties, shop.getCode() + ".cartContentsValidator", CartContentsValidator.class);

        customise(shop.getCode(), shop.getCode(), "cartContentsValidator", CartContentsValidator.class, validator);
    }

    void registerCustomOrderAssemblerPostProcessor(final Shop shop, final List<Shop> subs, final Properties properties) {

        final OrderAssemblerPostProcessor postProcessor = determineConfiguration(properties, shop.getCode() + ".orderAssemblerPostProcessor", OrderAssemblerPostProcessor.class);

        customise(shop.getCode(), shop.getCode(), "orderAssemblerPostProcessor", OrderAssemblerPostProcessor.class, postProcessor);
        if (CollectionUtils.isNotEmpty(subs)) {
            for (final Shop sub : subs) {
                customise(sub.getCode(), sub.getCode(), "orderAssemblerPostProcessor", OrderAssemblerPostProcessor.class, postProcessor);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doConfigurations(final Shop shop, final List<Shop> subs, final Properties properties) {
        this.registerCustomCartContentsValidator(shop, subs, properties);
        this.registerCustomOrderAssemblerPostProcessor(shop, subs, properties);
    }

}
