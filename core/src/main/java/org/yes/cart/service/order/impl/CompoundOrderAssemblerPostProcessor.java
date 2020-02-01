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

package org.yes.cart.service.order.impl;

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.order.OrderAssemblerPostProcessor;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/02/2020
 * Time: 16:40
 */
public class CompoundOrderAssemblerPostProcessor implements OrderAssemblerPostProcessor, Configuration {

    private final OrderAssemblerPostProcessor[] processors;

    private ConfigurationContext cfgContext;

    public CompoundOrderAssemblerPostProcessor(final List<OrderAssemblerPostProcessor> processors) {
        this.processors = processors.toArray(new OrderAssemblerPostProcessor[processors.size()]);
    }

    @Override
    public void postProcess(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final String orderNumber, final boolean temp) throws OrderAssemblyException {
        for (final OrderAssemblerPostProcessor processor : this.processors) {
            processor.postProcess(customerOrder, shoppingCart, orderNumber, temp);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
