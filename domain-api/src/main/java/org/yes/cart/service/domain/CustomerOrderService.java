package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerOrderService extends GenericService<CustomerOrder> {

    /**
     * TODO kill this method. Get customers orders since given date
     * Guess top seller counter must be different
     *
     * @param since given date
     * @return list of orders.
     */
    List<CustomerOrder> findCustomerOrders(Date since);

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
