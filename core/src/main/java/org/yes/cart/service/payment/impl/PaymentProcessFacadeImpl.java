package org.yes.cart.service.payment.impl;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentFailedException;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.service.payment.PaymentResult;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Map;

/**
 * Payment process facade implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:18
 */
public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    private final CustomerOrderService customerOrderService;
    private final OrderStateManager orderStateManager;

    /**
     * Construct payment process facade.
     * @param customerOrderService order service
     * @param orderStateManager order state manager
     */
    public PaymentProcessFacadeImpl(final CustomerOrderService customerOrderService,
                                    final OrderStateManager orderStateManager) {
        this.customerOrderService = customerOrderService;
        this.orderStateManager = orderStateManager;
    }

    /**
     * Perform order payment.
     * @param paymentParameter the payment parameters.
     * @return {@link PaymentResult} in cae of succesfull payment
     * @throws PaymentFailedException in case of payment failure.
     */
    public PaymentResult pay(final Map paymentParameter) throws PaymentFailedException {

        ShoppingCart shoppingCart = null;//todo get from map ShoppingCart


        final OrderEvent orderEvent = new OrderEventImpl(
                OrderStateManager.EVT_PENDING,
                customerOrderService.findByGuid(shoppingCart.getGuid()),
                null,
                paymentParameter
        );
        final CustomerOrder order = orderEvent.getCustomerOrder();
        final String orderNumber = order.getOrdernum();

        if (orderStateManager.fireTransition(orderEvent)
                && !CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {

            System.out.println("Payment(s) for order " + orderNumber + " was successful");
            customerOrderService.update(order);
            //setResponsePage(PaymentSuccessfulPage.class, new PageParameters("orderNum=" + orderNumber));

        } else {

            System.out.println("Payment(s) for order " + orderNumber + " was failed");
            customerOrderService.update(order);
            //setResponsePage(PaymentFailedPage.class);

        }

        return new PaymentResultImpl(); //not sure do we need excption or not


    }
}
