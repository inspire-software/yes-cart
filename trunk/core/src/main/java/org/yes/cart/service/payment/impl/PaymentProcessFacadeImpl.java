package org.yes.cart.service.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import sun.rmi.runtime.Log;

import java.util.Map;

/**
 * Payment process facade implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 09/11/11
 * Time: 10:18
 */
public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

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
     * @param shoppingCart the shopping cart.
     * @return true in case of succesfull payment
     */
    public boolean pay(final ShoppingCart shoppingCart, final Map paymentParameter){


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

            LOG.info("PaymentProcessFacadeImpl#pay payment(s) for order " + orderNumber + " was successful");
            customerOrderService.update(order);
            return true;
        } else {
            LOG.info("PaymentProcessFacadeImpl#pay payment(s) for order " + orderNumber + " was failed");
            customerOrderService.update(order);
            return false;

        }

    }
}
