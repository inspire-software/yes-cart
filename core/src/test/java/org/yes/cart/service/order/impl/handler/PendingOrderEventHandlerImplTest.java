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
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PendingOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private OrderEventHandler handler;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private WarehouseService warehouseService;
    private ProductSkuService productSkuService;
    private SkuWarehouseService skuWarehouseService;

    @Before
    public void setUp()  {
        handler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        orderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        super.setUp();
    }

    /**
     * Test with ok payment and offline payment gw
     */
    @Test
    public void testHandle0_0() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("courierPaymentGatewayLabel");
        orderService.update(customerOrder);
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));
        final Warehouse warehouse = warehouseService.getById(1);
        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("9.00"), qty.getFirst());
        assertEquals(new BigDecimal("2.00"), qty.getSecond());
        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("1.00"), qty.getFirst());
        assertEquals(new BigDecimal("1.00"), qty.getSecond());
        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED,
                    delivery.getDeliveryStatus());

        }
        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, rezList.size());
        CustomerOrderPayment customerOrderPayment = rezList.get(0);
        assertEquals(Payment.PAYMENT_STATUS_OK, customerOrderPayment.getPaymentProcessorResult());
    }

    /**
     * Test with ok payment and online payment gw
     */
    @Test
    public void testHandle0_1() throws Exception {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP)));
        final Warehouse warehouse = warehouseService.getById(1);
        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("7.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("0.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                    delivery.getDeliveryStatus());
        }
        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, rezList.size());
        CustomerOrderPayment customerOrderPayment = rezList.get(0);
        assertEquals(Payment.PAYMENT_STATUS_OK, customerOrderPayment.getPaymentProcessorResult());
    }

    /**
     * Test with failed payment
     */
    @Test
    public void testHandle1_1() throws Exception {
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, new PaymentGatewayParameterEntity());
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);
        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.pending
                        customerOrder,
                        null,
                        Collections.EMPTY_MAP
                )
        ));
        final Warehouse warehouse = warehouseService.getById(1);
        // check reserved quantity
        ProductSku sku = productSkuService.getProductSkuBySkuCode("CC_TEST1");
        Pair<BigDecimal, BigDecimal> qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("9.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        sku = productSkuService.getProductSkuBySkuCode("CC_TEST2");
        qty = skuWarehouseService.getQuantity(
                new ArrayList<Warehouse>() {{
                    add(warehouse);
                }},
                sku.getCode()
        );
        assertEquals(new BigDecimal("1.00"), qty.getFirst());
        assertEquals(new BigDecimal("0.00"), qty.getSecond());
        for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_VOID_RESERVATION,
                    delivery.getDeliveryStatus());
        }
        List<CustomerOrderPayment> rezList = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertEquals(1, rezList.size());
        CustomerOrderPayment customerOrderPayment = rezList.get(0);
        assertEquals(Payment.PAYMENT_STATUS_FAILED, customerOrderPayment.getPaymentProcessorResult());
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
    }
}
