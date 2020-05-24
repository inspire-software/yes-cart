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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.DeliveryTimeEstimationVisitor;
import org.yes.cart.shoppingcart.MutableShoppingCart;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 07:36
 */
public class DeliveryTimeEstimationVisitorImpl 
        implements DeliveryTimeEstimationVisitor, ConfigurationRegistry<String, DeliveryTimeEstimationVisitor> {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryTimeEstimationVisitorImpl.class);

    private final ShopService shopService;
    private final DeliveryTimeEstimationVisitor defaultDeliveryTimeEstimationVisitor;
    private final Map<String, DeliveryTimeEstimationVisitor> customDeliveryTimeEstimationVisitors = new HashMap<>();

    public DeliveryTimeEstimationVisitorImpl(final ShopService shopService,
                                             final DeliveryTimeEstimationVisitor deliveryTimeEstimationVisitor) {
        this.shopService = shopService;
        this.defaultDeliveryTimeEstimationVisitor = deliveryTimeEstimationVisitor;
    }


    /** {@inheritDoc} */
    @Override
    public void visit(final CustomerOrder order) {

        final Shop orderShop = shopService.getById(order.getShop().getShopId());
        final Shop cfgShop = orderShop.getMaster() == null ? orderShop : shopService.getById(orderShop.getMaster().getShopId());

        getDeliveryTimeEstimationVisitor(cfgShop.getCode()).visit(order);

    }

    /** {@inheritDoc} */
    @Override
    public void visit(final CustomerOrderDelivery delivery) {

        final Shop orderShop = shopService.getById(delivery.getCustomerOrder().getShop().getShopId());
        final Shop cfgShop = orderShop.getMaster() == null ? orderShop : shopService.getById(orderShop.getMaster().getShopId());

        getDeliveryTimeEstimationVisitor(cfgShop.getCode()).visit(delivery);

    }

    /** {@inheritDoc} */
    @Override
    public void visit(final MutableShoppingCart shoppingCart) {

        final Shop cfgShop = shopService.getById(shoppingCart.getShoppingContext().getShopId());

        getDeliveryTimeEstimationVisitor(cfgShop.getCode()).visit(shoppingCart);

    }

    protected DeliveryTimeEstimationVisitor getDeliveryTimeEstimationVisitor(final String shopCode) {
        DeliveryTimeEstimationVisitor resolver = customDeliveryTimeEstimationVisitors.get(shopCode);
        if (resolver == null) {
            resolver = defaultDeliveryTimeEstimationVisitor;
        }
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return configuration instanceof DeliveryTimeEstimationVisitor ||
                (configuration instanceof Class && DeliveryTimeEstimationVisitor.class.isAssignableFrom((Class<?>) configuration));
    }

    /** {@inheritDoc} */
    @Override
    public void register(final String shopCode, final DeliveryTimeEstimationVisitor provider) {

        if (provider != null) {
            LOG.debug("Custom shop settings for {} registering delivery time estimation visitor {}", shopCode, provider.getClass());
            customDeliveryTimeEstimationVisitors.put(shopCode, provider);
        } else {
            LOG.debug("Custom shop settings for {} registering delivery time estimation visitor DEFAULT", shopCode);
            customDeliveryTimeEstimationVisitors.remove(shopCode);
        }

    }
}
