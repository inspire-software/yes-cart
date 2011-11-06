package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.OrderEventImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CancelOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private CancelOrderEventHandlerImpl handler;

    @Before
    public void setUp() throws Exception {
        handler = (CancelOrderEventHandlerImpl) ctx.getBean("cancelOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
    }

    // FIX to allow running from IDE
    @Test
    public void testHandle() {
        Customer customer = createCustomer();
        assertFalse(customer.getAddress().isEmpty());
        CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        handler.handle(
                new OrderEventImpl(
                        "", //evt.payment.offline
                        customerOrder
                )
        );
        assertEquals(CustomerOrder.ORDER_STATUS_CANCELLED, customerOrder.getOrderStatus());
    }
}
