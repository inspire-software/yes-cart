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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.order.*;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImpl extends BaseGenericServiceImpl<CustomerOrder> implements CustomerOrderService {

    private final OrderAssembler orderAssembler;

    private final DeliveryAssembler deliveryAssembler;

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
     */
    public CustomerOrderServiceImpl(
            final GenericDAO<CustomerOrder, Long> customerOrderDao,
            final GenericDAO<Customer, Long> customerDao,
            final GenericDAO<Object, Long> genericDao,
            final GenericDAO<CustomerOrderDelivery, Long> customerOrderDeliveryDao,
            final OrderAssembler orderAssembler,
            final DeliveryAssembler deliveryAssembler) {
        super(customerOrderDao);
        this.orderAssembler = orderAssembler;
        this.deliveryAssembler = deliveryAssembler;
        this.customerDao = customerDao;
        this.genericDao = genericDao;
        this.customerOrderDeliveryDao = customerOrderDeliveryDao;
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
    public boolean isOrderMultipleDeliveriesAllowed(final ShoppingCart shoppingCart) {
        try {
            final CustomerOrder order = orderAssembler.assembleCustomerOrder(shoppingCart, true); // Must be temp assembly, we are just checking not persisting
            return deliveryAssembler.isOrderMultipleDeliveriesAllowed(order);
        } catch (OrderAssemblyException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart, final boolean onePhysicalDelivery)
            throws OrderAssemblyException {
        final CustomerOrder customerOrderToDelete = getGenericDao().findSingleByCriteria(
                Restrictions.eq("cartGuid", shoppingCart.getGuid())
        );
        if (customerOrderToDelete != null) {
            //CPOINT Two ways - delete or not delete existing order with not NONE status
            if (!CustomerOrder.ORDER_STATUS_NONE.equals(customerOrderToDelete.getOrderStatus())) {
                ShopCodeContext.getLog(this).error(
                        MessageFormat.format(
                                "Order {0} with {1} cart guid has {2} order status instead of 1 - ORDER_STATUS_NONE",
                                customerOrderToDelete.getCustomerorderId(),
                                shoppingCart.getGuid(),
                                customerOrderToDelete.getOrderStatus()
                        )
                );
            }

            getGenericDao().delete(customerOrderToDelete);

            getGenericDao().flushClear();

            getGenericDao().evict(customerOrderToDelete);

        }
        final CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);
        deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, onePhysicalDelivery);
        return create(customerOrder);

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
