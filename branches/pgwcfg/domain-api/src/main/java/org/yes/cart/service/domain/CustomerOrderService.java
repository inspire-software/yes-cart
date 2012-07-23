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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerOrderService extends GenericService<CustomerOrder> {


    /**
     * Get all orders , than belong to give customer.
     *
     * @param customer {@link Customer}
     * @param since    given date optional
     * @return list of all orders
     */
    List<CustomerOrder> findCustomerOrders(Customer customer, Date since);

    /**
     * Get all orders , than belong to give customer.
     *
     * @param customerId customer id
     * @param since      given date optional
     * @return list of all orders
     */
    List<CustomerOrder> findCustomerOrders(long customerId, Date since);

    /**
     * Get order amount
     *
     * @param orderNumber given order number
     * @return order amount
     */
    BigDecimal getOrderAmount(String orderNumber);

    /**
     * Find customer's order by given criteria.
     *
     * @param customerId  customer id. Rest of parameters will be ignored, if customerId more that 0.
     * @param firstName   optional to perform search using like by first name
     * @param lastName    optional to perform search using like by last name
     * @param email       optional to perform search using like by email
     * @param orderStatus optional order status
     * @param fromDate    optional order created from
     * @param tillDate    optional orer created till
     * @param orderNum    optional to perform search using like by order number
     * @return
     */
    List<CustomerOrder> findCustomerOrdersByCriterias(
            long customerId,
            String firstName,
            String lastName,
            String email,
            String orderStatus,
            Date fromDate,
            Date tillDate,
            String orderNum
    );


    /**
     * Create customer order from shopping cart.
     *
     * @param shoppingCart        shopping cart
     * @param onePhysicalDelivery true if need to create one physical delivery.
     * @return created order.
     */
    CustomerOrder createFromCart(ShoppingCart shoppingCart, boolean onePhysicalDelivery);

    /**
     * Find created order by cart guid.
     *
     * @param shoppingCartGuid shopping cart  guid
     * @return created order.
     */
    CustomerOrder findByGuid(String shoppingCartGuid);

    /**
     * Is order can be with multiple deliveries.
     *
     * @param shoppingCart cart to  check
     * @return true if order can be with several physical deliveries
     */
    boolean isOrderCanHasMultipleDeliveries(ShoppingCart shoppingCart);


}
