package org.yes.cart.service.order;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderStateTransitionListener {

    /**
     * Event handler.
     * @param orderEvent event to fire transition
     * @return  true in case if transition was  successful
     */
    boolean onEvent(OrderEvent orderEvent);

}
