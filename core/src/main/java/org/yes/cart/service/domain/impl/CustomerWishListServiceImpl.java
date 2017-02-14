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

import org.hibernate.Hibernate;
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
    public List<CustomerWishList> findWishListByCustomerId(final long customerId) {
        final List<CustomerWishList> wishList = getGenericDao().findByNamedQuery("WISHLIST.BY.CUSTOMER", customerId);
        for (final CustomerWishList item : wishList) {
            // Need to load the sku information, so that it is available without session
            Hibernate.initialize(item.getSkus());
        }
        return wishList;
    }

    /** {@inheritDoc} */
    public CustomerWishList create(final CustomerWishList instance) {
        return super.create(instance);
    }

    /** {@inheritDoc} */
    public CustomerWishList update(final CustomerWishList instance) {
        return super.update(instance);
    }

    /** {@inheritDoc} */
    public void delete(final CustomerWishList instance) {
        super.delete(instance);
    }
}
