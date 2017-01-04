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
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.emptyMap()));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleCarrierNotFound() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            one(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyList())));
            one(bucket1).getSupplier(); will(returnValue("Main"));
            one(carrierSlaService).getById(123L); will(returnValue(null));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingle() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            one(carrierSla).getDisplayName(); will(returnValue(""));
            one(carrierSla).getName(); will(returnValue("CSL001"));
            one(cart).getCurrentLocale(); will(returnValue("en"));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyList())));
            one(bucket1).getSupplier(); will(returnValue("Main"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            one(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

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
    public void testCalculateSingleNoBucket() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            one(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyMap())));
            one(bucket1).getSupplier(); will(returnValue(""));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMulti() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            one(carrierSla).getDisplayName(); will(returnValue(""));
            one(carrierSla).getName(); will(returnValue("CSL001"));
            one(cart).getCurrentLocale(); will(returnValue("en"));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(cart).getCartItemMap(); will(returnValue(buckets));
            one(bucket1).getSupplier(); will(returnValue("Main"));
            one(bucket2).getSupplier(); will(returnValue("Main"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cost).getSkuPriceId(); will(returnValue(345L));
            one(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            one(cart).addShippingToCart(bucket2, "CSL001", "CSL001", new BigDecimal("1.00"));
            one(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("0.00"), new BigDecimal("0.00"));
            one(cart).setShippingPrice("CSL001", bucket2, new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

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
    public void testCalculateMultiNoPrice() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            one(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            one(shoppingContext).getCountryCode(); will(returnValue("GB"));
            one(shoppingContext).getStateCode(); will(returnValue("LON"));
            one(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            one(carrierSla).getGuid(); will(returnValue("CSL001"));
            one(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            one(carrierSla).getDisplayName(); will(returnValue(""));
            one(carrierSla).getName(); will(returnValue("CSL001"));
            one(cart).getCurrentLocale(); will(returnValue("en"));
            one(cart).getCurrencyCode(); will(returnValue("USD"));
            one(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            one(cart).getCartItemMap(); will(returnValue(buckets));
            one(bucket1).getSupplier(); will(returnValue("Main"));
            one(bucket2).getSupplier(); will(returnValue("Main"));
            one(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            one(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, new BigDecimal("1.00")); will(returnValue(cost));
            one(cost).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateMultiNoBucket() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<DeliveryBucket, List<CartItem>>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            one(cart).getCartItemMap(); will(returnValue(buckets));
            one(bucket1).getSupplier(); will(returnValue(""));
            one(bucket2).getSupplier(); will(returnValue(""));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


}