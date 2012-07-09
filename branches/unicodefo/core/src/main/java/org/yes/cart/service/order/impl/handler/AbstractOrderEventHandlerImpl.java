package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractOrderEventHandlerImpl implements OrderEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Get transition target.
     * @param orderEvent event
     * @return transition target id.
     */
    protected abstract String getTransitionTarget(final OrderEvent orderEvent);

    protected void handleInternal(final OrderEvent orderEvent) {
        if (LOG.isInfoEnabled()) {
            LOG.info(
                    MessageFormat.format(
                            "Order {0} transition from {1} to {2} state",
                            orderEvent.getCustomerOrder().getOrdernum(),
                            orderEvent.getCustomerOrder().getOrderStatus(),
                            getTransitionTarget(orderEvent)
                    )
            );
        }
        orderEvent.getCustomerOrder().setOrderStatus(getTransitionTarget(orderEvent));
    }



}
