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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.text.MessageFormat;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImpl extends BaseGenericServiceImpl<CustomerOrder> implements CustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    private final OrderAssembler orderAssembler;

    private final DeliveryAssembler deliveryAssembler;

    private final OrderSplittingStrategy orderSplittingStrategy;

    private final GenericDAO<Customer, Long> customerDao;

    private final GenericDAO<Object, Long> genericDao;

    private final GenericDAO<CustomerOrderDelivery, Long> customerOrderDeliveryDao;


    /**
     * Construct order service.
     *
     * @param customerOrderDao customer order dao.
     * @param customerDao customer dao to use
     * @param customerOrderDeliveryDao to get deliveries, awiting for inventory
     * @param orderAssembler order assembler
     * @param deliveryAssembler delivery assembler
     * @param orderSplittingStrategy order splitting strategy
     */
    public CustomerOrderServiceImpl(
            final GenericDAO<CustomerOrder, Long> customerOrderDao,
            final GenericDAO<Customer, Long> customerDao,
            final GenericDAO<Object, Long> genericDao,
            final GenericDAO<CustomerOrderDelivery, Long> customerOrderDeliveryDao,
            final OrderAssembler orderAssembler,
            final DeliveryAssembler deliveryAssembler,
            final OrderSplittingStrategy orderSplittingStrategy) {
        super(customerOrderDao);
        this.orderAssembler = orderAssembler;
        this.deliveryAssembler = deliveryAssembler;
        this.customerDao = customerDao;
        this.genericDao = genericDao;
        this.customerOrderDeliveryDao = customerOrderDeliveryDao;
        this.orderSplittingStrategy = orderSplittingStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrderDelivery findDelivery(final long deliveryId) {
        return customerOrderDeliveryDao.findById(deliveryId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findEligibleForExportOrderIds() {
        return (List) customerOrderDeliveryDao.findQueryObjectByNamedQuery("ORDERS.IDS.BY.ELIGIBLE.FOR.EXPORT", Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findAwaitingDeliveriesIds(final List<String> skuCodes, final String deliveryStatus, final List<String> orderStatus) {

        final List<Long> waitingDeliveries;

        if (skuCodes != null) {
            waitingDeliveries = (List) customerOrderDeliveryDao.findQueryObjectByNamedQuery("DELIVERIES.IDS.WAITING.FOR.INVENTORY.BY.SKU",
                    deliveryStatus,
                    orderStatus,
                    skuCodes);
        } else {
            waitingDeliveries = (List) customerOrderDeliveryDao.findByNamedQuery("DELIVERIES.IDS.WAITING.FOR.INVENTORY",
                    deliveryStatus,
                    orderStatus);
        }

        return waitingDeliveries;

    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<CustomerOrderDelivery> findAwaitingDeliveries(final List<String> skuCodes, final String deliveryStatus, final List<String> orderStatus) {

        final ResultsIterator<CustomerOrderDelivery> waitingDeliveries;

        if (skuCodes != null) {
            waitingDeliveries = customerOrderDeliveryDao.findByNamedQueryIterator("DELIVERIES.WAITING.FOR.INVENTORY.BY.SKU",
                    deliveryStatus,
                    orderStatus,
                    skuCodes);
        } else {
            waitingDeliveries = customerOrderDeliveryDao.findByNamedQueryIterator("DELIVERIES.WAITING.FOR.INVENTORY",
                    deliveryStatus,
                    orderStatus);
        }

        return waitingDeliveries;

    }



    /**
     * {@inheritDoc}
     */
    public List<CustomerOrder> findCustomerOrdersByCriteria(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date toDate,
            final String orderNum
            ) {
        return getGenericDao().findByNamedQuery("ORDERS.BY.CRITERIA",
                likeValue(firstName),
                likeValue(lastName),
                likeValue(email),
                orderStatus,
                fromDate,
                toDate,
                likeValue(orderNum),
                customerId
        );

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrder> findCustomerOrdersByDeliveryIds(final Collection<Long> deliveryIds) {
        return getGenericDao().findByNamedQuery("ORDERS.BY.DELIVERY.IDS", deliveryIds);
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrder> findCustomerOrders(final long customerId, final Date since) {
        return findCustomerOrders(customerDao.findById(customerId), since);
    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrder> findCustomerOrders(final Customer customer, final Date since) {
        if (since == null) {
            return getGenericDao().findByCriteria(
                    Restrictions.eq("customer", customer)
            );
        }
        return getGenericDao().findByCriteria(
                Restrictions.eq("customer", customer),
                Restrictions.gt("orderTimestamp", since)
        );
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> isOrderMultipleDeliveriesAllowed(final ShoppingCart shoppingCart) {

        return orderSplittingStrategy.isMultipleDeliveriesAllowed(
                shoppingCart.getShoppingContext().getCustomerShopId(),
                shoppingCart.getCartItemList()
        );

    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart)
            throws OrderAssemblyException {


        final boolean checkoutBlocked = Boolean.valueOf(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT));
        if (checkoutBlocked) {
            throw new PlaceOrderDisabledException(shoppingCart.getCustomerEmail());
        }

        final Map<String, Boolean> onePhysicalDelivery = new HashMap<String, Boolean>();
        for (final Map.Entry<String, Boolean> isAllowed : shoppingCart.getOrderInfo().getMultipleDeliveryAvailable().entrySet()) {
            onePhysicalDelivery.put(isAllowed.getKey(), !shoppingCart.getOrderInfo().isMultipleDelivery() || !isAllowed.getValue());
        }

        final CustomerOrder customerOrderToDelete = getGenericDao().findSingleByCriteria(
                Restrictions.eq("cartGuid", shoppingCart.getGuid())
        );
        if (customerOrderToDelete != null) {

            if (!CustomerOrder.ORDER_STATUS_NONE.equals(customerOrderToDelete.getOrderStatus())) {

                // Order is not in ORDER_STATUS_NONE but the cart had not been cleaned
                // This can only happen if they did not click come back to site from the external payment site
                // Meanwhile the callback happened and updated the order

                LOG.warn(
                        MessageFormat.format(
                                "Order {0} with {1} cart guid has {2} order status instead of 1 - ORDER_STATUS_NONE",
                                customerOrderToDelete.getCustomerorderId(),
                                shoppingCart.getGuid(),
                                customerOrderToDelete.getOrderStatus()
                        )
                );

                // detach order as it is being processed (rehash the GUID and reset cartGuid)
                customerOrderToDelete.setGuid(UUID.randomUUID().toString());
                customerOrderToDelete.setCartGuid(customerOrderToDelete.getOrdernum());

                getGenericDao().saveOrUpdate(customerOrderToDelete);

                getGenericDao().flushClear();

                getGenericDao().evict(customerOrderToDelete);

            } else {

                // This is ORDER_STATUS_NONE so probably customer made some changes and came back
                // we no longer need this order

                getGenericDao().delete(customerOrderToDelete);
                getGenericDao().flushClear();
                getGenericDao().evict(customerOrderToDelete);

            }

        }

        final CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, onePhysicalDelivery);
        return create(customerOrder);

    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder findByReference(final String reference) {
        final CustomerOrder order = findByGuid(reference);
        if (order == null) {
            return findByOrderNumber(reference);
        }
        return order;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder findByGuid(final String shoppingCartGuid) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("cartGuid", shoppingCartGuid)
        );
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder findByOrderNumber(final String orderNumber) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("ordernum", orderNumber)
        );
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final CustomerOrder instance) {
        for (CustomerOrderDelivery delivery : instance.getDelivery()) {
            this.genericDao.delete(delivery);
        }
        super.delete(instance);
    }

}
