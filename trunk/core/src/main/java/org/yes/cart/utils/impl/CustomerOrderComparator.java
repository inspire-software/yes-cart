package org.yes.cart.utils.impl;


import org.yes.cart.domain.entity.CustomerOrder;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderComparator implements Comparator<CustomerOrder> {

    /** {@inheritDoc} */
    public int compare(final CustomerOrder o1, final CustomerOrder o2) {
        return (o1.getCustomerorderId() < o2.getCustomerorderId() ? -1 : (o1.getCustomerorderId() == o2.getCustomerorderId() ? 0 : 1));
    }

}
