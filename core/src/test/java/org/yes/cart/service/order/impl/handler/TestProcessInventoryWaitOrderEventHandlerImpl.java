package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.TestOrderAssemblerImpl;
import org.yes.cart.service.order.impl.OrderEventImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestProcessInventoryWaitOrderEventHandlerImpl extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private ProcessInventoryWaitOrderEventHandlerImpl handler;

    @Before
    public void setUp() throws Exception {
        handler = (ProcessInventoryWaitOrderEventHandlerImpl) ctx.getBean("processInventoryWaitOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
    }

    @Test
    public void testHandle() {
        final Customer customer = TestOrderAssemblerImpl.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());
        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        CustomerOrderDelivery delivery = customerOrder.getDelivery().iterator().next();
        handler.handle(
                new OrderEventImpl("", //evt.payment.offline
                        customerOrder,
                        delivery));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
    }
}
