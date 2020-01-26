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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CarrierServiceImpl extends BaseGenericServiceImpl<Carrier> implements CarrierService {

    /**
     * Construct service.
     * @param genericDao  doa to use.
     */
    public CarrierServiceImpl(final GenericDAO<Carrier, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> findCarriersByShopId(final long shopId, final boolean includeDisabled) {
        if (includeDisabled) {
            return getGenericDao().findByNamedQuery("CARRIER.BY.SHOPID", shopId);
        }
        return getGenericDao().findByNamedQuery("CARRIER.BY.SHOPID.ENABLEDONLY", shopId, Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> getCarriersByShopId(final long shopId) {
        // This method must be READONLY transaction since we are modifying the list of SLA
        final List<Carrier> rez  = findCarriersByShopId(shopId, false);
        if (CollectionUtils.isEmpty(rez)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(rez);
    }



    private Pair<String, Object[]> findCarrierQuery(final boolean count,
                                                    final String sort,
                                                    final boolean sortDescending,
                                                    final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(distinct c.carrierId) from CarrierEntity c left join c.shops cse ");
        } else {
            hqlCriteria.append("select distinct c from CarrierEntity c left join fetch c.shops cse ");
        }

        final List shops = currentFilter != null ? currentFilter.remove("shopIds") : null;
        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where (cse.shop.shopId in (?1)) ");
            params.add(shops);
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



    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> findCarriers(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCarrierQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCarrierCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCarrierQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
