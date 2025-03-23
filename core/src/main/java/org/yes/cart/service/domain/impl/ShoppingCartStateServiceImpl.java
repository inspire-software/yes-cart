/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Override
    public ShoppingCartState findByGuid(final String guid) {
        return shoppingCartStateDao.findSingleByNamedQuery("SHOPPINGCARTSTATE.BY.GUID", guid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingCartState> findByCustomerLogin(final String login, final long shopId) {
        return shoppingCartStateDao.findByNamedQuery("SHOPPINGCARTSTATE.BY.LOGIN.AND.SHOP", login, shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingCartState> findByOrdernum(final String ordernum) {
        return shoppingCartStateDao.findByNamedQuery("SHOPPINGCARTSTATE.BY.ORDERNUM", ordernum);
    }

    @Override
    public List<ShoppingCartState> findShoppingCartStates(final int start,
                                                          final int offset,
                                                          final String sort,
                                                          final boolean sortDescending,
                                                          final Map<String, List> filter) {

        final Pair<String, Object[]> query = findShoppingCartStatesQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );

    }

    private Pair<String, Object[]> findShoppingCartStatesQuery(final boolean count,
                                                               final String sort,
                                                               final boolean sortDescending,
                                                               final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(c.shoppingCartStateId) from ShoppingCartStateEntity c ");
        } else {
            hqlCriteria.append("select c from ShoppingCartStateEntity c ");
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "c", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by c." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );


    }
}
