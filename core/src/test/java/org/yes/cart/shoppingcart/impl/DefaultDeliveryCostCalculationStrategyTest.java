/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

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

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
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

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
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
    }

    @Test
    public void testCalculateSingle() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(false));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getCarrierslaId(); will(returnValue(123L));
            one(carrierSla).getPrice(); will(returnValue(new BigDecimal("20.00")));
            one(cart).removeShipping();
            one(cart).addShippingToCart("123", new BigDecimal("1.00"));
            one(cart).setShippingPrice("123", new BigDecimal("20.00"), new BigDecimal("20.00"));
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("20.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("20.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("20.00", delTotal.getTotalAmount().toPlainString());
    }

    @Test
    public void testCalculateMulti() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(true));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getCarrierslaId(); will(returnValue(123L));
            one(carrierSla).getPrice(); will(returnValue(new BigDecimal("20.00")));
            one(cart).removeShipping();
            one(cart).addShippingToCart("123", new BigDecimal("2.00"));
            one(cart).setShippingPrice("123", new BigDecimal("20.00"), new BigDecimal("20.00"));
        }});

        final Total delTotal = new DefaultDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("40.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("40.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("40.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("40.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("40.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("40.00", delTotal.getTotalAmount().toPlainString());
    }
}
