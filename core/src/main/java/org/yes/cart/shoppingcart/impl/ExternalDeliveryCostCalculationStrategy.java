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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.util.HashSet;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class ExternalDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalDeliveryCostCalculationStrategy.class);

    private final CarrierSlaService carrierSlaService;
    private ApplicationContext applicationContext;

    public ExternalDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    public Total calculate(final MutableShoppingCart cart) {

        if (!cart.getCarrierSlaId().isEmpty()) {

            Total total = null;

            final Set<Long> uniqueCarrierSlaIds = new HashSet<Long>(cart.getCarrierSlaId().values());
            final Set<DeliveryCostCalculationStrategy> strategyExecutionPlan = new HashSet<DeliveryCostCalculationStrategy>();

            for (final Long carrierSlaId : uniqueCarrierSlaIds) {

                final CarrierSla carrierSla = carrierSlaService.getById(carrierSlaId);
                if (carrierSla != null && CarrierSla.EXTERNAL.equals(carrierSla.getSlaType())) {

                    final Object strategy = applicationContext.getBean(carrierSla.getScript());
                    if (strategy instanceof DeliveryCostCalculationStrategy) {

                        strategyExecutionPlan.add((DeliveryCostCalculationStrategy) strategy);

                    } else {

                        LOG.error("CarrierSla.script [{}] is not a bean of type DeliveryCostCalculationStrategy", carrierSla.getScript());

                    }

                }
            }

            for (final DeliveryCostCalculationStrategy strategy : strategyExecutionPlan) {

                final Total subTotal = strategy.calculate(cart);
                if (subTotal != null) {
                    total = total == null ? subTotal : total.add(subTotal);
                }

            }

            return total;
        }
        return null;

    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
