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

package org.yes.cart.service.order.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13-10-09
 * Time: 11:26 AM
 */
public class OrderPaymentIntegrationTest extends BaseCoreDBTestCase {

    /**
     * Verify order state and inventory data for offline payment
     */
    @Test
    public void testOfflineOrderPayment() throws Exception {

        final CustomerOrderService orderService = ctx().getBean("customerOrderService", CustomerOrderService.class);
        final PaymentProcessFacade paymentProcessFacade = ctx().getBean("paymentProcessFacade", PaymentProcessFacade.class);

        Customer customer = createCustomer("_payoffline");
        assertFalse(customer.getAddress().isEmpty());

        // check stock
        final BigDecimal[] stockBefore = getStockAndReservationsForSku("CC_TEST1");
        assertTrue(stockBefore[0].compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stockBefore[1].compareTo(BigDecimal.ZERO) >= 0);

        final String gateway = "courierPaymentGatewayLabel";

        final ShoppingCart cart = getStdCart(customer.getEmail(), gateway, "CC_TEST1", "2.00");
        final CustomerOrder customerOrder = orderService.createFromCart(cart, false);
        assertNotNull(customerOrder);
        assertEquals(customerOrder.getOrderStatus(), CustomerOrder.ORDER_STATUS_NONE);

        // Need to set this manually (same on the payment page)
        customerOrder.setPgLabel(gateway);
        orderService.update(customerOrder);

        assertEquals(cart.getGuid(), customerOrder.getGuid());
        assertEquals(cart.getOrderInfo().getPaymentGatewayLabel(), customerOrder.getPgLabel());
        assertEquals(1, customerOrder.getDelivery().size());
        assertEquals(1, customerOrder.getOrderDetail().size());

        final Map params = new HashMap();
        params.put(PaymentMiscParam.CLIENT_IP, "123.123.123.124");


        assertTrue(paymentProcessFacade.pay(cart, params));

        final BigDecimal[] stockAfter = getStockAndReservationsForSku("CC_TEST1");

        // check that we have 2 items reserved
        assertTrue(stockBefore[0].compareTo(stockAfter[0]) == 0);
        assertTrue(stockBefore[1].add(new BigDecimal("2.00")).compareTo(stockAfter[1]) == 0);

        // order is waiting for offline payment to be taken
        final CustomerOrder customerOrderAfter = orderService.findByGuid(customerOrder.getGuid());
        assertEquals(customerOrderAfter.getOrderStatus(), CustomerOrder.ORDER_STATUS_WAITING);

    }

    /**
     * Verify order state and inventory data for offline payment
     */
    @Test
    public void testOnlineOrderPayment() throws Exception {

        final CustomerOrderService orderService = ctx().getBean("customerOrderService", CustomerOrderService.class);
        final PaymentProcessFacade paymentProcessFacade = ctx().getBean("paymentProcessFacade", PaymentProcessFacade.class);
        final CustomerOrderPaymentService customerOrderPaymentService = ctx().getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE, CustomerOrderPaymentService.class);

        Customer customer = createCustomer("_payonline");
        assertFalse(customer.getAddress().isEmpty());

        // check stock
        final BigDecimal[] stockBefore = getStockAndReservationsForSku("CC_TEST1");
        assertTrue(stockBefore[0].compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stockBefore[1].compareTo(BigDecimal.ZERO) >= 0);

        final String gateway = "testPaymentGatewayLabel";

        final ShoppingCart cart = getStdCart(customer.getEmail(), gateway, "CC_TEST1", "2.00");
        final CustomerOrder customerOrder = orderService.createFromCart(cart, false);
        assertNotNull(customerOrder);
        assertEquals(customerOrder.getOrderStatus(), CustomerOrder.ORDER_STATUS_NONE);

        // Need to set this manually (same on the payment page)
        customerOrder.setPgLabel(gateway);
        orderService.update(customerOrder);

        assertEquals(cart.getGuid(), customerOrder.getGuid());
        assertEquals(cart.getOrderInfo().getPaymentGatewayLabel(), customerOrder.getPgLabel());
        assertEquals(1, customerOrder.getDelivery().size());
        assertEquals(1, customerOrder.getOrderDetail().size());

        final Map params = new HashMap();
        params.put(PaymentMiscParam.CLIENT_IP, "123.123.123.124");


        assertTrue(paymentProcessFacade.pay(cart, params));


        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, payments.size());
        final CustomerOrderPayment payment = payments.get(0);
        assertEquals("123.123.123.124", payment.getShopperIpAddress());


        final BigDecimal[] stockAfter = getStockAndReservationsForSku("CC_TEST1");

        // check that we have 2 items taken off stock
        assertTrue(stockBefore[0].subtract(new BigDecimal("2.00")).compareTo(stockAfter[0]) == 0);
        assertTrue(stockBefore[1].compareTo(stockAfter[1]) == 0);

        // order is in progress waiting for shipped transition
        final CustomerOrder customerOrderAfter = orderService.findByGuid(customerOrder.getGuid());
        assertEquals(customerOrderAfter.getOrderStatus(), CustomerOrder.ORDER_STATUS_IN_PROGRESS);

    }

    private BigDecimal[] getStockAndReservationsForSku(final String sku) {
        final WarehouseService warehouseService = ctx().getBean("warehouseService", WarehouseService.class);
        final SkuWarehouseService skuWarehouseService = ctx().getBean("skuWarehouseService", SkuWarehouseService.class);

        final List<Warehouse> shop10warehouses = warehouseService.getByShopId(10L);

        BigDecimal stock = BigDecimal.ZERO;
        BigDecimal reserved = BigDecimal.ZERO;

        for (final Warehouse warehouse : shop10warehouses) {
            final SkuWarehouse inventory = skuWarehouseService.findByWarehouseSku(warehouse, sku);
            if (inventory == null) {
                continue;
            }
            if (inventory.getQuantity() != null) {
                stock = stock.add(inventory.getQuantity());
            }
            if (inventory.getReserved() != null) {
                reserved = reserved.add(inventory.getReserved());
            }
        }

        return new BigDecimal[] { stock, reserved };
    }

    protected ShoppingCart getStdCart(final String customerEmail,
                                      final String paymentLabel,
                                      final String... skuAndQty) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        for (int i = 0; i < skuAndQty.length; i += 2) {
            Map<String, String> param = new HashMap<String, String>();
            param.put(ShoppingCartCommand.CMD_SETQTYSKU, skuAndQty[i]);
            param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, skuAndQty[i + 1]);
            commands.execute(shoppingCart, (Map) param);
        }

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETPGLABEL, paymentLabel);
        commands.execute(shoppingCart, (Map) param);

        return shoppingCart;
    }


}
