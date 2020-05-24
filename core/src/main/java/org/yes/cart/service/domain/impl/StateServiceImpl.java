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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.State;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.StateService;
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
public class StateServiceImpl extends BaseGenericServiceImpl<State> implements StateService {

    /**
     * Construct Service.
     *
     * @param genericDao dao to use.
     */
    public StateServiceImpl(final GenericDAO<State, Long> genericDao) {
        super(genericDao);
    }



    private Pair<String, Object[]> findStateQuery(final boolean count,
                                                  final String sort,
                                                  final boolean sortDescending,
                                                  final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(s.stateId) from StateEntity s ");
        } else {
            hqlCriteria.append("select s from StateEntity s ");
        }

        final List countryCodes = currentFilter != null ? currentFilter.remove("countryCodes") : null;
        if (CollectionUtils.isNotEmpty(countryCodes)) {
            if (params.size() > 0) {
                hqlCriteria.append(" and (s.countryCode in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" where (s.countryCode in (?1)) ");
            }
            params.add(countryCodes);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "s", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by s." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }


    /** {@inheritDoc} */
    @Override
    public List<State> findStates(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findStateQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );

    }

    /** {@inheritDoc} */
    @Override
    public int findStateCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findStateQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    /** {@inheritDoc} */
    @Override
    public List<State> findByCountry(final String countryCode) {

        return getGenericDao().findByCriteria(
                " where e.countryCode = ?1", countryCode);

    }

}
