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
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerOrderService extends GenericService<CustomerOrder> {



    /**
     * Find orders by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of orders, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<CustomerOrder> findOrders(int start,
                                   int offset,
                                   String sort,
                                   boolean sortDescending,
                                   Map<String, List> filter);

    /**
     * Find orders by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return count
     */
    int findOrderCount(Map<String, List> filter);


    /**
     * Get all orders , than belong to give customer.
     *
     * @param customer {@link Customer}
     * @param since    given date optional
     *
     * @return list of all orders
     */
    List<CustomerOrder> findCustomerOrders(Customer customer, LocalDateTime since);

    /**
     * Get all orders , than belong to give customer.
     *
     * @param customerId customer id
     * @param since      given date optional
     *
     * @return list of all orders
     */
    List<CustomerOrder> findCustomerOrders(long customerId, LocalDateTime since);

    /**
     * Find customer orders by delivery ids.
     *
     * @param deliveryIds delivery ids
     *
     * @return list of orders
     */
    List<Long> findCustomerOrderIdsByDeliveryIds(Collection<Long> deliveryIds);

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
     *
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
     * @param shops          what shops are required. optional
     * @param deliveryStatus status of delivery
     * @param orderStatus    order status
     *
     * @return awaiting orders count
     */
    int findAwaitingDeliveriesCount(Set<Long> shops, String deliveryStatus, List<String> orderStatus);

    /**
     * Find orders, which are waiting for inventory to be completed.
     *
     * @param skuCodes       what sku is required. optional
     * @param deliveryStatus status of delivery
     * @param orderStatus    order status
     *
     * @return awaiting orders
     */
    List<Long> findAwaitingDeliveriesIds(List<String> skuCodes, String deliveryStatus, List<String> orderStatus);

    /**
     * Find orders, which are waiting for inventory to be completed.
     *
     * @param skuCodes       what sku is required. optional
     * @param deliveryStatus status of delivery
     * @param orderStatus    order status
     *
     * @return awaiting orders
     */
    ResultsIterator<CustomerOrderDelivery> findAwaitingDeliveries(List<String> skuCodes, String deliveryStatus, List<String> orderStatus);

    /**
     * Validate cart. Returns a pair of flag and list of messages.
     * If the flag is true is indicates that checkout must be blocked.
     *
     * @param shoppingCart cart to validate
     *
     * @return pair of flag and message options
     */
    CartValidityModel validateCart(ShoppingCart shoppingCart);

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
     *
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
     *
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
     *
     * @return true if order can be with several physical deliveries
     */
    Map<String, Boolean> isOrderMultipleDeliveriesAllowed(ShoppingCart shoppingCart);


}
