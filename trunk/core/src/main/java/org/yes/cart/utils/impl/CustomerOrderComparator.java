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
    public int compare(final CustomerOrder order1, final CustomerOrder order2) {
        return (order1.getCustomerorderId() < order2.getCustomerorderId() ?
                -1 : (order1.getCustomerorderId() == order2.getCustomerorderId() ? 0 : 1));
    }

}
