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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.dto.DtoShoppingCartService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.OrderDisassembler;
import org.yes.cart.service.order.impl.DeliveryAssemblerImpl;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 16/03/2016
 * Time: 18:06
 */
public class DtoShoppingCartServiceImplTezt extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private OrderDisassembler orderDisassembler;
    private DeliveryAssemblerImpl deliveryAssembler;
    private CustomerOrderService customerOrderService;

    private DtoShoppingCartService dtoShoppingCartService;

    @Before
    public void setUp() {

        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        orderDisassembler = (OrderDisassembler) ctx().getBean(ServiceSpringKeys.ORDER_DISASSEMBLER);
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);

        dtoShoppingCartService = (DtoShoppingCartService) ctx().getBean(DtoServiceSpringKeys.DTO_SHOPPINGCART_SERVICE);
        super.setUp();
    }

    @Test
    public void testBasicAmendmentCartSequence() throws Exception {

        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), true);

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
        assertNull(customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4551.88"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getGrossPrice());


        // 1. Test assembly

        final ShoppingCart reassembledCart = dtoShoppingCartService.create(customerOrder.getOrdernum(), true);

        assertNotNull(reassembledCart);
        assertTrue(reassembledCart.isModified()); // ready to be saved

        assertEquals(customerOrder.getOrderDetail().size(), reassembledCart.getCartItemList().size());
        assertEquals(0, reassembledCart.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, reassembledCart.getCoupons().size());

        for (final CartItem orderItem : customerOrder.getOrderDetail()) {

            final int index = reassembledCart.indexOfProductSku(orderItem.getProductSkuCode());
            assertTrue(index != -1);

            final CartItem cartItem = reassembledCart.getCartItemList().get(index);

            assertEquals(orderItem.getQty().setScale(Constants.DEFAULT_SCALE), cartItem.getQty());
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
        assertEquals("SHOIP1", reassembledSC.getShopCode());
        assertEquals("CA", reassembledSC.getCountryCode());
        assertNull(reassembledSC.getStateCode());
        assertNotNull(reassembledSC.getCustomerShops());
        assertEquals(1, reassembledSC.getCustomerShops().size());
        assertEquals("SHOIP1", reassembledSC.getCustomerShops().get(0));
        assertNull(reassembledSC.getResolvedIp());

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

        final String removedSku = reassembledCart.getCartItemList().get(0).getProductSkuCode();
        final BigDecimal removedQuantity = reassembledCart.getCartItemList().get(0).getQty();


        // 2. Test removal

        dtoShoppingCartService.removeLine(reassembledCart.getGuid(), removedSku);

        final ShoppingCart afterRemove = dtoShoppingCartService.getById(reassembledCart.getGuid());
        assertEquals(customerOrder.getOrderDetail().size() - 1, afterRemove.getCartItemList().size());
        assertEquals(0, afterRemove.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, afterRemove.getCoupons().size());

        final Total afterRemoveTotal = afterRemove.getTotal();

        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getListSubTotal());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getPriceSubTotal());
        assertFalse(afterRemoveTotal.isOrderPromoApplied());
        assertNull(afterRemoveTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getSubTotal());
        assertEquals(new BigDecimal("880.19"), afterRemoveTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("0.00"), afterRemoveTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("0.00"), afterRemoveTotal.getDeliveryCost());
        assertFalse(afterRemoveTotal.isDeliveryPromoApplied());
        assertNull(afterRemoveTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("0.00"), afterRemoveTotal.getDeliveryTax());
        assertEquals(new BigDecimal("0.00"), afterRemoveTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getTotal());
        assertEquals(new BigDecimal("880.19"), afterRemoveTotal.getTotalTax());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5272.92"), afterRemoveTotal.getTotalAmount());


        // 3. Test addition

        dtoShoppingCartService.updateLineQuantity(reassembledCart.getGuid(), removedSku, removedQuantity);


        final ShoppingCart afterAdd = dtoShoppingCartService.getById(reassembledCart.getGuid());
        assertEquals(customerOrder.getOrderDetail().size(), afterAdd.getCartItemList().size());
        assertEquals(0, afterAdd.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, afterAdd.getCoupons().size());

        final Total afterAddTotal = afterAdd.getTotal();

        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getPriceSubTotal());
        assertFalse(afterAddTotal.isOrderPromoApplied());
        assertNull(afterAddTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getSubTotal());
        assertEquals(new BigDecimal("912.03"), afterAddTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("0.00"), afterAddTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("0.00"), afterAddTotal.getDeliveryCost());
        assertFalse(afterAddTotal.isDeliveryPromoApplied());
        assertNull(afterAddTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("0.00"), afterAddTotal.getDeliveryTax());
        assertEquals(new BigDecimal("0.00"), afterAddTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getTotal());
        assertEquals(new BigDecimal("912.03"), afterAddTotal.getTotalTax());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5463.91"), afterAddTotal.getTotalAmount());


        // 4. Test change price

        dtoShoppingCartService.updateLinePrice(reassembledCart.getGuid(), removedSku, new BigDecimal("150.00"), "AUTH001");


        final ShoppingCart afterChange = dtoShoppingCartService.getById(reassembledCart.getGuid());
        assertEquals(customerOrder.getOrderDetail().size(), afterChange.getCartItemList().size());
        assertEquals(0, afterChange.getShippingList().size()); // order assembler does not create deliveries
        assertEquals(0, afterChange.getCoupons().size());

        final Total afterChangeTotal = afterChange.getTotal();

        assertEquals(new BigDecimal("5463.91"), afterChangeTotal.getListSubTotal());
        assertEquals(new BigDecimal("5463.91"), afterChangeTotal.getSaleSubTotal());
        assertEquals(new BigDecimal("5463.91"), afterChangeTotal.getNonSaleSubTotal());
        assertEquals(new BigDecimal("5422.92"), afterChangeTotal.getPriceSubTotal());
        assertFalse(afterChangeTotal.isOrderPromoApplied());
        assertNull(afterChangeTotal.getAppliedOrderPromo());
        assertEquals(new BigDecimal("5422.92"), afterChangeTotal.getSubTotal());
        assertEquals(new BigDecimal("905.19"), afterChangeTotal.getSubTotalTax());
        assertEquals(new BigDecimal("5422.92"), afterChangeTotal.getSubTotalAmount());
        assertEquals(new BigDecimal("0.00"), afterChangeTotal.getDeliveryListCost());
        assertEquals(new BigDecimal("0.00"), afterChangeTotal.getDeliveryCost());
        assertFalse(afterChangeTotal.isDeliveryPromoApplied());
        assertNull(afterChangeTotal.getAppliedDeliveryPromo());
        assertEquals(new BigDecimal("0.00"), afterChangeTotal.getDeliveryTax());
        assertEquals(new BigDecimal("0.00"), afterChangeTotal.getDeliveryCostAmount());
        assertEquals(new BigDecimal("5422.92"), afterChangeTotal.getTotal());
        assertEquals(new BigDecimal("905.19"), afterChangeTotal.getTotalTax());
        assertEquals(new BigDecimal("5463.91"), afterChangeTotal.getListTotalAmount());
        assertEquals(new BigDecimal("5422.92"), afterChangeTotal.getTotalAmount());


    }
}