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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.CartContentsValidator;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.HQLUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImpl extends BaseGenericServiceImpl<CustomerOrder> implements CustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    private final OrderNumberGenerator orderNumberGenerator;

    private final OrderAssembler orderAssembler;

    private final DeliveryAssembler deliveryAssembler;

    private final OrderSplittingStrategy orderSplittingStrategy;

    private final CartContentsValidator cartContentsValidator;

    private final GenericDAO<Customer, Long> customerDao;

    private final GenericDAO<Object, Long> genericDao;

    private final GenericDAO<CustomerOrderDelivery, Long> customerOrderDeliveryDao;


    /**
     * Construct order service.
     *
     * @param customerOrderDao customer order dao.
     * @param customerDao customer dao to use
     * @param customerOrderDeliveryDao to get deliveries, awiting for inventory
     * @param orderNumberGenerator   order number generator
     * @param orderAssembler order assembler
     * @param deliveryAssembler delivery assembler
     * @param orderSplittingStrategy order splitting strategy
     * @param cartContentsValidator cart contents validator
     */
    public CustomerOrderServiceImpl(final GenericDAO<CustomerOrder, Long> customerOrderDao,
                                    final GenericDAO<Customer, Long> customerDao,
                                    final GenericDAO<Object, Long> genericDao,
                                    final GenericDAO<CustomerOrderDelivery, Long> customerOrderDeliveryDao,
                                    final OrderNumberGenerator orderNumberGenerator,
                                    final OrderAssembler orderAssembler,
                                    final DeliveryAssembler deliveryAssembler,
                                    final OrderSplittingStrategy orderSplittingStrategy,
                                    final CartContentsValidator cartContentsValidator) {
        super(customerOrderDao);
        this.orderNumberGenerator = orderNumberGenerator;
        this.orderAssembler = orderAssembler;
        this.deliveryAssembler = deliveryAssembler;
        this.customerDao = customerDao;
        this.genericDao = genericDao;
        this.customerOrderDeliveryDao = customerOrderDeliveryDao;
        this.orderSplittingStrategy = orderSplittingStrategy;
        this.cartContentsValidator = cartContentsValidator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrderDelivery findDelivery(final long deliveryId) {
        return customerOrderDeliveryDao.findById(deliveryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrderDelivery findDeliveryByNumber(final String deliveryNum) {
        return customerOrderDeliveryDao.findSingleByCriteria(" where e.deliveryNum = ?1", deliveryNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findEligibleForExportOrderIds() {
        return (List) customerOrderDeliveryDao.findQueryObjectByNamedQuery("ORDERS.IDS.BY.ELIGIBLE.FOR.EXPORT", Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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

    private Pair<String, Object[]> findCustomerOrderQuery(final boolean count,
                                                          final String sort,
                                                          final boolean sortDescending,
                                                          final Set<Long> shops,
                                                          final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(o) from CustomerOrderEntity o ");
        } else {
            hqlCriteria.append("select o from CustomerOrderEntity o ");
        }

        final List statuses  = currentFilter != null ? currentFilter.remove("orderStatus") : null;

        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where (o.shop.shopId in (?1) or o.shop.master.shopId in (?1)) ");
            params.add(shops);
        }

        if (CollectionUtils.isNotEmpty(statuses)) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where o.orderStatus in ?1 ");
            } else {
                hqlCriteria.append(" and o.orderStatus in ?2 ");
            }
            params.add(statuses);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "o", currentFilter);


        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by o." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrder> findCustomerOrder(final int start,
                                                 final int offset,
                                                 final String sort,
                                                 final boolean sortDescending,
                                                 final Set<Long> shops,
                                                 final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerOrderQuery(false, sort, sortDescending, shops, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCustomerOrderCount(final Set<Long> shops,
                                      final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerOrderQuery(true, null, false, shops, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findCustomerOrderIdsByDeliveryIds(final Collection<Long> deliveryIds) {
        return (List) getGenericDao().findQueryObjectByNamedQuery("ORDER.IDS.BY.DELIVERY.IDS", deliveryIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrder> findCustomerOrders(final long customerId, final LocalDateTime since) {
        return findCustomerOrders(customerDao.findById(customerId), since);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrder> findCustomerOrders(final Customer customer, final LocalDateTime since) {
        if (since == null) {
            return getGenericDao().findByCriteria(
                    " where e.customer = ?1", customer
            );
        }
        return getGenericDao().findByCriteria(
                " where e.customer = ?1 and e.orderTimestamp >= ?2", customer, since
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> isOrderMultipleDeliveriesAllowed(final ShoppingCart shoppingCart) {

        return orderSplittingStrategy.isMultipleDeliveriesAllowed(
                shoppingCart.getShoppingContext().getCustomerShopId(),
                shoppingCart.getCartItemList()
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartValidityModel validateCart(final ShoppingCart shoppingCart) {
        return cartContentsValidator.validate(shoppingCart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart)
            throws OrderAssemblyException {


        final boolean checkoutBlocked = Boolean.valueOf(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT));
        if (checkoutBlocked) {
            throw new PlaceOrderDisabledException(shoppingCart.getCustomerEmail());
        }

        if (validateCart(shoppingCart).isCheckoutBlocked()) {
            throw new OrderAssemblyException("Cart validation failed");
        }

        final Map<String, Boolean> onePhysicalDelivery = new HashMap<>();
        for (final Map.Entry<String, Boolean> isAllowed : shoppingCart.getOrderInfo().getMultipleDeliveryAvailable().entrySet()) {
            onePhysicalDelivery.put(isAllowed.getKey(), !shoppingCart.getOrderInfo().isMultipleDelivery() || !isAllowed.getValue());
        }

        final CustomerOrder customerOrderToDelete = findByGuid(shoppingCart.getGuid());

        if (customerOrderToDelete != null) {

            final boolean orderIsProcessedByPg = !CustomerOrder.ORDER_STATUS_NONE.equals(customerOrderToDelete.getOrderStatus());
            final boolean orderHasPgSet = StringUtils.isNotBlank(customerOrderToDelete.getPgLabel());

            if (orderIsProcessedByPg || orderHasPgSet) {

                if (orderIsProcessedByPg) {
                    // Order is not in ORDER_STATUS_NONE but the cart had not been cleaned
                    // This can only happen if they did not click come back to site from the external payment site
                    // Meanwhile the callback happened and updated the order

                    LOG.warn("Order {} with {} cart guid has {} order status instead of 1 - ORDER_STATUS_NONE, keeping the order",
                            customerOrderToDelete.getCustomerorderId(), shoppingCart.getGuid(), customerOrderToDelete.getOrderStatus()
                    );
                } else /* if (orderHasPgSet) */ {
                    // If we have an order that is os.none but we have PGLabel it may indicate that
                    // customer is on an external payment form and came back to re-check in which case
                    // we need to retain the order reference which was sent to external PG

                    LOG.warn("Order {} with {} cart guid has {} order status and PG {}, keeping the order",
                            customerOrderToDelete.getCustomerorderId(), shoppingCart.getGuid(), customerOrderToDelete.getOrderStatus(), customerOrderToDelete.getPgLabel()
                    );
                }

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

        final String orderNumber = orderNumberGenerator.getNextOrderNumber();
        final CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, orderNumber);
        deliveryAssembler.assembleCustomerOrder(customerOrder, shoppingCart, onePhysicalDelivery);
        return create(customerOrder);

    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public CustomerOrder findByDeliveryReference(final String reference) {
        final CustomerOrderDelivery delivery = findDeliveryByNumber(reference);
        if (delivery != null) {
            Hibernate.initialize(delivery.getCustomerOrder());
            return delivery.getCustomerOrder();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrder findByGuid(final String shoppingCartGuid) {
        return getGenericDao().findSingleByCriteria(
                " where e.cartGuid = ?1", shoppingCartGuid
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrder findByOrderNumber(final String orderNumber) {
        return getGenericDao().findSingleByCriteria(
                " where e.ordernum = ?1", orderNumber
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final CustomerOrder instance) {
        for (CustomerOrderDelivery delivery : instance.getDelivery()) {
            this.genericDao.delete(delivery);
        }
        super.delete(instance);
    }

}
