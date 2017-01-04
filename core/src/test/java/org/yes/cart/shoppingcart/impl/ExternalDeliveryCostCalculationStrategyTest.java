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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.emptyMap()));
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
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            one(carrierSlaService).getById(123L); will(returnValue(null));
        }});

        final Total delTotal = new ExternalDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternalOne() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternalMulti() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");

        final Map<String, Long> carriers = new LinkedHashMap<String, Long>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 123L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternalMultiSameCustom() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSla2 = context.mock(CarrierSla.class, "carrierSla2");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");

        final Map<String, Long> carriers = new LinkedHashMap<String, Long>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 123L);
        carriers.put("Main2", 234L);
        carriers.put("Backorder2", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(carrierSlaService).getById(234L); will(returnValue(carrierSla2));
            one(carrierSla2).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla2).getScript(); will(returnValue("customStrategy"));
            allowing(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternalMultiDifferentCustom() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSla2 = context.mock(CarrierSla.class, "carrierSla2");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final DeliveryCostCalculationStrategy customStrategy2 = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy2");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");
        final Total customStrategyTotal2 = context.mock(Total.class, "customStrategyTotal2");
        final Total customStrategyTotalAll = context.mock(Total.class, "customStrategyTotalAll");

        final Map<String, Long> carriers = new LinkedHashMap<String, Long>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 123L);
        carriers.put("Main2", 234L);
        carriers.put("Backorder2", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(carrierSlaService).getById(234L); will(returnValue(carrierSla2));
            one(carrierSla2).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla2).getScript(); will(returnValue("customStrategy2"));
            allowing(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            allowing(applicationContext).getBean("customStrategy2"); will(returnValue(customStrategy2));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
            one(customStrategy2).calculate(cart); will(returnValue(customStrategyTotal2));
            allowing(customStrategyTotal).add(customStrategyTotal2); will(returnValue(customStrategyTotalAll));
            allowing(customStrategyTotal2).add(customStrategyTotal); will(returnValue(customStrategyTotalAll));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotalAll, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateExternalMultiDifferentCustomWithUnavailable() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSla2 = context.mock(CarrierSla.class, "carrierSla2");
        final ApplicationContext applicationContext = context.mock(ApplicationContext.class, "applicationContext");
        final DeliveryCostCalculationStrategy customStrategy = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy");
        final DeliveryCostCalculationStrategy customStrategy2 = context.mock(DeliveryCostCalculationStrategy.class, "customStrategy2");
        final Total customStrategyTotal = context.mock(Total.class, "customStrategyTotal");
        final Total customStrategyTotal2 = null;

        final Map<String, Long> carriers = new LinkedHashMap<String, Long>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 123L);
        carriers.put("Main2", 234L);
        carriers.put("Backorder2", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla).getScript(); will(returnValue("customStrategy"));
            one(carrierSlaService).getById(234L); will(returnValue(carrierSla2));
            one(carrierSla2).getSlaType(); will(returnValue(CarrierSla.EXTERNAL));
            one(carrierSla2).getScript(); will(returnValue("customStrategy2"));
            allowing(applicationContext).getBean("customStrategy"); will(returnValue(customStrategy));
            allowing(applicationContext).getBean("customStrategy2"); will(returnValue(customStrategy2));
            one(customStrategy).calculate(cart); will(returnValue(customStrategyTotal));
            one(customStrategy2).calculate(cart); will(returnValue(customStrategyTotal2));
        }});

        final ExternalDeliveryCostCalculationStrategy strategy = new ExternalDeliveryCostCalculationStrategy(carrierSlaService);
        strategy.setApplicationContext(applicationContext);
        final Total delTotal = strategy.calculate(cart);

        assertSame(customStrategyTotal, delTotal);

        context.assertIsSatisfied();
    }

}