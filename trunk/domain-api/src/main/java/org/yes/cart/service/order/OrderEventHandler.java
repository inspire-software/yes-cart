package org.yes.cart.service.order;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderEventHandler {

    String syncMonitor = "syncMonitorOrderEventHandler";

    /**
     * Event handler.
     *
     * @param orderEvent event to fire transition
     * @return true in case if transition was  successful
     * @throws Exception case if transition failed
     */
    boolean handle(OrderEvent orderEvent) throws Exception;

}
