package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.DeliveryAssembler;
import org.yes.cart.service.order.OrderAssembler;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderServiceImpl extends BaseGenericServiceImpl<CustomerOrder> implements CustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    private final OrderAssembler orderAssembler;

    private final DeliveryAssembler deliveryAssembler;

    private final GenericDAO<Customer, Long> customerDao;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    /**
     * Construct order service.
     *
     * @param customerOrderDao customer order dao.
     * @param orderAssembler order assembler
     * @param deliveryAssembler delivery assembler
     * @param customerDao customer dao to use
     * @param customerOrderPaymentService to calculate order amount payments.
     */
    public CustomerOrderServiceImpl(
            final GenericDAO<CustomerOrder, Long> customerOrderDao,
            final GenericDAO<Customer, Long> customerDao,
            final OrderAssembler orderAssembler,
            final DeliveryAssembler deliveryAssembler,
            final CustomerOrderPaymentService customerOrderPaymentService) {
        super(customerOrderDao);
        this.orderAssembler = orderAssembler;
        this.deliveryAssembler = deliveryAssembler;
        this.customerDao = customerDao;
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /**
     * Get order amount
     *
     * @param orderNumber given order number
     * @return order amount
     */
    public BigDecimal getOrderAmount(final String orderNumber) {
        return customerOrderPaymentService.getOrderAmount(orderNumber);
    }



    /**
     * {@inheritDoc}
     */
    public List<CustomerOrder> findCustomerOrdersByCriterias(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date tillDate,
            final String orderNum
            ) {
        if (customerId > 0) {
            return getGenericDao().findByCriteria(Restrictions.eq("customer", customerDao.findById(customerId)));
        } else {
            return getGenericDao().findByNamedQuery("ORDERS.BY.CRITERIA",
                    likeValue(firstName),
                    likeValue(lastName),
                    likeValue(email),
                    orderStatus,
                    fromDate,
                    tillDate,
                    likeValue(orderNum)
                    );
        }
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
    public boolean isOrderCanHasMultipleDeliveries(final ShoppingCart shoppingCart) {
        final CustomerOrder order = orderAssembler.assembleCustomerOrder(shoppingCart, false);
        return deliveryAssembler.isOrderCanHasMultipleDeliveries(order);
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart, final boolean onePhysicalDelivery) {
        final CustomerOrder customerOrderToDelete = getGenericDao().findSingleByCriteria(
                Restrictions.eq("cartGuid", shoppingCart.getGuid())
        );
        if (customerOrderToDelete != null) {
            //CPOINT Two ways - delete or not delete existing order with not NONE status
            if (!CustomerOrder.ORDER_STATUS_NONE.equals(customerOrderToDelete.getOrderStatus())) {
                LOG.error(
                        MessageFormat.format(
                                "Order {0} with {1} cart guid has {2} order status instead of 1 - ORDER_STATUS_NONE",
                                customerOrderToDelete.getCustomerorderId(),
                                shoppingCart.getGuid(),
                                customerOrderToDelete.getOrderStatus()
                        )
                );
            }
            getGenericDao().delete(customerOrderToDelete);
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


}
