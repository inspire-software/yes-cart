/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * Find customer's order by given criteria.
     *
     * @param customerId  customer id. Rest of parameters will be ignored, if customerId more that 0.
     * @param firstName   optional to perform search using like by first name
     * @param lastName    optional to perform search using like by last name
     * @param email       optional to perform search using like by email
     * @param orderStatus optional order status
     * @param fromDate    optional order created from
     * @param toDate    optional order created to
     * @param orderNum    optional to perform search using like by order number
     *
     * @return list of orders
     */
    List<CustomerOrder> findCustomerOrdersByCriteria(
            long customerId,
            String firstName,
            String lastName,
            String email,
            String orderStatus,
            Date fromDate,
            Date toDate,
            String orderNum
    );

    /**
     * Find customer orders by delivery ids.
     *
     * @param deliveryIds delivery ids
     *
     * @return list of orders
     */
    List<CustomerOrder> findCustomerOrdersByDeliveryIds(Collection<Long> deliveryIds);

    /**
     * Find specific delivery.
     *
     * @param deliveryId PK
     * @return delivery
     */
    CustomerOrderDelivery findDelivery(long deliveryId);

    /**
     * Find specific delivery.
     *
     * @param deliveryNum delivery number
     * @return delivery
     */
    CustomerOrderDelivery findDeliveryByNumber(String deliveryNum);

    /**
     * Find order ids with eligible for export flag set to true.
     *
     * @return order ids with eligible for export flag
     */
    List<Long> findEligibleForExportOrderIds();

    /**
     * Find orders, which are waiting for inventory to be completed.
     *
     * @param skuCodes       what sku is required. optional
     * @param deliveryStatus status of delivery
     * @param orderStatus    order status
     * @return awaiting orders
     */
    List<Long> findAwaitingDeliveriesIds(List<String> skuCodes, String deliveryStatus, List<String> orderStatus);

    /**
     * Find orders, which are waiting for inventory to be completed.
     *
     * @param skuCodes       what sku is required. optional
     * @param deliveryStatus status of delivery
     * @param orderStatus    order status
     * @return awaiting orders
     */
    ResultsIterator<CustomerOrderDelivery> findAwaitingDeliveries(List<String> skuCodes, String deliveryStatus, List<String> orderStatus);


    /**
     * Create customer order from shopping cart.
     *
     * @param shoppingCart        shopping cart
     *
     * @return created order.
     */
    CustomerOrder createFromCart(ShoppingCart shoppingCart) throws OrderAssemblyException;

    /**
     * Find order by reference (cart guid or order number).
     *
     * @param reference reference
     *
     * @return customer order
     */
    CustomerOrder findByReference(String reference);

    /**
     * Find order by reference (cart guid or order number).
     *
     * @param reference reference
     *
     * @return customer order
     */
    CustomerOrder findByDeliveryReference(String reference);

    /**
     * Find created order by cart guid.
     *
     * @param shoppingCartGuid shopping cart  guid
     * @return created order.
     *
     * @deprecated use {@link #findByReference(String)}
     */
    @Deprecated
    CustomerOrder findByGuid(String shoppingCartGuid);

    /**
     * Find created order by order number.
     *
     * @param orderNumber order number
     * @return created order.
     *
     * @deprecated use {@link #findByReference(String)}
     */
    @Deprecated
    CustomerOrder findByOrderNumber(String orderNumber);

    /**
     * Is order can be with multiple deliveries.
     *
     * @param shoppingCart cart to  check
     * @return true if order can be with several physical deliveries
     */
    Map<String, Boolean> isOrderMultipleDeliveriesAllowed(ShoppingCart shoppingCart);


}
