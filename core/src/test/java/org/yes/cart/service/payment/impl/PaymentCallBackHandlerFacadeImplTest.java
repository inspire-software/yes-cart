package org.yes.cart.service.payment.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.impl.TestExtFormPaymentGatewayImpl;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.impl.DeliveryAssemblerImpl;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;

import java.util.HashMap;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentCallBackHandlerFacadeImplTest  extends BaseCoreDBTestCase {

    private PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;
    private OrderAssembler  orderAssembler;
    private CustomerOrderService customerOrderService;
    private DeliveryAssemblerImpl deliveryAssembler;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        paymentCallBackHandlerFacade  =  (PaymentCallBackHandlerFacade) ctx.getBean(ServiceSpringKeys.PAYMENT_CALLBACK_HANDLER);
        orderAssembler = (OrderAssembler)  ctx.getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        deliveryAssembler = (DeliveryAssemblerImpl)  ctx.getBean(ServiceSpringKeys.DELIVERY_ASSEMBLER);


    }

    @After
    public void tearDown() {
        paymentCallBackHandlerFacade = null;
        orderAssembler = null;
        customerOrderService = null;
        deliveryAssembler = null;
    }

    @Test
    public void testHandlePaymentCallback() {
        Customer customer = OrderAssemblerImplTest.createCustomer(ctx, "testHandlePaymentCallback");
        ShoppingCart shoppingCart = OrderAssemblerImplTest.getShoppingCart(ctx, customer.getEmail());

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);

        customerOrder = deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, true);

        customerOrder.setPgLabel("testExtFormPaymentGatewayLabel");
        customerOrder = customerOrderService.create(customerOrder);
        assertEquals(
                "Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());

        final String ordGuid = customerOrder.getCartGuid();
        


        paymentCallBackHandlerFacade.handlePaymentCallback(
                new HashMap() {{
                    put(TestExtFormPaymentGatewayImpl.ORDER_GUID_PARAM_KEY, ordGuid);
                    put(TestExtFormPaymentGatewayImpl.RESPONCE_CODE_PARAM_KEY, "1"); // 1 - means ok 
                }}
                ,
                "testExtFormPaymentGatewayLabel"
        );


        customerOrder = customerOrderService.findByGuid(customerOrder.getCartGuid());

        assertEquals(
                "Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_IN_PROGRESS,
                customerOrder.getOrderStatus());







    }
}
