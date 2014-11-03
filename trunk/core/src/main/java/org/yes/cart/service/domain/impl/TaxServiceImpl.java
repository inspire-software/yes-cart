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

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.domain.TaxService;

import java.util.ArrayList;
import java.util.List;

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
    @Cacheable("taxService-getTaxesByShopCode")
    public List<Tax> getTaxesByShopCode(final String shopCode, final String currency) {
        return getGenericDao().findByNamedQuery("TAX.BY.SHOPCODE.CURRENCY", shopCode, currency);
    }

    /** {@inheritDoc} */
    public List<Tax> findByParameters(final String code, final String shopCode, final String currency) {

        final List<Criterion> criterionList = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(code)) {
            criterionList.add(Restrictions.like("code", code, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(shopCode)) {
            criterionList.add(Restrictions.like("shopCode", shopCode, MatchMode.ANYWHERE));
        }

        if (StringUtils.isNotBlank(currency)) {
            criterionList.add(Restrictions.like("currency", currency, MatchMode.ANYWHERE));
        }

        if (criterionList.isEmpty()) {
            return getGenericDao().findAll();

        } else {
            return getGenericDao().findByCriteria(
                    criterionList.toArray(new Criterion[criterionList.size()])
            );

        }
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxService-getTaxesByShopCode", allEntries = true)
    public Tax create(final Tax instance) {
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxService-getTaxesByShopCode", allEntries = true)
    public Tax update(final Tax instance) {
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxService-getTaxesByShopCode", allEntries = true)
    public void delete(final Tax instance) {
        super.delete(instance);
    }
}
