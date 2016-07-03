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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 07/11/2014
 * Time: 11:39
 */
public class DefaultDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNone() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId();
            will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, Collections.singletonMap("S", strategy)).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleCarrierNotFound() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, Collections.singletonMap("S", strategy)).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateStrategy() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final DeliveryCostCalculationStrategy strategy = context.mock(DeliveryCostCalculationStrategy.class, "strategy");
        final Total strategyTotal = context.mock(Total.class, "strategyTotal");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getSlaType(); will(returnValue("S"));
            one(cart).removeShipping();
            one(strategy).calculate(cart); will(returnValue(strategyTotal));
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, Collections.singletonMap("S", strategy)).calculate(cart);

        assertSame(strategyTotal, delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateNoStrategy() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            allowing(carrierSla).getSlaType(); will(returnValue("S"));
            one(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService, Collections.EMPTY_MAP).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }
}
