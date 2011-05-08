package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.CustomerWishList;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerWishListService extends GenericService<CustomerWishList> {

    /**
     * Get customer credit cards.
     *
     * @param customerId customer id
     * @return list of cards.
     */
    List<CustomerWishList> getByCustomerId(long customerId);


}
