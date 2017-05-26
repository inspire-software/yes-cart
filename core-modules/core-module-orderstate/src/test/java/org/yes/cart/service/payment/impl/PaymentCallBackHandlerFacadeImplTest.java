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

package org.yes.cart.service.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.impl.TestExtFormPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.impl.DeliveryAssemblerImpl;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentCallBackHandlerFacadeImplTest extends BaseCoreDBTestCase {

    private PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;
    private OrderAssembler orderAssembler;
    private CustomerOrderService customerOrderService;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private SkuWarehouseService skuWarehouseService;
    private WarehouseService warehouseService;
    private DeliveryAssemblerImpl deliveryAssembler;

    @Before
    public void setUp() {
        paymentCallBackHandlerFacade = (PaymentCallBackHandlerFacade) ctx().getBean(ServiceSpringKeys.PAYMENT_CALLBACK_HANDLER);
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        warehouseService = (WarehouseService) ctx().getBean("warehouseService");
        skuWarehouseService = (SkuWarehouseService) ctx().getBean("skuWarehouseService");
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);

        super.setUp();
    }

    @Test
    public void testHandlePaymentCallbackEnoughStock() throws Exception {
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), false);
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        customerOrder.setPgLabel("testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.create(customerOrder);
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        final String ordGuid = customerOrder.getCartGuid();
        ShopCodeContext.setShopCode(customerOrder.getShop().getCode());
        final PaymentGatewayCallback callback = paymentCallBackHandlerFacade.registerCallback(
                new HashMap<String, String>() {{
                    put(TestExtFormPaymentGatewayImpl.ORDER_GUID_PARAM_KEY, ordGuid);
                    put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1"); // 1 - means ok
                }},
                "testExtFormPaymentGatewayLabel",
                customerOrder.getShop().getCode(), "TEST"
        );
        paymentCallBackHandlerFacade.handlePaymentCallback(callback);
        ShopCodeContext.clear();
        customerOrder = customerOrderService.findByReference(customerOrder.getCartGuid());
        assertEquals("Order must be in ORDER_STATUS_IN_PROGRESS state",
                CustomerOrder.ORDER_STATUS_IN_PROGRESS,
                customerOrder.getOrderStatus());

        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, (String) null, (String) null);
        assertNotNull(payments);
        assertEquals(payments.size(), 1);
        assertEquals(PaymentGateway.AUTH_CAPTURE, payments.get(0).getTransactionOperation());
        assertEquals("1", payments.get(0).getTransactionOperationResultCode());

    }

    @Test
    public void testHandlePaymentCallbackOutOfStock() throws Exception {
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), false);

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, getMultiSelection(shoppingCart));
        customerOrder.setPgLabel("testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.create(customerOrder);
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());

        // Simulate out of stock condition in time period between we go off to external form and then come back with payment callback
        final Warehouse warehouse = warehouseService.findById(1L);
        assertNotNull(warehouse);
        final Pair<BigDecimal, BigDecimal> quantityAndReserve = skuWarehouseService.findQuantity(Arrays.asList(warehouse), "CC_TEST1");
        skuWarehouseService.debit(warehouse, "CC_TEST1", quantityAndReserve.getFirst());
        // end Simulate

        final String ordGuid = customerOrder.getCartGuid();
        ShopCodeContext.setShopCode(customerOrder.getShop().getCode());
        final PaymentGatewayCallback callback = paymentCallBackHandlerFacade.registerCallback(
                new HashMap<String, String>() {{
                    put(TestExtFormPaymentGatewayImpl.ORDER_GUID_PARAM_KEY, ordGuid);
                    put(TestExtFormPaymentGatewayImpl.AUTH_RESPONSE_CODE_PARAM_KEY, "1"); // 1 - means ok
                }},
                "testExtFormPaymentGatewayLabel",
                customerOrder.getShop().getCode(), "TEST"
        );
        paymentCallBackHandlerFacade.handlePaymentCallback(callback);
        ShopCodeContext.clear();
        customerOrder = customerOrderService.findByReference(customerOrder.getCartGuid());
        assertEquals("Order must be in ORDER_STATUS_CANCELLED_WAITING_PAYMENT state",  //because item is out of stock and we have AUTH_CAPTURE so we need a REFUND
                CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT,
                customerOrder.getOrderStatus());

        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, (String) null, (String) null);
        assertNotNull(payments);
        assertEquals(payments.size(), 2);
        assertEquals(PaymentGateway.AUTH_CAPTURE, payments.get(0).getTransactionOperation());
        assertEquals("1", payments.get(0).getTransactionOperationResultCode());
        assertEquals(PaymentGateway.REFUND, payments.get(1).getTransactionOperation());
        assertEquals("Manual processing required", payments.get(1).getTransactionOperationResultCode());


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
