/*
 * Copyright 2009 Inspire-Software.com
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
package org.yes.cart.bulkjob.order;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test awaiting orders.
 */
public class BulkAwaitingInventoryDeliveriesProcessorImplTest extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;
    private OrderStateManager orderStateManager;
    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;

    private CronJobProcessor bulkAwaitingInventoryDeliveriesProcessor;


    @Override
    @Before
    public void setUp()  {
        customerOrderService = ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE, CustomerOrderService.class);
        orderStateManager =  ctx().getBean(ServiceSpringKeys.ORDER_STATE_MANAGER, OrderStateManager.class);
        warehouseService =  ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE, WarehouseService.class);
        skuWarehouseService =  ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE, SkuWarehouseService.class);
        bulkAwaitingInventoryDeliveriesProcessor =  ctx().getBean("bulkAwaitingInventoryDeliveriesProcessor", CronJobProcessor.class);
        super.setUp();
    }


    @Test
    public void testProcessAwaitingOrdersPreorderToBackorderThenReleased() throws Exception {

        final Map<String, Object> ctx = configureJobContext("bulkAwaitingInventoryDeliveriesProcessor", null);

        Warehouse warehouse = warehouseService.findById(1L);

        LocalDateTime calendar = LocalDateTime.now().plusHours(1);

        SkuWarehouse inventory;

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, "PREORDER-BACK-TO-FLOW4");
        inventory.setReleaseDate(calendar);
        skuWarehouseService.update(inventory);

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, "PREORDER-BACK-TO-FLOW5");
        inventory.setReleaseDate(calendar);
        skuWarehouseService.update(inventory);

        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCartWithPreorderItems("PREORDER-BACK-TO-FLOW4", "PREORDER-BACK-TO-FLOW5", true);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        customerOrderService.update(order);

        orderStateManager.fireTransition(
                new OrderEventImpl(OrderStateManager.EVT_PENDING,
                        order,
                        null,
                        new HashMap()
                )

        );

        customerOrderService.update(order);

        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT, delivery.getDeliveryStatus());
            assertNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status1 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status1);
        assertFalse(status1.getReport(), status1.getReport().contains(order.getOrdernum()));

        //No changes
        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT, delivery.getDeliveryStatus());
            assertNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

        calendar = LocalDateTime.now().minusHours(1);

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, "PREORDER-BACK-TO-FLOW4");
        inventory.setReleaseDate(calendar);
        skuWarehouseService.update(inventory);

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, "PREORDER-BACK-TO-FLOW5");
        inventory.setReleaseDate(calendar);
        skuWarehouseService.update(inventory);

        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status2 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status2);
        final String expected2 = "Updated customer order " + order.getOrdernum()
                + " delivery " + order.getDelivery().iterator().next().getDeliveryNum() + ", event evt.delivery.allowed.timeout";
        assertTrue(status2.getReport() + "\ndoes not contain\n" + expected2, status2.getReport().contains(expected2));

        //wait for inventory, this is BACKORDER flow, only alerts are raised for this (not emails)
        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
            assertNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

        //add inventory
        //need 2 items to push order back to life cycle
        skuWarehouseService.credit(warehouse, "PREORDER-BACK-TO-FLOW4", BigDecimal.TEN);
        skuWarehouseService.credit(warehouse, "PREORDER-BACK-TO-FLOW5", BigDecimal.TEN);

        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status3 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status3);
        final String expected3 = "Updated customer order " + order.getOrdernum()
                + " delivery " + order.getDelivery().iterator().next().getDeliveryNum() + ", event evt.delivery.allowed.quantity";
        assertTrue(status3.getReport() + "\ndoes not contain\n" + expected3, status3.getReport().contains(expected3));

        // inventory allocated
        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
            assertNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

    }



    @Test
    public void testProcessAwaitingOrdersStandardToOutOfStockThenReleased() throws Exception {

        final Map<String, Object> ctx = configureJobContext("bulkAwaitingInventoryDeliveriesProcessor", null);

        Warehouse warehouse = warehouseService.findById(1L);

        SkuWarehouse inventory;

        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCartWithPreorderItems("CC_TEST4", "CC_TEST5", true);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        customerOrderService.update(order);

        orderStateManager.fireTransition(
                new OrderEventImpl(OrderStateManager.EVT_PENDING,
                        order,
                        null,
                        new HashMap()
                )

        );

        customerOrderService.update(order);

        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT, delivery.getDeliveryStatus());
            assertNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

        // Exhaust allocation manually
        BigDecimal oldQuantity = null;

        inventory = skuWarehouseService.findByWarehouseSku(warehouse, "CC_TEST4");
        oldQuantity = inventory.getQuantity();
        inventory.setQuantity(BigDecimal.ZERO);
        skuWarehouseService.update(inventory);

        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status1 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status1);
        final String expected1 = "Marked customer order " + order.getOrdernum()
                + " delivery " + order.getDelivery().iterator().next().getDeliveryNum() + " ... OOS notification sent";
        assertTrue(status1.getReport() + "\ndoes not contain\n" + expected1, status1.getReport().contains(expected1));

        //wait for inventory, this is STANDARD flow, email alert will be generated
        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT, delivery.getDeliveryStatus());
            assertNotNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

        //add inventory
        //need items to push order back to life cycle
        skuWarehouseService.credit(warehouse, "CC_TEST4", oldQuantity);

        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status2 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status2);
        final String expected2 = "Updated customer order " + order.getOrdernum()
                + " delivery " + order.getDelivery().iterator().next().getDeliveryNum() + ", event evt.process.allocation";
        assertTrue(status2.getReport() + "\ndoes not contain\n" + expected2, status2.getReport().contains(expected2));

        // inventory allocated
        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
            assertNotNull(order.getValue("OUT_OF_STOCK_NOTIFICATION:" + delivery.getDeliveryNum()));
        }

    }


}
