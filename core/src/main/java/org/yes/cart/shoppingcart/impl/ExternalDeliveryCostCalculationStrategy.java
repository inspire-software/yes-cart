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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.ShopCodeContext;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class ExternalDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy, ApplicationContextAware {

    private static final Total ZERO_TOTAL = new TotalImpl();

    private final CarrierSlaService carrierSlaService;
    private ApplicationContext applicationContext;

    public ExternalDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    public Total calculate(final MutableShoppingCart cart) {

        cart.removeShipping();

        if (cart.getCarrierSlaId() != null) {
            final CarrierSla carrierSla = carrierSlaService.getById(cart.getCarrierSlaId());
            if (carrierSla != null && CarrierSla.EXTERNAL.equals(carrierSla.getSlaType())) {

                final Object strategy = applicationContext.getBean(carrierSla.getScript());
                if (strategy instanceof DeliveryCostCalculationStrategy) {

                    return ((DeliveryCostCalculationStrategy) strategy).calculate(cart);

                } else {

                    ShopCodeContext.getLog(this).error("CarrierSla.script [{}] is not a bean of type DeliveryCostCalculationStrategy", carrierSla.getScript());

                }

            }
        }
        return ZERO_TOTAL;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
