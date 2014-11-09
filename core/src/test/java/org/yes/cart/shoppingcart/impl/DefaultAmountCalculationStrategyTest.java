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

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final BigDecimal delivery = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(null);
        assertEquals("0.00", delivery.toPlainString());

    }

    @Test
    public void testCalculateDeliveryPriceNull() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        context.checking(new Expectations() {{
            one(orderDelivery).getPrice();
            will(returnValue(null));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(orderDelivery);
        assertEquals("0.00", delivery.toPlainString());

    }

    @Test
    public void testCalculateDeliveryPrice() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getPrice();
            will(returnValue(new BigDecimal("9.99")));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateDelivery(orderDelivery);
        assertEquals("9.99", delivery.toPlainString());

    }

    @Test
    public void testCalculateTaxNull() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = null;

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, BigDecimal.ZERO, true);
        assertEquals("0.00", taxIncluded.toPlainString());

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, BigDecimal.ZERO, false);
        assertEquals("0.00", taxExcluded.toPlainString());

    }

    @Test
    public void testCalculateTaxNone() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, BigDecimal.ZERO, true);
        assertEquals("0.00", taxIncluded.toPlainString());

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, BigDecimal.ZERO, false);
        assertEquals("0.00", taxExcluded.toPlainString());

    }

    @Test
    public void testCalculateTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, TAX, true);
        assertEquals("16.67", taxIncluded.toPlainString());

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculateTax(amount, TAX, false);
        assertEquals("20.00", taxExcluded.toPlainString());

    }

    @Test
    public void testCalculateSubTotalInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getProductSkuCode(); will(returnValue("A-001"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("16.66")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-001"); will(returnValue(tax));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(false));
            one(cart).setProductSkuTax("A-001", new BigDecimal("16.66"), new BigDecimal("20.00"), new BigDecimal("20.00"), "VAT", false);
            one(cart).setProductSkuTax("A-002", new BigDecimal("33.33"), new BigDecimal("40.00"), new BigDecimal("20.00"), "VAT", false);
        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("100.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("90.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("80.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("80.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("13.35", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("80.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("80.00", itemTotal.getTotal().toPlainString());
        assertEquals("13.35", itemTotal.getTotalTax().toPlainString());
        assertEquals("100.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("80.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateSubTotalExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getProductSkuCode(); will(returnValue("A-001"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("24.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-001"); will(returnValue(tax));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(true));
            one(cart).setProductSkuTax("A-001", new BigDecimal("20.00"), new BigDecimal("24.00"), new BigDecimal("20.00"), "VAT", true);
            one(cart).setProductSkuTax("A-002", new BigDecimal("40.00"), new BigDecimal("48.00"), new BigDecimal("20.00"), "VAT", true);
        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("100.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("90.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("80.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("80.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("16.00", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("96.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("80.00", itemTotal.getTotal().toPlainString());
        assertEquals("16.00", itemTotal.getTotalTax().toPlainString());
        assertEquals("120.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("96.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateSubTotalWithNullPriceInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getPrice(); will(returnValue(null));
            allowing(item1).getSalePrice(); will(returnValue(null));
            allowing(item1).getListPrice(); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item1).getNetPrice(); will(returnValue(null));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(false));
            one(cart).setProductSkuTax("A-002", new BigDecimal("33.33"), new BigDecimal("40.00"), new BigDecimal("20.00"), "VAT", false);

        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("60.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", itemTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("40.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("6.67", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("40.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotal().toPlainString());
        assertEquals("6.67", itemTotal.getTotalTax().toPlainString());
        assertEquals("60.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateSubTotalWithNullPriceExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getPrice(); will(returnValue(null));
            allowing(item1).getSalePrice(); will(returnValue(null));
            allowing(item1).getListPrice(); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item1).getNetPrice(); will(returnValue(null));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(true));
            one(cart).setProductSkuTax("A-002", new BigDecimal("40.00"), new BigDecimal("48.00"), new BigDecimal("20.00"), "VAT", true);

        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("60.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", itemTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("40.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("8.00", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("48.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotal().toPlainString());
        assertEquals("8.00", itemTotal.getTotalTax().toPlainString());
        assertEquals("72.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("48.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateSubTotalQtyNullInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(null));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(false));
            one(cart).setProductSkuTax("A-002", new BigDecimal("33.33"), new BigDecimal("40.00"), new BigDecimal("20.00"), "VAT", false);

        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("60.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("50.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", itemTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("40.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("6.67", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("40.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotal().toPlainString());
        assertEquals("6.67", itemTotal.getTotalTax().toPlainString());
        assertEquals("60.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateSubTotalQtyNullExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext cartContext = context.mock(MutableShoppingContext.class, "cartContext");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        context.checking(new Expectations() {{
            allowing(cart).getShoppingContext(); will(returnValue(cartContext));
            allowing(cartContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(cartContext).getCountryCode(); will(returnValue("GB"));
            allowing(cartContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(null));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(true));
            one(cart).setProductSkuTax("A-002", new BigDecimal("40.00"), new BigDecimal("48.00"), new BigDecimal("20.00"), "VAT", true);

        }});


        final Total itemTotal = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService)
                .applyTaxToCartItemsAndCalculateItemTotal(cart);
        assertEquals("60.00", itemTotal.getListSubTotal().toPlainString());
        assertEquals("50.00", itemTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", itemTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("40.00", itemTotal.getPriceSubTotal().toPlainString());
        assertFalse(itemTotal.isOrderPromoApplied());
        assertNull(itemTotal.getAppliedOrderPromo());
        assertEquals("40.00", itemTotal.getSubTotal().toPlainString());
        assertEquals("8.00", itemTotal.getSubTotalTax().toPlainString());
        assertEquals("48.00", itemTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCost().toPlainString());
        assertFalse(itemTotal.isDeliveryPromoApplied());
        assertNull(itemTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", itemTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", itemTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("40.00", itemTotal.getTotal().toPlainString());
        assertEquals("8.00", itemTotal.getTotalTax().toPlainString());
        assertEquals("72.00", itemTotal.getListTotalAmount().toPlainString());
        assertEquals("48.00", itemTotal.getTotalAmount().toPlainString());

    }

    @Test
    public void testCalculateDeliveryInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getNetPrice(); will(returnValue(new BigDecimal("8.33")));
            allowing(orderDelivery).getGrossPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("16.66")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(orderDelivery);

        assertEquals("90.00", rezTaxIncluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxIncluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxIncluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxIncluded.getSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getSubTotalAmount().toPlainString());
        assertEquals("13.35", rezTaxIncluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxIncluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCost().toPlainString());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("1.67", rezTaxIncluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotal().toPlainString());
        assertEquals("110.00", rezTaxIncluded.getListTotalAmount().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotalAmount().toPlainString());
        assertEquals("15.02", rezTaxIncluded.getTotalTax().toPlainString());

    }


    @Test
    public void testCalculateDeliveryExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getNetPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getGrossPrice(); will(returnValue(new BigDecimal("12.00")));
            allowing(orderDelivery).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("24.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(orderDelivery);

        assertEquals("90.00", rezTaxExcluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxExcluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxExcluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxExcluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxExcluded.getSubTotal().toPlainString());
        assertEquals("96.00", rezTaxExcluded.getSubTotalAmount().toPlainString());
        assertEquals("16.00", rezTaxExcluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxExcluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxExcluded.getDeliveryCost().toPlainString());
        assertEquals("12.00", rezTaxExcluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("2.00", rezTaxExcluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxExcluded.getTotal().toPlainString());
        assertEquals("132.00", rezTaxExcluded.getListTotalAmount().toPlainString());
        assertEquals("108.00", rezTaxExcluded.getTotalAmount().toPlainString());
        assertEquals("18.00", rezTaxExcluded.getTotalTax().toPlainString());

    }


    @Test
    public void testCalculateOrderSingleDeliveryInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
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
            allowing(orderDelivery).getNetPrice(); will(returnValue(new BigDecimal("8.33")));
            allowing(orderDelivery).getGrossPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("16.66")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order, orderDelivery);

        assertEquals("90.00", rezTaxIncluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxIncluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxIncluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxIncluded.getSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getSubTotalAmount().toPlainString());
        assertEquals("13.35", rezTaxIncluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxIncluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCost().toPlainString());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("1.67", rezTaxIncluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotal().toPlainString());
        assertEquals("110.00", rezTaxIncluded.getListTotalAmount().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotalAmount().toPlainString());
        assertEquals("15.02", rezTaxIncluded.getTotalTax().toPlainString());

    }

    @Test
    public void testCalculateOrderSingleDeliveryExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
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
            allowing(orderDelivery).getNetPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery).getGrossPrice(); will(returnValue(new BigDecimal("12.00")));
            allowing(orderDelivery).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(orderDelivery).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("24.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order, orderDelivery);

        assertEquals("90.00", rezTaxExcluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxExcluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxExcluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxExcluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxExcluded.getSubTotal().toPlainString());
        assertEquals("96.00", rezTaxExcluded.getSubTotalAmount().toPlainString());
        assertEquals("16.00", rezTaxExcluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxExcluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxExcluded.getDeliveryCost().toPlainString());
        assertEquals("12.00", rezTaxExcluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("2.00", rezTaxExcluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxExcluded.getTotal().toPlainString());
        assertEquals("132.00", rezTaxExcluded.getListTotalAmount().toPlainString());
        assertEquals("108.00", rezTaxExcluded.getTotalAmount().toPlainString());
        assertEquals("18.00", rezTaxExcluded.getTotalTax().toPlainString());

    }

    @Test
    public void testCalculateOrderMultiDeliveryInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
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
            allowing(order).getPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(order).getNetPrice(); will(returnValue(new BigDecimal("50.00")));
            allowing(order).getGrossPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(order).getListPrice(); will(returnValue(new BigDecimal("90.00")));
            allowing(orderDelivery1).getDetail(); will(returnValue(Arrays.asList(item1)));
            allowing(orderDelivery1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery1).getNetPrice(); will(returnValue(new BigDecimal("8.33")));
            allowing(orderDelivery1).getGrossPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(orderDelivery1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery1).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery1).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(orderDelivery2).getDetail(); will(returnValue(Arrays.asList(item2)));
            allowing(orderDelivery2).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).getNetPrice(); will(returnValue(new BigDecimal("8.33")));
            allowing(orderDelivery2).getGrossPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(orderDelivery2).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery2).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery2).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("16.66")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order);

        assertEquals("90.00", rezTaxIncluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxIncluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxIncluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getPriceSubTotal().toPlainString());
        assertTrue(rezTaxIncluded.isOrderPromoApplied());
        assertEquals("ORDER-25%", rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("60.00", rezTaxIncluded.getSubTotal().toPlainString());
        assertEquals("60.00", rezTaxIncluded.getSubTotalAmount().toPlainString());
        assertEquals("10.00", rezTaxIncluded.getSubTotalTax().toPlainString());
        assertEquals("40.00", rezTaxIncluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("20.00", rezTaxIncluded.getDeliveryCost().toPlainString());
        assertEquals("20.00", rezTaxIncluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("3.34", rezTaxIncluded.getDeliveryTax().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getTotal().toPlainString());
        assertEquals("130.00", rezTaxIncluded.getListTotalAmount().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getTotalAmount().toPlainString());
        assertEquals("13.34", rezTaxIncluded.getTotalTax().toPlainString());

    }



    @Test
    public void testCalculateOrderMultiDeliveryExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
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
            allowing(order).getPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(order).getNetPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(order).getGrossPrice(); will(returnValue(new BigDecimal("72.00")));
            allowing(order).getListPrice(); will(returnValue(new BigDecimal("90.00")));
            allowing(orderDelivery1).getDetail(); will(returnValue(Arrays.asList(item1)));
            allowing(orderDelivery1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery1).getNetPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery1).getGrossPrice(); will(returnValue(new BigDecimal("12.00")));
            allowing(orderDelivery1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(orderDelivery1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery1).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery1).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(orderDelivery2).getDetail(); will(returnValue(Arrays.asList(item2)));
            allowing(orderDelivery2).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).getNetPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).getGrossPrice(); will(returnValue(new BigDecimal("12.00")));
            allowing(orderDelivery2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(orderDelivery2).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(orderDelivery2).isPromoApplied(); will(returnValue(true));
            allowing(orderDelivery2).getAppliedPromo(); will(returnValue("SHIP-50%"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("24.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService).calculate(order);

        assertEquals("90.00", rezTaxExcluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxExcluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxExcluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxExcluded.getPriceSubTotal().toPlainString());
        assertTrue(rezTaxExcluded.isOrderPromoApplied());
        assertEquals("ORDER-25%", rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("60.00", rezTaxExcluded.getSubTotal().toPlainString());
        assertEquals("72.00", rezTaxExcluded.getSubTotalAmount().toPlainString());
        assertEquals("12.00", rezTaxExcluded.getSubTotalTax().toPlainString());
        assertEquals("40.00", rezTaxExcluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("20.00", rezTaxExcluded.getDeliveryCost().toPlainString());
        assertEquals("24.00", rezTaxExcluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("4.00", rezTaxExcluded.getDeliveryTax().toPlainString());
        assertEquals("80.00", rezTaxExcluded.getTotal().toPlainString());
        assertEquals("156.00", rezTaxExcluded.getListTotalAmount().toPlainString());
        assertEquals("96.00", rezTaxExcluded.getTotalAmount().toPlainString());
        assertEquals("16.00", rezTaxExcluded.getTotalTax().toPlainString());

    }


    @Test
    public void testCalculateShoppingCartInclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem ship1 = context.mock(CartItem.class, "ship1");

        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "ctx");
        final Customer customer = context.mock(Customer.class, "customer");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");
        final BigDecimal deliveryListCost = new BigDecimal("20.00");
        final Total deliveryTotal = new TotalImpl(
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost,
                false,
                null,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost
        );

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promoCtx");

        context.checking(new Expectations() {{
            allowing(deliveryCostCalculationStrategy).calculate(cart); will(returnValue(deliveryTotal));
            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(customerService).getCustomerByEmail("bob@doe.com"); will(returnValue(customer));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            allowing(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(shoppingContext).getCountryCode(); will(returnValue("GB"));
            allowing(shoppingContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getProductSkuCode(); will(returnValue("A-001"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("16.66")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("33.33")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-001"); will(returnValue(tax));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(false));
            one(cart).setProductSkuTax("A-001", new BigDecimal("16.66"), new BigDecimal("20.00"), new BigDecimal("20.00"), "VAT", false);
            one(cart).setProductSkuTax("A-002", new BigDecimal("33.33"), new BigDecimal("40.00"), new BigDecimal("20.00"), "VAT", false);
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(ship1)));
            allowing(ship1).getProductSkuCode(); will(returnValue("B-001"));
            allowing(ship1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(ship1).getNetPrice(); will(returnValue(new BigDecimal("8.33")));
            allowing(ship1).getGrossPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(ship1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).isTaxExclusiveOfPrice(); will(returnValue(false));
            allowing(ship1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(ship1).isPromoApplied(); will(returnValue(true));
            allowing(ship1).getAppliedPromo(); will(returnValue("SHIP-50%"));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "B-001"); will(returnValue(tax));
            one(cart).setShippingTax("B-001", new BigDecimal("8.33"), new BigDecimal("10.00"), new BigDecimal("20.00"), "VAT", false);

        }});

        final Total rezTaxIncluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService) {

            @Override
            void applyItemLevelPromotions(final Customer cust, final MutableShoppingCart scart, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }

            @Override
            Total applyOrderLevelPromotions(final Customer cust, final MutableShoppingCart scart, final Total itemTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return itemTotal;
            }

            @Override
            void applyShippingPromotions(final Customer cust, final MutableShoppingCart scart, final Total orderTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }

        }.calculate(cart);


        assertEquals("90.00", rezTaxIncluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxIncluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxIncluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxIncluded.isOrderPromoApplied());
        assertNull(rezTaxIncluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxIncluded.getSubTotal().toPlainString());
        assertEquals("80.00", rezTaxIncluded.getSubTotalAmount().toPlainString());
        assertEquals("13.35", rezTaxIncluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxIncluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxIncluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCost().toPlainString());
        assertEquals("10.00", rezTaxIncluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxIncluded.getAppliedDeliveryPromo());
        assertEquals("1.67", rezTaxIncluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotal().toPlainString());
        assertEquals("110.00", rezTaxIncluded.getListTotalAmount().toPlainString());
        assertEquals("90.00", rezTaxIncluded.getTotalAmount().toPlainString());
        assertEquals("15.02", rezTaxIncluded.getTotalTax().toPlainString());

    }

    @Test
    public void testCalculateShoppingCartExclusiveTax() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCost");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotion");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final CartItem ship1 = context.mock(CartItem.class, "ship1");

        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "ctx");
        final Customer customer = context.mock(Customer.class, "customer");
        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");
        final BigDecimal deliveryListCost = new BigDecimal("20.00");
        final Total deliveryTotal = new TotalImpl(
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost,
                false,
                null,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost,
                Total.ZERO,
                deliveryListCost,
                deliveryListCost
        );

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promoCtx");

        context.checking(new Expectations() {{
            allowing(deliveryCostCalculationStrategy).calculate(cart); will(returnValue(deliveryTotal));
            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));
            allowing(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            allowing(customerService).getCustomerByEmail("bob@doe.com"); will(returnValue(customer));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            allowing(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            allowing(shoppingContext).getCountryCode(); will(returnValue("GB"));
            allowing(shoppingContext).getStateCode(); will(returnValue("GB-CAM"));
            allowing(cart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(cart).getCartItemList(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(item1).isGift(); will(returnValue(false));
            allowing(item1).getProductSkuCode(); will(returnValue("A-001"));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getNetPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getGrossPrice(); will(returnValue(new BigDecimal("24.00")));
            allowing(item1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(item1).getSalePrice(); will(returnValue(new BigDecimal("22.50")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("25.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).isGift(); will(returnValue(false));
            allowing(item2).getProductSkuCode(); will(returnValue("A-002"));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getSalePrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(item2).getNetPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getGrossPrice(); will(returnValue(new BigDecimal("48.00")));
            allowing(item2).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(item2).isTaxExclusiveOfPrice(); will(returnValue(true));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-001"); will(returnValue(tax));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "A-002"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(TAX));
            allowing(tax).isExcluded(); will(returnValue(true));
            one(cart).setProductSkuTax("A-001", new BigDecimal("20.00"), new BigDecimal("24.00"), new BigDecimal("20.00"), "VAT", true);
            one(cart).setProductSkuTax("A-002", new BigDecimal("40.00"), new BigDecimal("48.00"), new BigDecimal("20.00"), "VAT", true);
            allowing(cart).getShippingList(); will(returnValue(Arrays.asList(ship1)));
            allowing(ship1).getProductSkuCode(); will(returnValue("B-001"));
            allowing(ship1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(ship1).getNetPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(ship1).getGrossPrice(); will(returnValue(new BigDecimal("12.00")));
            allowing(ship1).getTaxRate(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).isTaxExclusiveOfPrice(); will(returnValue(true));
            allowing(ship1).getSalePrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(ship1).getQty(); will(returnValue(new BigDecimal("1")));
            allowing(ship1).isPromoApplied(); will(returnValue(true));
            allowing(ship1).getAppliedPromo(); will(returnValue("SHIP-50%"));
            one(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-CAM", "B-001"); will(returnValue(tax));
            one(cart).setShippingTax("B-001", new BigDecimal("10.00"), new BigDecimal("12.00"), new BigDecimal("20.00"), "VAT", true);
        }});

        final Total rezTaxExcluded = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService) {

            @Override
            void applyItemLevelPromotions(final Customer cust, final MutableShoppingCart scart, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }

            @Override
            Total applyOrderLevelPromotions(final Customer cust, final MutableShoppingCart scart, final Total itemTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
                return itemTotal;
            }

            @Override
            void applyShippingPromotions(final Customer cust, final MutableShoppingCart scart, final Total orderTotal, final PromotionContext promoCtx) {
                assertSame(customer, cust);
                assertSame(cart, scart);
                assertSame(promotionContext, promoCtx);
            }
        }.calculate(cart);


        assertEquals("90.00", rezTaxExcluded.getListSubTotal().toPlainString());
        assertEquals("85.00", rezTaxExcluded.getSaleSubTotal().toPlainString());
        assertEquals("40.00", rezTaxExcluded.getNonSaleSubTotal().toPlainString());
        assertEquals("80.00", rezTaxExcluded.getPriceSubTotal().toPlainString());
        assertFalse(rezTaxExcluded.isOrderPromoApplied());
        assertNull(rezTaxExcluded.getAppliedOrderPromo());
        assertEquals("80.00", rezTaxExcluded.getSubTotal().toPlainString());
        assertEquals("96.00", rezTaxExcluded.getSubTotalAmount().toPlainString());
        assertEquals("16.00", rezTaxExcluded.getSubTotalTax().toPlainString());
        assertEquals("20.00", rezTaxExcluded.getDeliveryListCost().toPlainString());
        assertTrue(rezTaxExcluded.isDeliveryPromoApplied());
        assertEquals("10.00", rezTaxExcluded.getDeliveryCost().toPlainString());
        assertEquals("12.00", rezTaxExcluded.getDeliveryCostAmount().toPlainString());
        assertEquals("SHIP-50%", rezTaxExcluded.getAppliedDeliveryPromo());
        assertEquals("2.00", rezTaxExcluded.getDeliveryTax().toPlainString());
        assertEquals("90.00", rezTaxExcluded.getTotal().toPlainString());
        assertEquals("132.00", rezTaxExcluded.getListTotalAmount().toPlainString());
        assertEquals("108.00", rezTaxExcluded.getTotalAmount().toPlainString());
        assertEquals("18.00", rezTaxExcluded.getTotalTax().toPlainString());

    }

}
