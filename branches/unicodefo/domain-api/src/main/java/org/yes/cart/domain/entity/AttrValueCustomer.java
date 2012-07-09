package org.yes.cart.domain.entity;

/**
 * customer attribute value.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueCustomer extends AttrValue {

    /**
     * Get customer.
     *
     * @return customer
     */
    Customer getCustomer();

    /**
     * Set {@link Customer}
     *
     * @param customer to set
     */
    void setCustomer(Customer customer);


}
