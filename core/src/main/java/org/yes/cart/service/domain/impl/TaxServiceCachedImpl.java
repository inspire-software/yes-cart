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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.domain.TaxService;

import java.util.List;
import java.util.Map;

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
    @Override
    @Cacheable("taxService-getById")
    public Tax getById(final long pk) {
        return taxService.getById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("taxService-getTaxesByShopCode")
    public List<Tax> getTaxesByShopCode(final String shopCode, final String currency) {
        return taxService.getTaxesByShopCode(shopCode, currency);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> findTaxes(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return taxService.findTaxes(start, offset, sort, sortDescending, filter);
    }

    /** {@inheritDoc} */
    @Override
    public int findTaxCount(final Map<String, List> filter) {
        return taxService.findTaxCount(filter);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public Tax create(final Tax instance) {
        return taxService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public Tax update(final Tax instance) {
        return taxService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "taxService-getTaxesByShopCode",
            "taxService-getById"
    }, allEntries = true)
    public void delete(final Tax instance) {
        taxService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> findAll() {
        return taxService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Tax> callback) {
        taxService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Tax> callback) {
        taxService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public Tax findById(final long pk) {
        return taxService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> findByCriteria(final String eCriteria, final Object... parameters) {
        return taxService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return taxService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Tax findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return taxService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Tax, Long> getGenericDao() {
        return taxService.getGenericDao();
    }
}
