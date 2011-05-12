package org.yes.cart.service.order.impl.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Collections;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentOkOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService = null;
    private PaymentOkOrderEventHandlerImpl handler = null;
    private CustomerOrderPaymentService customerOrderPaymentService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();


        handler = (PaymentOkOrderEventHandlerImpl) ctx.getBean("paymentOkOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean("customerOrderPaymentService");
    }

    @After
    public void tearDown() {
        orderService = null;
        handler = null;
        customerOrderPaymentService = null;
        super.tearDown();
    }

    /**
     * Test with ok payment
     */
    @Test
    public void testHandle() {

        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());

        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());

        customerOrder.setPgLabel("testPaymentGatewayLabel");
        orderService.update(customerOrder);

        assertTrue(handler.handle( new OrderEventImpl("",  customerOrder,  null,  Collections.EMPTY_MAP  ) ));

        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, customerOrder.getOrderStatus());
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED,
                customerOrder.getDelivery().iterator().next().getDeliveryStatus());



    }

}
