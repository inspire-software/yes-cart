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
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/06/2016
 * Time: 18:21
 */
public class FreeDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNone() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

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

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(carrierSlaService).getById(123L); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

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
            one(carrierSla).getGuid(); will(returnValue("CS001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            one(cart).removeShipping();
            one(cart).addShippingToCart("CS001", new BigDecimal("1.00"));
            one(cart).setShippingPrice("CS001", new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

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

        context.assertIsSatisfied();
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
            one(carrierSla).getGuid(); will(returnValue("CS001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            one(cart).removeShipping();
            one(cart).addShippingToCart("CS001", new BigDecimal("2.00"));
            one(cart).setShippingPrice("CS001", new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService).calculate(cart);

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