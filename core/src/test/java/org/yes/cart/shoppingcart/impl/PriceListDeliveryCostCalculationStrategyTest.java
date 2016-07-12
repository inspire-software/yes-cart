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
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/06/2016
 * Time: 18:36
 */
public class PriceListDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNone() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(null));
            one(cart).removeShipping();
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

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

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleRegular() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(false));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cost).getRegularPrice(); will(returnValue(new BigDecimal("10.00")));
            one(cost).getSalePriceForCalculation(); will(returnValue(null));
            one(cart).addShippingToCart("CSL001", new BigDecimal("1.00"));
            one(cart).setShippingPrice("CSL001", new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("10.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleSale() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(false));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cost).getRegularPrice(); will(returnValue(new BigDecimal("10.00")));
            one(cost).getSalePriceForCalculation(); will(returnValue(new BigDecimal("8.00")));
            one(cart).addShippingToCart("CSL001", new BigDecimal("1.00"));
            one(cart).setShippingPrice("CSL001", new BigDecimal("8.00"), new BigDecimal("8.00"));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("8.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("8.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("8.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleNoPrice() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(false));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMultiRegular() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("2.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cost).getRegularPrice(); will(returnValue(new BigDecimal("10.00")));
            one(cost).getSalePriceForCalculation(); will(returnValue(null));
            one(cart).addShippingToCart("CSL001", new BigDecimal("2.00"));
            one(cart).setShippingPrice("CSL001", new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

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

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMultiSale() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("2.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cost).getRegularPrice(); will(returnValue(new BigDecimal("10.00")));
            one(cost).getSalePriceForCalculation(); will(returnValue(new BigDecimal("8.00")));
            one(cart).addShippingToCart("CSL001", new BigDecimal("2.00"));
            one(cart).setShippingPrice("CSL001", new BigDecimal("8.00"), new BigDecimal("8.00"));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("16.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("16.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("16.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }



    @Test
    public void testCalculateMultiNoPrice() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(123L));
            one(cart).getOrderInfo(); will(returnValue(orderInfo));
            one(orderInfo).isMultipleDelivery(); will(returnValue(true));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FIXED));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("2.00")); will(returnValue(cost));
            one(cart).removeShipping();
            one(cost).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new PriceListDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


}