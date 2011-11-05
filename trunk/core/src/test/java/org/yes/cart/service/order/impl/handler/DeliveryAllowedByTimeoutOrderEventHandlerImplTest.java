package org.yes.cart.service.order.impl.handler;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.impl.OrderAssemblerImplTest;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAllowedByTimeoutOrderEventHandlerImplTest extends AbstractEventHandlerImplTest {

    private CustomerOrderService orderService;
    private DeliveryAllowedByTimeoutOrderEventHandlerImpl handler;

    @Before
    public void setUp() throws Exception {
        handler = (DeliveryAllowedByTimeoutOrderEventHandlerImpl) ctx.getBean("deliveryAllowedByTimeoutOrderEventHandler");
        orderService = (CustomerOrderService) ctx.getBean("customerOrderService");
    }

    // TODO fix to not depend on order or running
    @Test
    public void testHandle() {
        final Customer customer = OrderAssemblerImplTest.createCustomer(ctx);
        assertFalse(customer.getAddress().isEmpty());
        final CustomerOrder customerOrder = orderService.createFromCart(getStdCard(ctx, customer.getEmail()), false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, customerOrder.getOrderStatus());
        CustomerOrderDelivery delivery = customerOrder.getDelivery().iterator().next();
        Calendar calendar = Calendar.getInstance();
        //set allowed time in the future
        calendar.set(2020, 9, 11);
        for (CustomerOrderDeliveryDet det : delivery.getDetail()) {
            det.getSku().getProduct().setAvailablefrom(calendar.getTime());
        }
        assertFalse(handler.handle(
                new OrderEventImpl(
                        "", //evt.payment.offline
                        customerOrder,
                        delivery
                )
        ));
        //set allowed time in the past
        calendar.set(2011, 1, 22);
        for (CustomerOrderDeliveryDet det : delivery.getDetail()) {
            det.getSku().getProduct().setAvailablefrom(calendar.getTime());
        }
        assertTrue(handler.handle(
                new OrderEventImpl(
                        "", //evt.payment.offline
                        customerOrder,
                        delivery
                )
        ));
        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                delivery.getDeliveryStatus());

    }
}
