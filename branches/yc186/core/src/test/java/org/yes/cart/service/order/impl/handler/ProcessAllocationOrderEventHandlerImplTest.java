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

package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProcessAllocationOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private ProcessAllocationOrderEventHandlerImpl handler;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;

    @Before
    public void setUp()  {
        handler = (ProcessAllocationOrderEventHandlerImpl) ctx().getBean("processAllocationOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean("customerOrderService");
        productSkuService = (ProductSkuService) ctx().getBean("productSkuService");
        skuWarehouseService = (SkuWarehouseService) ctx().getBean("skuWarehouseService");
        warehouseService = (WarehouseService) ctx().getBean("warehouseService");
        super.setUp();
    }

    @Test
    public void testHandle() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx(), customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        CustomerOrderDelivery customerOrderDelivery = customerOrder.getDelivery().iterator().next();
        assertTrue(handler.handle(new OrderEventImpl("", customerOrder, customerOrderDelivery)));
        final Warehouse warehouse = warehouseService.getById(1);
        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                Collections.singletonList(warehouse),
                sku
        );
        assertEquals(new BigDecimal("7.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                Collections.singletonList(warehouse),
                sku
        );
        assertEquals(new BigDecimal("0.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                customerOrderDelivery.getDeliveryStatus());
    }
}
