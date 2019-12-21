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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 19:27
 */
public class TaxServiceImpl  extends BaseGenericServiceImpl<Tax> implements TaxService {

    public TaxServiceImpl(final GenericDAO<Tax, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public Tax getById(final long pk) {
        return findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> getTaxesByShopCode(final String shopCode, final String currency) {
        return getGenericDao().findByNamedQuery("TAX.BY.SHOPCODE.CURRENCY", shopCode, currency);
    }



    private Pair<String, Object[]> findTaxQuery(final boolean count,
                                                final String sort,
                                                final boolean sortDescending,
                                                final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(t.taxId) from TaxEntity t ");
        } else {
            hqlCriteria.append("select t from TaxEntity t ");
        }

        final List shopIds = currentFilter != null ? currentFilter.remove("shopCodes") : null;
        if (shopIds != null) {
            hqlCriteria.append(" where (t.shopCode in (?1)) ");
            params.add(shopIds);
        }
        final List currencies = currentFilter != null ? currentFilter.remove("currencies") : null;
        if (currencies != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (t.currency in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (t.currency in (?").append(params.size() + 1).append(")) ");
            }
            params.add(currencies);
        }

        final List exclusiveOfPrice = currentFilter != null ? currentFilter.remove("exclusiveOfPrice") : null;
        if (exclusiveOfPrice != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (t.exclusiveOfPrice = (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (t.exclusiveOfPrice = (?").append(params.size() + 1).append(")) ");
            }
            params.add(exclusiveOfPrice.get(0));
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "t", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by t." + sort + " " + (sortDescending ? "desc" : "asc"));

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
    public List<Tax> findTaxes(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findTaxQuery(false, sort, sortDescending, filter);

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
    public int findTaxCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findTaxQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    private void regenerateGuid(final Tax entity) {
        final StringBuilder guid = new StringBuilder();
        guid.append(entity.getShopCode()).append('_');
        guid.append(entity.getCurrency()).append('_');
        guid.append(entity.getCode());
        entity.setGuid(guid.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Tax create(final Tax instance) {
        regenerateGuid(instance);
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    public Tax update(final Tax instance) {
        regenerateGuid(instance);
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Tax instance) {
        super.delete(instance);
    }
}
