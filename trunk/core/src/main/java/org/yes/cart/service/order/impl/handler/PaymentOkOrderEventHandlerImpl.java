package org.yes.cart.service.order.impl.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Perform separate processing of order deliveries.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentOkOrderEventHandlerImpl extends AbstractOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;

    private static final Map<String, String> GROUP_TRIGGER_MAP = new HashMap<String, String>() {{

        put(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_ALLOCATION);
        put(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);
        put(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_INVENTORY_WAIT);
        put(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP, OrderStateManager.EVT_SHIPMENT_COMPLETE);
        put(CustomerOrderDelivery.MIX_DELIVERY_GROUP, OrderStateManager.EVT_PROCESS_TIME_WAIT);

    }};


    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private synchronized OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = (OrderStateManager) applicationContext.getBean("orderStateManager");
        }
        return orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) {
        synchronized (OrderEventHandler.syncMonitor) {
            handleInternal(orderEvent);
            CustomerOrder order = orderEvent.getCustomerOrder();
            for (CustomerOrderDelivery delivery : order.getDelivery()) {
                final String eventId = GROUP_TRIGGER_MAP.get(delivery.getDeliveryGroup());
                if (LOG.isInfoEnabled()) {
                    LOG.info(MessageFormat.format("Delivery {0} for order {1} event {2}",
                            delivery.getDevileryNum(), order.getOrdernum(), eventId));
                }
                final OrderEvent deliveryEvent = new OrderEventImpl(eventId, order, delivery);
                getOrderStateManager().fireTransition(deliveryEvent);
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTransitionTarget(final OrderEvent orderEvent) {
        return CustomerOrder.ORDER_STATUS_IN_PROGRESS;
    }


}
