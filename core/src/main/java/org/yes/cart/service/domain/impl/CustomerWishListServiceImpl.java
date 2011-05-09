package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.service.domain.CustomerWishListService;

import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerWishListServiceImpl extends BaseGenericServiceImpl<CustomerWishList> implements CustomerWishListService {

    /**
     * Construct service.
     * @param genericDao dao to use.
     */
    public CustomerWishListServiceImpl(final GenericDAO<CustomerWishList, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    public List<CustomerWishList> getByCustomerId(final long customerId) {
        return getGenericDao().findByNamedQuery("WISHLIST.BY.CUSTOMER", customerId);
    }
}
