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

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.domain.TaxService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 19:00
 */
public class TaxServiceCachedImpl implements TaxService {

    private final TaxService taxService;

    public TaxServiceCachedImpl(final TaxService taxService) {
        this.taxService = taxService;
    }

    /** {@inheritDoc} */
    @Cacheable("taxService-getById")
    public Tax getById(final long pk) {
        return taxService.getById(pk);
    }

    /** {@inheritDoc} */
    @Cacheable("taxService-getTaxesByShopCode")
    public List<Tax> getTaxesByShopCode(final String shopCode, final String currency) {
        return taxService.getTaxesByShopCode(shopCode, currency);
    }

    /** {@inheritDoc} */
    public List<Tax> findByParameters(final String code, final String shopCode, final String currency) {
        return taxService.findByParameters(code, shopCode, currency);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public Tax create(final Tax instance) {
        return taxService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public Tax update(final Tax instance) {
        return taxService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public void delete(final Tax instance) {
        taxService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<Tax> findAll() {
        return taxService.findAll();
    }

    /** {@inheritDoc} */
    public Tax findById(final long pk) {
        return taxService.findById(pk);
    }

    /** {@inheritDoc} */
    public List<Tax> findByCriteria(final Criterion... criterion) {
        return taxService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return taxService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return taxService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public Tax findSingleByCriteria(final Criterion... criterion) {
        return taxService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<Tax, Long> getGenericDao() {
        return taxService.getGenericDao();
    }
}
