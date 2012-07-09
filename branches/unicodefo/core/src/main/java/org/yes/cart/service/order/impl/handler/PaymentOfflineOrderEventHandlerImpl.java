package org.yes.cart.service.order.impl.handler;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentOfflineOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler {

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            handleInternal(orderEvent);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_WAITING;
    }

}
