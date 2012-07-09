package org.yes.cart.service.order.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.order.OrderNumberGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 * Generate order numbers - yyyymmdd-xxxxxx format
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DefaultOrderNumberGeneratorImpl implements OrderNumberGenerator {

    private final GenericDAO<CustomerOrder, Long> customerOrderDao ;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static long orderSequence;

    /**
     * Construct order number generator service.
     * @param customerOrderDao order dao to use in check what consequence number need to use.
     */
    public DefaultOrderNumberGeneratorImpl(final GenericDAO<CustomerOrder, Long> customerOrderDao) {
        this.customerOrderDao = customerOrderDao;
        orderSequence = -1;
    }

    /**
     * Default constructor.
     */
    DefaultOrderNumberGeneratorImpl() {
        customerOrderDao = null;
        orderSequence = 0;
    }

    /**
     * Generate Order number.
     *
     * @return Generated order number.
     */
    public synchronized String getNextOrderNumber() {
        final String datePart = dateFormat.format(new Date()); //TODO v2 get from time machine
        return datePart + '-' + getOrderSequence();
    }

    private  long getOrderSequence() {
        if (DefaultOrderNumberGeneratorImpl.orderSequence == -1) {
           DefaultOrderNumberGeneratorImpl.orderSequence = Long.valueOf(String.valueOf(customerOrderDao.getScalarResultByNamedQuery("ORDERS.COUNT")));
        }
        DefaultOrderNumberGeneratorImpl.orderSequence ++;
        return DefaultOrderNumberGeneratorImpl.orderSequence;
    }

}
