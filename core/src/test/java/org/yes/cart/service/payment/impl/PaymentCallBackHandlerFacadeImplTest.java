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

package org.yes.cart.service.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.impl.TestExtFormPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.impl.DeliveryAssemblerImpl;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

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
    private DeliveryAssemblerImpl deliveryAssembler;

    @Before
    public void setUp() {
        paymentCallBackHandlerFacade = (PaymentCallBackHandlerFacade) ctx().getBean(ServiceSpringKeys.PAYMENT_CALLBACK_HANDLER);
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
        deliveryAssembler = (DeliveryAssemblerImpl) ctx().getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);

        super.setUp();
    }

    @Test
    public void testHandlePaymentCallbackEnoughStock() throws Exception {
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail());
        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, true);
        customerOrder.setPgLabel("testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.create(customerOrder);
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        final String ordGuid = customerOrder.getCartGuid();
        paymentCallBackHandlerFacade.handlePaymentCallback(
                new HashMap<String, String>() {{
                    put(TestExtFormPaymentGatewayImpl.ORDER_GUID_PARAM_KEY, ordGuid);
                    put(TestExtFormPaymentGatewayImpl.RESPONSE_CODE_PARAM_KEY, "1"); // 1 - means ok
                }},
                "testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.findByGuid(customerOrder.getCartGuid());
        assertEquals("Order must be in ORDER_STATUS_PARTIALLY_SHIPPED state",  //because one of the delivery is electronic delivery
                CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED,
                customerOrder.getOrderStatus());

        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertNotNull(payments);
        assertEquals(payments.size(), 1);
        assertEquals("AUTH_CAPTURE", payments.get(0).getTransactionOperation());
        assertEquals("1", payments.get(0).getTransactionOperationResultCode());

    }

    @Test
    public void testHandlePaymentCallbackOutOfStock() throws Exception {
        Customer customer = createCustomer();
        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail());

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);
        Map<String, String> param = new HashMap<String, String>();
        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST1");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "20000.00");
        commands.execute(shoppingCart, (Map) param);

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, true);
        customerOrder.setPgLabel("testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.create(customerOrder);
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        final String ordGuid = customerOrder.getCartGuid();
        paymentCallBackHandlerFacade.handlePaymentCallback(
                new HashMap<String, String>() {{
                    put(TestExtFormPaymentGatewayImpl.ORDER_GUID_PARAM_KEY, ordGuid);
                    put(TestExtFormPaymentGatewayImpl.RESPONSE_CODE_PARAM_KEY, "1"); // 1 - means ok
                }},
                "testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.findByGuid(customerOrder.getCartGuid());
        assertEquals("Order must be in ORDER_STATUS_CANCELLED state",  //because item is out of stock
                CustomerOrder.ORDER_STATUS_CANCELLED,
                customerOrder.getOrderStatus());

        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        assertNotNull(payments);
        assertEquals(payments.size(), 2);
        assertEquals("AUTH_CAPTURE", payments.get(0).getTransactionOperation());
        assertEquals("1", payments.get(0).getTransactionOperationResultCode());
        assertEquals("VOID_CAPTURE", payments.get(1).getTransactionOperation());
        assertEquals("1", payments.get(1).getTransactionOperationResultCode());


    }
}
