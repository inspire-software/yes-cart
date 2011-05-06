package org.yes.cart.service.order;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.util.Map;

/**
 *
 * Event to fire transition between order states.
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderEvent {


    /**
     * Get event id.
     * @see org.yes.cart.service.order.OrderStateManager statuses for more details
     * @return event id.
     */
    String getEventId();


    /**
     * Event for customer order.
     * @return customer order.
     */
    CustomerOrder  getCustomerOrder();


    /**
     * Get optional delivery
     * @return {@link CustomerOrderDelivery}
     */
    CustomerOrderDelivery getCustomerOrderDelivery();

    /** Get oprional params
     * @return event params
     * */
    Map getParams();



}
