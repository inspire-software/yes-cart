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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestSkuWarehouseServiceImpl extends BaseCoreDBTestCase {

    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private CustomerOrderService customerOrderService;
    private OrderStateManager orderStateManager;

    @Before
    public void setUp()  {
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        orderStateManager = (OrderStateManager) ctx().getBean(ServiceSpringKeys.ORDER_STATE_MANAGER);
        super.setUp();

    }

    @Test
    public void testPushOrdersAwaitingForInventory() throws Exception {

        final Map<String, Object> ctx = configureJobContext("bulkAwaitingInventoryDeliveriesProcessor", null);

        CronJobProcessor bulkAwaitingInventoryDeliveriesProcessor =  ctx().getBean("bulkAwaitingInventoryDeliveriesProcessor", CronJobProcessor.class);

        Customer cust = createCustomer();

        ShoppingCart shoppingCart = getShoppingCartWithPreorderItems("BACKORDER-BACK-TO-FLOW1", "BACKORDER-BACK-TO-FLOW2", true);

        prepareMultiDeliveriesAndRecalculate(shoppingCart, true);

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
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        Warehouse warehouse = warehouseService.findById(1L);
        //need 2 items to push order back to life cycle
        skuWarehouseService.credit(warehouse, "BACKORDER-BACK-TO-FLOW2", BigDecimal.ONE);
        Thread.sleep(100L);
        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status1 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status1);
        assertFalse(status1.getReport(), status1.getReport().contains(order.getOrdernum()));

        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still be in progress 0", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("One item not enough to continue order processing. Must still await inventory 0", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        // credit one more
        skuWarehouseService.credit(warehouse, "BACKORDER-BACK-TO-FLOW2", BigDecimal.ONE);
        Thread.sleep(100L);
        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status2 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status2);
        assertFalse(status2.getReport(), status2.getReport().contains(order.getOrdernum()));

        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still be in progress 1", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("One item not enough to continue order processing. Must still await inventory 1", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        //need 2 items to push order back to life cycle
        skuWarehouseService.credit(warehouse, "BACKORDER-BACK-TO-FLOW1", BigDecimal.ONE);
        Thread.sleep(100L);
        bulkAwaitingInventoryDeliveriesProcessor.process(ctx);

        final JobStatus status3 = ((JobStatusAware) bulkAwaitingInventoryDeliveriesProcessor).getStatus(null);

        assertNotNull(status3);
        assertTrue(status3.getReport(), status3.getReport().contains("Updated customer order " + order.getOrdernum()
                + " delivery " + order.getDelivery().iterator().next().getDeliveryNum() + ", event evt.delivery.allowed.quantity"));

        order = customerOrderService.findByReference(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still be in progress 2", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("Delivery can start", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
        }



    }
}
