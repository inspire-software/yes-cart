/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Test awaiting orders.
 */
public class PreOrderJobImplTest extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;
    private ProductService productService;
    private OrderStateManager orderStateManager;
    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private ProductSkuService productSkuService;



    @Before
    public void setUp()  {
        customerOrderService = ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE, CustomerOrderService.class);
        productService = ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE, ProductService.class);
        orderStateManager =  ctx().getBean(ServiceSpringKeys.ORDER_STATE_MANAGER, OrderStateManager.class);
        warehouseService =  ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE, WarehouseService.class);
        skuWarehouseService =  ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE, SkuWarehouseService.class);
        productSkuService =  ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE, ProductSkuService.class);
        super.setUp();
    }


    @Test
    public void testProcessAwaitingOrders() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);

        Product product = productService.getProductById(15350L);
        product.setAvailablefrom(calendar.getTime());
        productService.update(product);

        product = productService.getProductById(15360L);
        product.setAvailablefrom(calendar.getTime());
        productService.update(product);

        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCartWithPreorderItems(getTestName(), 2);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
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

        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT, delivery.getDeliveryStatus());
        }

        PreOrderJobImpl preOrderJob = new PreOrderJobImpl();

        preOrderJob.processAwaitingOrders(
                Arrays.asList(
                        productSkuService.getById(15350L).getCode(),
                        productSkuService.getById(15360L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT,
                customerOrderService,
                orderStateManager);

        //No changes
        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT, delivery.getDeliveryStatus());
        }

        Thread.sleep(5000);

        preOrderJob.processAwaitingOrders(
                Arrays.asList(
                        productSkuService.getById(15350L).getCode(),
                        productSkuService.getById(15360L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT,
                customerOrderService,
                orderStateManager);

        //wait for inventory
        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        //add inventory
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(15350L)); //need 2 items to push order back to life cycle
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouseService.create(skuWarehouse);

        skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(15360L)); //need 2 items to push order back to life cycle
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouseService.create(skuWarehouse);

        preOrderJob.processAwaitingOrders(
                Arrays.asList(
                        productSkuService.getById(15350L).getCode(),
                        productSkuService.getById(15360L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                OrderStateManager.EVT_DELIVERY_ALLOWED_QUANTITY,
                customerOrderService,
                orderStateManager);

        //wait for inventory
        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery: order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
        }




    }

}
