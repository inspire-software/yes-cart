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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CarrierSlaServiceImpl extends BaseGenericServiceImpl<CarrierSla> implements CarrierSlaService {

    /**
     * Construct Service.
     * @param genericDao dao to use.
     */
    public CarrierSlaServiceImpl(final GenericDAO<CarrierSla, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public CarrierSla getById(final long carrierSlaId) {
        return findById(carrierSlaId);
    }

    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findByCarrier(final long carrierId) {
        return getGenericDao().findByNamedQuery("CARRIER.SLA.BY.CARRIER", carrierId);
    }



    private Pair<String, Object[]> findCarrierSlaQuery(final boolean count,
                                                       final String sort,
                                                       final boolean sortDescending,
                                                       final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(c.carrierslaId) from CarrierSlaEntity c ");
        } else {
            hqlCriteria.append("select c from CarrierSlaEntity c ");
        }

        final List carrierIds = currentFilter != null ? currentFilter.remove("carrierIds") : null;
        if (carrierIds != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (c.carrier.carrierId in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (c.carrier.carrierId in (?").append(params.size() + 1).append(")) ");
            }
            params.add(carrierIds);
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



    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findCarrierSlas(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCarrierSlaQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /** {@inheritDoc} */
    @Override
    public int findCarrierSlaCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCarrierSlaQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
