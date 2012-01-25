package org.yes.cart.service.order.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.OrderStateTransitionListener;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderStateManagerImpl implements OrderStateManager {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final Map<String, OrderEventHandler> handlers;

    private final Map<String, List<? extends OrderStateTransitionListener>> beforeListenersMap;

    private final Map<String, List<? extends OrderStateTransitionListener>> afterListenersMap;

    /**
     * Construct order state manager with mandatory event handlers, that perform
     * transition and optional listener before and after transitions.
     * After transition listeners may not be called if no transition was performed.
     *
     * @param handlers           mandatory event id - handler map.
     * @param beforeListenersMap optional before transition event handlers
     * @param afterListenersMap  optional after transition event handlers
     */
    public OrderStateManagerImpl(
            final Map<String, OrderEventHandler> handlers,
            final Map<String, List<? extends OrderStateTransitionListener>> beforeListenersMap,
            final Map<String, List<? extends OrderStateTransitionListener>> afterListenersMap) {
        this.handlers = handlers;
        this.beforeListenersMap = beforeListenersMap;
        this.afterListenersMap = afterListenersMap;
    }

    /**
     * {@inheritDoc}
     */
    public boolean fireTransition(final OrderEvent orderEvent) {
        final OrderEventHandler handler = handlers.get(orderEvent.getEventId());

        if (handler == null) {
            LOG.warn(MessageFormat.format(
                    "No handler registered for {0} event", orderEvent.getEventId()
            ));
        } else {
            fireEventListeners(beforeListenersMap, orderEvent);
            boolean result;
            try {
                result = handler.handle(orderEvent);
                if (result) {
                    fireEventListeners(afterListenersMap, orderEvent);
                }
                return result;
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(
                            MessageFormat.format("Cant handle {0} event for {1} order because of {2}",
                                    orderEvent.getEventId(),
                                    orderEvent.getCustomerOrder().getOrdernum(),
                                    e.getMessage()),
                            e
                    );
                }
            }

        }
        return false;
    }

    private void fireEventListeners(final Map<String, List<? extends OrderStateTransitionListener>> map,
                                    final OrderEvent orderEvent) {
        if (map != null) {
            final List<? extends OrderStateTransitionListener> beforeListeners = map.get(orderEvent.getEventId());
            if (beforeListeners != null) {
                for (OrderStateTransitionListener listener : beforeListeners) {
                    listener.onEvent(orderEvent);
                }
            }
        }

    }

    /**
     * Get  before transition listeners map.
     *
     * @return before transition listeners map.
     */
    public Map<String, List<? extends OrderStateTransitionListener>> getBeforeListenersMap() {
        return beforeListenersMap;
    }

    /**
     * Get handlers.
     *
     * @return handlers map.
     */
    public Map<String, OrderEventHandler> getHandlers() {
        return handlers;
    }

    /**
     * Get after transition listeners map.
     *
     * @return after transition listeners map.
     */
    public Map<String, List<? extends OrderStateTransitionListener>> getAfterListenersMap() {
        return afterListenersMap;
    }

}
