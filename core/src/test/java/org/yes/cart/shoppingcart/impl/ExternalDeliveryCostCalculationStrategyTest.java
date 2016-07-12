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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/06/2016
 * Time: 18:28
 */
public class ExternalDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNone() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new ExternalDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleCarrierNotFound() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new ExternalDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternal() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(cart).removeShipping();
            one(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotal, delTotal);

        context.assertIsSatisfied();
    }

}