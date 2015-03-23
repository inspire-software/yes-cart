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
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.util.Date;
import java.util.List;

public class ShoppingCartStateServiceImpl extends BaseGenericServiceImpl<ShoppingCartState> implements ShoppingCartStateService {

    private final GenericDAO<ShoppingCartState, Long> shoppingCartStateDao;


    /**
     * Construct service.
     * @param shoppingCartStateDao shopping cart state dao.
     */
    public ShoppingCartStateServiceImpl(final GenericDAO<ShoppingCartState, Long> shoppingCartStateDao) {
        super(shoppingCartStateDao);
        this.shoppingCartStateDao = shoppingCartStateDao;
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartState findByGuid(final String guid) {
        return shoppingCartStateDao.findSingleByNamedQuery("SHOPPINGCARTSTATE.BY.GUID", guid);
    }

    /**
     * {@inheritDoc}
     */
    public List<ShoppingCartState> findByCustomerEmail(final String email) {
        return shoppingCartStateDao.findByNamedQuery("SHOPPINGCARTSTATE.BY.EMAIL", email);
    }

    /**
     * {@inheritDoc}
     */
    public List<ShoppingCartState> findByModificationPrior(final Date lastModification) {
        return shoppingCartStateDao.findByNamedQuery("SHOPPINGCARTSTATE.BY.LASTMODIFIED", lastModification);
    }

}
