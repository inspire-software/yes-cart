/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
