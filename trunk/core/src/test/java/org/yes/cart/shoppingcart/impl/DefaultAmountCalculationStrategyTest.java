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
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-04-23
 * Time: 7:53 AM
 */
public class DefaultAmountCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();

    private final BigDecimal TAX = new BigDecimal("20.00");

    @Test
    public void testCalculateDeliveryNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(null);
        assertEquals(BigDecimal.ZERO.compareTo(delivery), 0);

    }

    @Test
    public void testCalculateDeliveryPriceNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        context.checking(new Expectations() {{
            one(orderDelivery).getPrice(); will(returnValue(null));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(orderDelivery);
        assertEquals(BigDecimal.ZERO.compareTo(delivery), 0);

    }

    @Test
    public void testCalculateDeliveryPrice() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("9.99")));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(orderDelivery);
        assertEquals(new BigDecimal("9.99").compareTo(delivery), 0);

    }

    @Test
    public void testCalculateTaxNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = null;

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateTaxNone() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateTax() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("16.67").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount);
        assertEquals(new BigDecimal("20.00").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateAmountNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = null;
        final BigDecimal taxIncluded = new BigDecimal("16.67");
        final BigDecimal taxExcluded = new BigDecimal("20.00");

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxIncluded);
        assertEquals(BigDecimal.ZERO.compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxExcluded);
        assertEquals(BigDecimal.ZERO.compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateAmountTaxNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");
        final BigDecimal taxIncluded = null;
        final BigDecimal taxExcluded = null;

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxIncluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxExcluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateAmount() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");
        final BigDecimal taxIncluded = new BigDecimal("16.67");
        final BigDecimal taxExcluded = new BigDecimal("20.00");

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxIncluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateAmount(amount, taxExcluded);
        assertEquals(new BigDecimal("120.00").compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateSubTotal() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final DefaultAmountCalculationStrategy.CartItemPrices subTotal = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .calculateSubTotal(Arrays.asList(item1, item2));
        assertEquals(new BigDecimal("80").compareTo(subTotal.getFinalPrice()), 0);
        assertEquals(new BigDecimal("90").compareTo(subTotal.getSalePrice()), 0);
        assertEquals(new BigDecimal("100").compareTo(subTotal.getListPrice()), 0);

    }

    @Test
    public void testCalculateSubTotalWithNullPrice() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(null));
            allowing(item1).getSalePrice(); will(returnValue(null));
            allowing(item1).getListPrice(); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final DefaultAmountCalculationStrategy.CartItemPrices subTotal = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .calculateSubTotal(Arrays.asList(item1, item2));
        assertEquals(new BigDecimal("40").compareTo(subTotal.getFinalPrice()), 0);
        assertEquals(new BigDecimal("40").compareTo(subTotal.getSalePrice()), 0);
        assertEquals(new BigDecimal("60").compareTo(subTotal.getListPrice()), 0);

    }


    @Test
    public void testCalculateSubTotalQtyNull() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(null));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final DefaultAmountCalculationStrategy.CartItemPrices subTotal = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .calculateSubTotal(Arrays.asList(item1, item2));
        assertEquals(new BigDecimal("40").compareTo(subTotal.getFinalPrice()), 0);
        assertEquals(new BigDecimal("50").compareTo(subTotal.getSalePrice()), 0);
        assertEquals(new BigDecimal("60").compareTo(subTotal.getListPrice()), 0);

    }

    @Test
    public void testCalculateInternal() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(orderDelivery);

        assertEquals("Was: " + rezTaxIncluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxIncluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxIncluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalAmount(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalTax(), new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCost(), new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCostAmount(), new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryTax(), new BigDecimal("1.67").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getListTotalAmount(), new BigDecimal("110").compareTo(rezTaxIncluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalAmount(), new BigDecimal("90").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalTax(), new BigDecimal("15").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(orderDelivery);

        assertEquals("Was: " + rezTaxExcluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxExcluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxExcluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalAmount(), new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalTax(), new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxExcluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCost(), new BigDecimal("10").compareTo(rezTaxExcluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCostAmount(), new BigDecimal("12").compareTo(rezTaxExcluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryTax(), new BigDecimal("2").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getListTotalAmount(), new BigDecimal("132").compareTo(rezTaxExcluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalAmount(), new BigDecimal("108").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalTax(), new BigDecimal("18").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }

    @Test
    public void testCalculateSingleDelivery() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrder order = context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order, orderDelivery);

        assertEquals("Was: " + rezTaxIncluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxIncluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxIncluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalAmount(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalTax(), new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCost(), new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCostAmount(), new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryTax(), new BigDecimal("1.67").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getListTotalAmount(), new BigDecimal("110").compareTo(rezTaxIncluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalAmount(), new BigDecimal("90").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalTax(), new BigDecimal("15").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order, orderDelivery);

        assertEquals("Was: " + rezTaxExcluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxExcluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxExcluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalAmount(), new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalTax(), new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxExcluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCost(), new BigDecimal("10").compareTo(rezTaxExcluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCostAmount(), new BigDecimal("12").compareTo(rezTaxExcluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryTax(), new BigDecimal("2").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getListTotalAmount(), new BigDecimal("132").compareTo(rezTaxExcluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalAmount(), new BigDecimal("108").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalTax(), new BigDecimal("18").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }

    @Test
    public void testCalculateMultiDelivery() throws Exception {

        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrder order = context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery orderDelivery1 = context.mock(CustomerOrderDelivery.class, "orderDelivery1");
        final CustomerOrderDelivery orderDelivery2 = context.mock(CustomerOrderDelivery.class, "orderDelivery2");

        context.checking(new Expectations() {{
            allowing(order).getDelivery(); will(returnValue(Arrays.asList(orderDelivery1, orderDelivery2)));
            allowing(order).isPromoApplied(); will(returnValue(true));
            allowing(order).getAppliedPromo(); will(returnValue("ORDER-25%"));
            allowing(order).getPrice(); will(returnValue(new BigDecimal("60")));
            allowing(order).getListPrice(); will(returnValue(new BigDecimal("90")));
            allowing(orderDelivery1).getDetail(); will(returnValue(Arrays.asList(item1)));
            allowing(orderDelivery1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery1).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery1).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(orderDelivery2).getDetail(); will(returnValue(Arrays.asList(item2)));
            allowing(orderDelivery2).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery2).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery2).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order);

        assertEquals("Was: " + rezTaxIncluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxIncluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getPriceSubTotal()), 0);
        assertTrue(rezTaxIncluded.isOrderPromoApplied());
        assertEquals("ORDER-25%", rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxIncluded.getSubTotal(), new BigDecimal("60").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalAmount(), new BigDecimal("60").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalTax(), new BigDecimal("10.00").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryListCost(), new BigDecimal("40").compareTo(rezTaxIncluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCost(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCostAmount(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryTax(), new BigDecimal("3.34").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getListTotalAmount(), new BigDecimal("130").compareTo(rezTaxIncluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalAmount(), new BigDecimal("80").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalTax(), new BigDecimal("13.34").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order);

        assertEquals("Was: " + rezTaxExcluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxExcluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getPriceSubTotal()), 0);
        assertTrue(rezTaxExcluded.isOrderPromoApplied());
        assertEquals("ORDER-25%", rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxExcluded.getSubTotal(), new BigDecimal("60").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalAmount(), new BigDecimal("72").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalTax(), new BigDecimal("12").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryListCost(), new BigDecimal("40").compareTo(rezTaxExcluded.getDeliveryListCost()), 0);
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCost(), new BigDecimal("20").compareTo(rezTaxExcluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCostAmount(), new BigDecimal("24").compareTo(rezTaxExcluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryTax(), new BigDecimal("4").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getListTotalAmount(), new BigDecimal("156").compareTo(rezTaxExcluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalAmount(), new BigDecimal("96").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalTax(), new BigDecimal("16").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }

    @Test
    public void testCalculateShoppingCart() throws Exception {


        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final ShoppingCart cart = context.mock(ShoppingCart.class, "cart");
        final ShoppingContext shoppingContext = context.mock(ShoppingContext.class, "ctx");
        final Customer customer = context.mock(Customer.class, "customer");

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promoCtx");

        context.checking(new Expectations() {{
            allowing(deliveryCostCalculationStrategy).getDeliveryPrice(cart); will(returnValue(new BigDecimal("20.00")));
            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(customerService).findCustomer("bob@doe.com"); will(returnValue(customer));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            allowing(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true, deliveryCostCalculationStrategy, promotionContextFactory, customerService) {

            @Override
            void applyItemLevelPromotions(final Customer cust, final ShoppingCart scart, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }

            @Override
            Total applyOrderLevelPromotions(final Customer cust, final ShoppingCart scart, final Total itemTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return itemTotal;
            }

            @Override
            Total applyShippingPromotions(final Customer cust, final ShoppingCart scart, final Total orderTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return orderTotal;
            }
        }.calculate(cart);

        assertEquals("Was: " + rezTaxIncluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxIncluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxIncluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxIncluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalAmount(), new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getSubTotalTax(), new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryListCost()), 0);
        assertFalse(rezTaxIncluded.isDeliveryPromoApplied());
        assertNull(rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCost(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryCostAmount(), new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getDeliveryTax(), new BigDecimal("3.33").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotal(), new BigDecimal("100").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxIncluded.getListTotalAmount(), new BigDecimal("110").compareTo(rezTaxIncluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalAmount(), new BigDecimal("100").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxIncluded.getTotalTax(), new BigDecimal("16.67").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false, deliveryCostCalculationStrategy, promotionContextFactory, customerService) {

            @Override
            void applyItemLevelPromotions(final Customer cust, final ShoppingCart scart, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }

            @Override
            Total applyOrderLevelPromotions(final Customer cust, final ShoppingCart scart, final Total itemTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return itemTotal;
            }

            @Override
            Total applyShippingPromotions(final Customer cust, final ShoppingCart scart, final Total orderTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return orderTotal;
            }
        }.calculate(cart);

        assertEquals("Was: " + rezTaxExcluded.getListSubTotal(), new BigDecimal("90").compareTo(rezTaxExcluded.getListSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSaleSubTotal(), new BigDecimal("85").compareTo(rezTaxExcluded.getSaleSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getPriceSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getPriceSubTotal()), 0);
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("Was: " + rezTaxExcluded.getSubTotal(), new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalAmount(), new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getSubTotalTax(), new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryListCost(), new BigDecimal("20").compareTo(rezTaxExcluded.getDeliveryListCost()), 0);
        assertFalse(rezTaxExcluded.isDeliveryPromoApplied());
        assertNull(rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCost(), new BigDecimal("20").compareTo(rezTaxExcluded.getDeliveryCost()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryCostAmount(), new BigDecimal("24").compareTo(rezTaxExcluded.getDeliveryCostAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getDeliveryTax(), new BigDecimal("4").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotal(), new BigDecimal("100").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals("Was: " + rezTaxExcluded.getListTotalAmount(), new BigDecimal("132").compareTo(rezTaxExcluded.getListTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalAmount(), new BigDecimal("120").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals("Was: " + rezTaxExcluded.getTotalTax(), new BigDecimal("20").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }
}
