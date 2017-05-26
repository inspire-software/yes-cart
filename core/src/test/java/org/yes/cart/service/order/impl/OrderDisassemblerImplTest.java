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

package org.yes.cart.service.order.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.OrderDisassembler;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 11/03/2016
 * Time: 07:50
 */
public class OrderDisassemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private OrderDisassembler orderDisassembler;
    private DeliveryAssemblerImpl deliveryAssembler;
    private CustomerOrderService customerOrderService;


    @Before
    public void setUp()  {
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        orderDisassembler = (OrderDisassembler) ctx().getBean(ServiceSpringKeys.ORDER_DISASSEMBLER);
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);
        super.setUp();
    }


    @Test
    public void testAssembleShoppingCartOsNoneNoDeliveries() throws Exception {
        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), true);
        setIPAddress(shoppingCart, "127.0.0.1");

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);

        assertNotNull(customerOrder);
        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertTrue("Order delivery assembler was not applied", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals("127.0.0.1", customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4551.88"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getGrossPrice());

        final ShoppingCart reassembledCart = orderDisassembler.assembleShoppingCart(customerOrder, false);

        assertNotNull(reassembledCart);
        assertTrue(reassembledCart.isModified()); // ready to be saved

        assertEquals(customerOrder.getOrderDetail().size(), reassembledCart.getCartItemList().size());
        assertEquals(0, reassembledCart.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, reassembledCart.getCoupons().size());

        for (final CartItem orderItem : customerOrder.getOrderDetail()) {

            final int index = reassembledCart.indexOfProductSku(orderItem.getProductSkuCode());
            assertTrue(index != -1);

            final CartItem cartItem = reassembledCart.getCartItemList().get(index);

            assertEquals(orderItem.getQty(), cartItem.getQty());
            assertEquals(orderItem.getPrice(), cartItem.getPrice());
            assertEquals(orderItem.getSalePrice(), cartItem.getSalePrice());
            assertEquals(orderItem.getListPrice(), cartItem.getListPrice());
            assertEquals(orderItem.getNetPrice(), cartItem.getNetPrice());
            assertEquals(orderItem.getGrossPrice(), cartItem.getGrossPrice());
            assertEquals(orderItem.getTaxRate(), cartItem.getTaxRate());
            assertEquals(orderItem.getTaxCode(), cartItem.getTaxCode());
            assertEquals(orderItem.isTaxExclusiveOfPrice(), cartItem.isTaxExclusiveOfPrice());
            assertEquals(orderItem.isGift(), cartItem.isGift());
            assertEquals(orderItem.isFixedPrice(), cartItem.isFixedPrice());
            assertEquals(orderItem.isPromoApplied(), cartItem.isPromoApplied());
            assertEquals(orderItem.getAppliedPromo(), cartItem.getAppliedPromo());
            assertEquals(orderItem.getProductSkuCode(), cartItem.getProductSkuCode());

        }

        final Total reassembledTotal = reassembledCart.getTotal();

        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getPriceSubTotal());
        assertFalse(reassembledTotal.isOrderPromoApplied());
        assertNull(reassembledTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSubTotal());
        assertEquals(new BigDecimal("912.03"), reassembledTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryCost());
        assertFalse(reassembledTotal.isDeliveryPromoApplied());
        assertNull(reassembledTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryTax());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getTotal());
        assertEquals(new BigDecimal("912.03"), reassembledTotal.getTotalTax());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getTotalAmount());

        assertEquals(customerOrder.getEmail(), reassembledCart.getCustomerEmail());
        assertEquals(customerOrder.getCurrency(), reassembledCart.getCurrencyCode());
        assertEquals(customerOrder.getLocale(), reassembledCart.getCurrentLocale());
        assertEquals(customerOrder.getOrdernum(), reassembledCart.getOrdernum());

        final ShoppingContext reassembledSC = reassembledCart.getShoppingContext();
        assertNotNull(reassembledSC.getCustomerName());
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getFirstname()));
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getLastname()));
        assertEquals(10L, reassembledSC.getShopId());
        assertEquals(10L, reassembledSC.getCustomerShopId());
        assertEquals("SHOIP1", reassembledSC.getShopCode());
        assertEquals("CA", reassembledSC.getCountryCode());
        assertNull(reassembledSC.getStateCode());
        assertNotNull(reassembledSC.getCustomerShops());
        assertEquals(1, reassembledSC.getCustomerShops().size());
        assertEquals("SHOIP1", reassembledSC.getCustomerShops().get(0));
        assertEquals("127.0.0.1", reassembledSC.getResolvedIp());

        final OrderInfo reassembledOI = reassembledCart.getOrderInfo();
        assertNull(reassembledOI.getPaymentGatewayLabel());
        assertFalse(reassembledOI.isMultipleDelivery());
        assertFalse(reassembledOI.isSeparateBillingAddress());
        assertFalse(reassembledOI.isBillingAddressNotRequired());
        assertFalse(reassembledOI.isDeliveryAddressNotRequired());
        assertNotNull(reassembledOI.getCarrierSlaId());
        assertTrue(reassembledOI.getCarrierSlaId().isEmpty());
        assertNotNull(reassembledOI.getBillingAddressId());
        assertNotNull(reassembledOI.getDeliveryAddressId());


    }


    @Test
    public void testAssembleShoppingCartOsNoneNoDeliveriesWithOffers() throws Exception {
        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), true);
        setIPAddress(shoppingCart, "127.0.0.1");
        setOffer(shoppingCart, "CC_TEST1", new BigDecimal("150.00"));

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);

        assertNotNull(customerOrder);
        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertTrue("Order delivery assembler was not applied", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals("127.0.0.1", customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5422.92"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4517.73"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5422.92"), customerOrder.getGrossPrice());

        final ShoppingCart reassembledCart = orderDisassembler.assembleShoppingCart(customerOrder, false);

        assertNotNull(reassembledCart);
        assertTrue(reassembledCart.isModified()); // ready to be saved

        assertEquals(customerOrder.getOrderDetail().size(), reassembledCart.getCartItemList().size());
        assertEquals(0, reassembledCart.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, reassembledCart.getCoupons().size());

        for (final CartItem orderItem : customerOrder.getOrderDetail()) {

            final int index = reassembledCart.indexOfProductSku(orderItem.getProductSkuCode());
            assertTrue(index != -1);

            final CartItem cartItem = reassembledCart.getCartItemList().get(index);

            assertEquals(orderItem.getQty(), cartItem.getQty());
            assertEquals(orderItem.getPrice(), cartItem.getPrice());
            assertEquals(orderItem.getSalePrice(), cartItem.getSalePrice());
            assertEquals(orderItem.getListPrice(), cartItem.getListPrice());
            assertEquals(orderItem.getNetPrice(), cartItem.getNetPrice());
            assertEquals(orderItem.getGrossPrice(), cartItem.getGrossPrice());
            assertEquals(orderItem.getTaxRate(), cartItem.getTaxRate());
            assertEquals(orderItem.getTaxCode(), cartItem.getTaxCode());
            assertEquals(orderItem.isTaxExclusiveOfPrice(), cartItem.isTaxExclusiveOfPrice());
            assertEquals(orderItem.isGift(), cartItem.isGift());
            assertEquals(orderItem.isPromoApplied(), cartItem.isPromoApplied());
            assertEquals(orderItem.isFixedPrice(), cartItem.isFixedPrice());
            assertEquals(orderItem.getAppliedPromo(), cartItem.getAppliedPromo());
            assertEquals(orderItem.getProductSkuCode(), cartItem.getProductSkuCode());

        }

        final Total reassembledTotal = reassembledCart.getTotal();

        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getPriceSubTotal());
        assertFalse(reassembledTotal.isOrderPromoApplied());
        assertNull(reassembledTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getSubTotal());
        assertEquals(new BigDecimal("905.19"), reassembledTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryCost());
        assertFalse(reassembledTotal.isDeliveryPromoApplied());
        assertNull(reassembledTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryTax());
        assertEquals(new BigDecimal("0.00"), reassembledTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getTotal());
        assertEquals(new BigDecimal("905.19"), reassembledTotal.getTotalTax());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getTotalAmount());

        assertEquals(customerOrder.getEmail(), reassembledCart.getCustomerEmail());
        assertEquals(customerOrder.getCurrency(), reassembledCart.getCurrencyCode());
        assertEquals(customerOrder.getLocale(), reassembledCart.getCurrentLocale());
        assertEquals(customerOrder.getOrdernum(), reassembledCart.getOrdernum());

        final ShoppingContext reassembledSC = reassembledCart.getShoppingContext();
        assertNotNull(reassembledSC.getCustomerName());
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getFirstname()));
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getLastname()));
        assertEquals(10L, reassembledSC.getShopId());
        assertEquals(10L, reassembledSC.getCustomerShopId());
        assertEquals("SHOIP1", reassembledSC.getShopCode());
        assertEquals("CA", reassembledSC.getCountryCode());
        assertNull(reassembledSC.getStateCode());
        assertNotNull(reassembledSC.getCustomerShops());
        assertEquals(1, reassembledSC.getCustomerShops().size());
        assertEquals("SHOIP1", reassembledSC.getCustomerShops().get(0));
        assertEquals("127.0.0.1", reassembledSC.getResolvedIp());

        final OrderInfo reassembledOI = reassembledCart.getOrderInfo();
        assertNull(reassembledOI.getPaymentGatewayLabel());
        assertFalse(reassembledOI.isMultipleDelivery());
        assertFalse(reassembledOI.isSeparateBillingAddress());
        assertFalse(reassembledOI.isBillingAddressNotRequired());
        assertFalse(reassembledOI.isDeliveryAddressNotRequired());
        assertTrue(reassembledOI.getCarrierSlaId().isEmpty());
        assertNotNull(reassembledOI.getBillingAddressId());
        assertNotNull(reassembledOI.getDeliveryAddressId());


    }



    @Test
    public void testAssembleShoppingCartOsNoneWithDeliveries() throws Exception {
        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), true);
        setIPAddress(shoppingCart, "127.0.0.1");

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);

        assertNotNull(customerOrder);
        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertFalse("Order delivery assembler was not applied", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals("127.0.0.1", customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4551.88"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getGrossPrice());

        final ShoppingCart reassembledCart = orderDisassembler.assembleShoppingCart(customerOrder, false);

        assertNotNull(reassembledCart);
        assertTrue(reassembledCart.isModified()); // ready to be saved

        assertEquals(customerOrder.getOrderDetail().size(), reassembledCart.getCartItemList().size());

        assertEquals(5, reassembledCart.getShippingList().size());
        for (final CartItem shipping : reassembledCart.getShippingList()) {
            assertEquals(new BigDecimal("1.00"), shipping.getQty());
            assertEquals(new BigDecimal("16.77"), shipping.getPrice());
            assertEquals(new BigDecimal("16.77"), shipping.getSalePrice());
            assertEquals(new BigDecimal("16.77"), shipping.getListPrice());
            assertEquals(new BigDecimal("13.97"), shipping.getNetPrice());
            assertEquals(new BigDecimal("16.77"), shipping.getGrossPrice());
            assertEquals(new BigDecimal("20.00"), shipping.getTaxRate());
            assertEquals("VAT", shipping.getTaxCode());
            assertFalse(shipping.isTaxExclusiveOfPrice());
            assertFalse(shipping.isGift());
            assertFalse(shipping.isPromoApplied());
            assertNull(shipping.getAppliedPromo());
            assertEquals("1_CARRIERSLA", shipping.getProductSkuCode());
        }

        assertEquals(0, reassembledCart.getCoupons().size());

        for (final CartItem orderItem : customerOrder.getOrderDetail()) {

            final int index = reassembledCart.indexOfProductSku(orderItem.getProductSkuCode());
            assertTrue(index != -1);

            final CartItem cartItem = reassembledCart.getCartItemList().get(index);

            assertEquals(orderItem.getQty(), cartItem.getQty());
            assertEquals(orderItem.getPrice(), cartItem.getPrice());
            assertEquals(orderItem.getSalePrice(), cartItem.getSalePrice());
            assertEquals(orderItem.getListPrice(), cartItem.getListPrice());
            assertEquals(orderItem.getNetPrice(), cartItem.getNetPrice());
            assertEquals(orderItem.getGrossPrice(), cartItem.getGrossPrice());
            assertEquals(orderItem.getTaxRate(), cartItem.getTaxRate());
            assertEquals(orderItem.getTaxCode(), cartItem.getTaxCode());
            assertEquals(orderItem.isTaxExclusiveOfPrice(), cartItem.isTaxExclusiveOfPrice());
            assertEquals(orderItem.isGift(), cartItem.isGift());
            assertEquals(orderItem.isFixedPrice(), cartItem.isFixedPrice());
            assertEquals(orderItem.isPromoApplied(), cartItem.isPromoApplied());
            assertEquals(orderItem.getAppliedPromo(), cartItem.getAppliedPromo());
            assertEquals(orderItem.getProductSkuCode(), cartItem.getProductSkuCode());

        }

        final Total reassembledTotal = reassembledCart.getTotal();

        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getPriceSubTotal());
        assertFalse(reassembledTotal.isOrderPromoApplied());
        assertNull(reassembledTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSubTotal());
        assertEquals(new BigDecimal("912.03"), reassembledTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryCost());
        assertFalse(reassembledTotal.isDeliveryPromoApplied());
        assertNull(reassembledTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("14.00"), reassembledTotal.getDeliveryTax());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5547.76"), reassembledTotal.getTotal());
        assertEquals(new BigDecimal("926.03"), reassembledTotal.getTotalTax());
        assertEquals(new BigDecimal("5547.76"), reassembledTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5547.76"), reassembledTotal.getTotalAmount());

        assertEquals(customerOrder.getEmail(), reassembledCart.getCustomerEmail());
        assertEquals(customerOrder.getCurrency(), reassembledCart.getCurrencyCode());
        assertEquals(customerOrder.getLocale(), reassembledCart.getCurrentLocale());
        assertEquals(customerOrder.getOrdernum(), reassembledCart.getOrdernum());

        final ShoppingContext reassembledSC = reassembledCart.getShoppingContext();
        assertNotNull(reassembledSC.getCustomerName());
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getFirstname()));
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getLastname()));
        assertEquals(10L, reassembledSC.getShopId());
        assertEquals(10L, reassembledSC.getCustomerShopId());
        assertEquals("SHOIP1", reassembledSC.getShopCode());
        assertEquals("CA", reassembledSC.getCountryCode());
        assertNull(reassembledSC.getStateCode());
        assertNotNull(reassembledSC.getCustomerShops());
        assertEquals(1, reassembledSC.getCustomerShops().size());
        assertEquals("SHOIP1", reassembledSC.getCustomerShops().get(0));
        assertEquals("127.0.0.1", reassembledSC.getResolvedIp());

        final OrderInfo reassembledOI = reassembledCart.getOrderInfo();
        assertNull(reassembledOI.getPaymentGatewayLabel());
        assertTrue(reassembledOI.isMultipleDelivery());
        assertFalse(reassembledOI.isSeparateBillingAddress());
        assertFalse(reassembledOI.isBillingAddressNotRequired());
        assertFalse(reassembledOI.isDeliveryAddressNotRequired());
        assertFalse(reassembledOI.getCarrierSlaId().isEmpty());
        assertEquals(Long.valueOf(1L), reassembledOI.getCarrierSlaId().values().iterator().next());
        assertNotNull(reassembledOI.getBillingAddressId());
        assertNotNull(reassembledOI.getDeliveryAddressId());


    }



    @Test
    public void testAssembleShoppingCartOsNoneWithDeliveriesWithOffers() throws Exception {
        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), true);
        setIPAddress(shoppingCart, "127.0.0.1");
        setOffer(shoppingCart, "CC_TEST1", new BigDecimal("150.00"));

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);

        assertNotNull(customerOrder);
        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertFalse("Order delivery assembler was not applied", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals("127.0.0.1", customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5422.92"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4517.73"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5422.92"), customerOrder.getGrossPrice());

        final ShoppingCart reassembledCart = orderDisassembler.assembleShoppingCart(customerOrder, false);

        assertNotNull(reassembledCart);
        assertTrue(reassembledCart.isModified()); // ready to be saved

        assertEquals(customerOrder.getOrderDetail().size(), reassembledCart.getCartItemList().size());

        assertEquals(5, reassembledCart.getShippingList().size());
        for (final CartItem shipping : reassembledCart.getShippingList()) {
            assertEquals(new BigDecimal("1.00"), shipping.getQty());
            assertEquals(new BigDecimal("16.77"), shipping.getPrice());
            assertEquals(new BigDecimal("16.77"), shipping.getSalePrice());
            assertEquals(new BigDecimal("16.77"), shipping.getListPrice());
            assertEquals(new BigDecimal("13.97"), shipping.getNetPrice());
            assertEquals(new BigDecimal("16.77"), shipping.getGrossPrice());
            assertEquals(new BigDecimal("20.00"), shipping.getTaxRate());
            assertEquals("VAT", shipping.getTaxCode());
            assertFalse(shipping.isTaxExclusiveOfPrice());
            assertFalse(shipping.isGift());
            assertFalse(shipping.isPromoApplied());
            assertNull(shipping.getAppliedPromo());
            assertEquals("1_CARRIERSLA", shipping.getProductSkuCode());
        }

        assertEquals(0, reassembledCart.getCoupons().size());

        for (final CartItem orderItem : customerOrder.getOrderDetail()) {

            final int index = reassembledCart.indexOfProductSku(orderItem.getProductSkuCode());
            assertTrue(index != -1);

            final CartItem cartItem = reassembledCart.getCartItemList().get(index);

            assertEquals(orderItem.getQty(), cartItem.getQty());
            assertEquals(orderItem.getPrice(), cartItem.getPrice());
            assertEquals(orderItem.getSalePrice(), cartItem.getSalePrice());
            assertEquals(orderItem.getListPrice(), cartItem.getListPrice());
            assertEquals(orderItem.getNetPrice(), cartItem.getNetPrice());
            assertEquals(orderItem.getGrossPrice(), cartItem.getGrossPrice());
            assertEquals(orderItem.getTaxRate(), cartItem.getTaxRate());
            assertEquals(orderItem.getTaxCode(), cartItem.getTaxCode());
            assertEquals(orderItem.isTaxExclusiveOfPrice(), cartItem.isTaxExclusiveOfPrice());
            assertEquals(orderItem.isGift(), cartItem.isGift());
            assertEquals(orderItem.isFixedPrice(), cartItem.isFixedPrice());
            assertEquals(orderItem.isPromoApplied(), cartItem.isPromoApplied());
            assertEquals(orderItem.getAppliedPromo(), cartItem.getAppliedPromo());
            assertEquals(orderItem.getProductSkuCode(), cartItem.getProductSkuCode());

        }

        final Total reassembledTotal = reassembledCart.getTotal();

        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), reassembledTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getPriceSubTotal());
        assertFalse(reassembledTotal.isOrderPromoApplied());
        assertNull(reassembledTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getSubTotal());
        assertEquals(new BigDecimal("905.19"), reassembledTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5422.92"), reassembledTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryCost());
        assertFalse(reassembledTotal.isDeliveryPromoApplied());
        assertNull(reassembledTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("14.00"), reassembledTotal.getDeliveryTax());
        assertEquals(new BigDecimal("83.85"), reassembledTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5506.77"), reassembledTotal.getTotal());
        assertEquals(new BigDecimal("919.19"), reassembledTotal.getTotalTax());
        assertEquals(new BigDecimal("5547.76"), reassembledTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5506.77"), reassembledTotal.getTotalAmount());

        assertEquals(customerOrder.getEmail(), reassembledCart.getCustomerEmail());
        assertEquals(customerOrder.getCurrency(), reassembledCart.getCurrencyCode());
        assertEquals(customerOrder.getLocale(), reassembledCart.getCurrentLocale());
        assertEquals(customerOrder.getOrdernum(), reassembledCart.getOrdernum());

        final ShoppingContext reassembledSC = reassembledCart.getShoppingContext();
        assertNotNull(reassembledSC.getCustomerName());
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getFirstname()));
        assertTrue(reassembledSC.getCustomerName().contains(customerOrder.getLastname()));
        assertEquals(10L, reassembledSC.getShopId());
        assertEquals(10L, reassembledSC.getCustomerShopId());
        assertEquals("SHOIP1", reassembledSC.getShopCode());
        assertEquals("CA", reassembledSC.getCountryCode());
        assertNull(reassembledSC.getStateCode());
        assertNotNull(reassembledSC.getCustomerShops());
        assertEquals(1, reassembledSC.getCustomerShops().size());
        assertEquals("SHOIP1", reassembledSC.getCustomerShops().get(0));
        assertEquals("127.0.0.1", reassembledSC.getResolvedIp());

        final OrderInfo reassembledOI = reassembledCart.getOrderInfo();
        assertNull(reassembledOI.getPaymentGatewayLabel());
        assertTrue(reassembledOI.isMultipleDelivery());
        assertFalse(reassembledOI.isSeparateBillingAddress());
        assertFalse(reassembledOI.isBillingAddressNotRequired());
        assertFalse(reassembledOI.isDeliveryAddressNotRequired());
        assertFalse(reassembledOI.getCarrierSlaId().isEmpty());
        assertEquals(Long.valueOf(1L), reassembledOI.getCarrierSlaId().values().iterator().next());
        assertNotNull(reassembledOI.getBillingAddressId());
        assertNotNull(reassembledOI.getDeliveryAddressId());


    }


    private void setIPAddress(final ShoppingCart shoppingCart, final String ip) {
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_INTERNAL_SETIP, ip);
        commands.execute(shoppingCart, (Map) params);
    }

    private void setOffer(final ShoppingCart shoppingCart, final String sku, final BigDecimal offer) {

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_SETPRICE, sku);
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, offer.toPlainString());
        params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, "CC0001");

        commands.execute(shoppingCart, (Map) params);

    }


    private Map<String, Boolean> getMultiSelection(ShoppingCart cart) {
        final Map<String, Boolean> single = new HashMap<String, Boolean>();
        final boolean selected = cart.getOrderInfo().isMultipleDelivery();
        for (final Map.Entry<String, Boolean> allowed : cart.getOrderInfo().getMultipleDeliveryAvailable().entrySet()) {
            single.put(allowed.getKey(), !selected || !allowed.getValue());
        }
        return single;
    }


}