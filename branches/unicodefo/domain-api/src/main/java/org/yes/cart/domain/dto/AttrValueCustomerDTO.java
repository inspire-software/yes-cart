package org.yes.cart.domain.dto;

/**
 * Customer attribute value.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueCustomerDTO extends AttrValueDTO {

    /**
     * Get customer id.
     *
     * @return customer id.
     */
    long getCustomerId();

    /**
     * Set customer id.
     *
     * @param customerId customer id.
     */
    void setCustomerId(long customerId);

}
