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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.spring.LinkedHashMapBeanImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * User: denispavlov
 * Date: 07/11/2014
 * Time: 11:39
 */
public class DefaultDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNoneOf() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId();
            will(returnValue(Collections.emptyMap()));
            oneOf(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(Collections.singletonMap("S", strategy))).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleCarrierNotFound() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(carrierSlaService).getById(123L); will(returnValue(null));
            oneOf(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(Collections.singletonMap("S", strategy))).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateStrategyoneOf() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");
        final Total strategyTotal = context.mock(Total.class, "strategyTotal");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getSlaType(); will(returnValue("S"));
            oneOf(cart).removeShipping();
            oneOf(strategy).calculate(cart); will(returnValue(strategyTotal));
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(Collections.singletonMap("S", strategy))).calculate(cart);

        assertSame(strategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateStrategyMulti() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSlaB = context.mock(CarrierSla.class, "carrierSlaB");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");
        final DeliveryCostCalculationStrategy strategyB = context.mock(DeliveryCostCalculationStrategy.class, "strategyB");
        final Total strategyTotal = context.mock(Total.class, "strategyTotal");
        final Total strategyTotalB = context.mock(Total.class, "strategyTotalB");
        final Total strategyTotalAll = context.mock(Total.class, "strategyTotalAll");

        final Map<String, Long> carriers = new LinkedHashMap<>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getSlaType(); will(returnValue("S"));
            oneOf(carrierSlaService).getById(234L); will(returnValue(carrierSlaB));
            oneOf(carrierSlaB).getSlaType(); will(returnValue("B"));
            oneOf(cart).removeShipping();
            oneOf(strategy).calculate(cart); will(returnValue(strategyTotal));
            oneOf(strategyB).calculate(cart); will(returnValue(strategyTotalB));
            allowing(strategyTotal).add(strategyTotalB); will(returnValue(strategyTotalAll));
            allowing(strategyTotalB).add(strategyTotal); will(returnValue(strategyTotalAll));
        }});

        final Map<String, DeliveryCostCalculationStrategy> strategies = new HashMap<>();
        strategies.put("S", strategy);
        strategies.put("B", strategyB);

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(strategies)).calculate(cart);

        assertSame(strategyTotalAll, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateStrategyMultiWithUnavailable() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSlaB = context.mock(CarrierSla.class, "carrierSlaB");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");
        final DeliveryCostCalculationStrategy strategyB = context.mock(DeliveryCostCalculationStrategy.class, "strategyB");
        final Total strategyTotal = null;
        final Total strategyTotalB = context.mock(Total.class, "strategyTotalB");

        final Map<String, Long> carriers = new LinkedHashMap<>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getSlaType(); will(returnValue("S"));
            oneOf(carrierSlaService).getById(234L); will(returnValue(carrierSlaB));
            oneOf(carrierSlaB).getSlaType(); will(returnValue("B"));
            oneOf(cart).removeShipping();
            oneOf(strategy).calculate(cart); will(returnValue(strategyTotal));
            oneOf(strategyB).calculate(cart); will(returnValue(strategyTotalB));
        }});

        final Map<String, DeliveryCostCalculationStrategy> strategies = new HashMap<>();
        strategies.put("S", strategy);
        strategies.put("B", strategyB);

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(strategies)).calculate(cart);

        assertSame(strategyTotalB, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateStrategyMultiWithUnavailableB() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CarrierSla carrierSlaB = context.mock(CarrierSla.class, "carrierSlaB");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");
        final DeliveryCostCalculationStrategy strategyB = context.mock(DeliveryCostCalculationStrategy.class, "strategyB");
        final Total strategyTotal = context.mock(Total.class, "strategyTotal");
        final Total strategyTotalB = null;

        final Map<String, Long> carriers = new LinkedHashMap<>();
        carriers.put("Main", 123L);
        carriers.put("Backorder", 234L);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(carriers));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getSlaType(); will(returnValue("S"));
            oneOf(carrierSlaService).getById(234L); will(returnValue(carrierSlaB));
            oneOf(carrierSlaB).getSlaType(); will(returnValue("B"));
            oneOf(cart).removeShipping();
            oneOf(strategy).calculate(cart); will(returnValue(strategyTotal));
            oneOf(strategyB).calculate(cart); will(returnValue(strategyTotalB));
        }});

        final Map<String, DeliveryCostCalculationStrategy> strategies = new HashMap<>();
        strategies.put("S", strategy);
        strategies.put("B", strategyB);

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(strategies)).calculate(cart);

        assertSame(strategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateNoStrategy() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            allowing(carrierSla).getSlaType(); will(returnValue("S"));
            oneOf(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, new LinkedHashMapBeanImpl<>(Collections.emptyMap())).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }
}
