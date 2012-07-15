/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.order;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.util.Map;

/**
 * Event to fire transition between order states.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderEvent {


    /**
     * Get event id.
     *
     * @return event id.
     * @see org.yes.cart.service.order.OrderStateManager statuses for more details
     */
    String getEventId();


    /**
     * Event for customer order.
     *
     * @return customer order.
     */
    CustomerOrder getCustomerOrder();


    /**
     * Get optional delivery
     *
     * @return {@link CustomerOrderDelivery}
     */
    CustomerOrderDelivery getCustomerOrderDelivery();

    /**
     * Get oprional params
     *
     * @return event params
     */
    Map getParams();


}
