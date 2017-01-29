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
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.service.domain.TaxConfigService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:55
 */
public class TaxConfigServiceCachedImpl implements TaxConfigService {

    private final TaxConfigService taxConfigService;

    public TaxConfigServiceCachedImpl(final TaxConfigService taxConfigService) {
        this.taxConfigService = taxConfigService;
    }

    /** {@inheritDoc} */
    @Cacheable("taxConfigService-getTaxIdBy")
    public Long getTaxIdBy(final String shopCode, final String currency, final String countryCode, final String stateCode, final String productCode) {
        return taxConfigService.getTaxIdBy(shopCode, currency, countryCode, stateCode, productCode);
    }

    /** {@inheritDoc} */
    public List<TaxConfig> findByTaxId(final long taxId,
                                       final String countryCode,
                                       final String stateCode,
                                       final String productCode) {
        return taxConfigService.findByTaxId(taxId, countryCode, stateCode, productCode);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxConfigService-getTaxIdBy", allEntries = true)
    public TaxConfig create(final TaxConfig instance) {
        return taxConfigService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxConfigService-getTaxIdBy", allEntries = true)
    public TaxConfig update(final TaxConfig instance) {
        return taxConfigService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = "taxConfigService-getTaxIdBy", allEntries = true)
    public void delete(final TaxConfig instance) {
        taxConfigService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<TaxConfig> findAll() {
        return taxConfigService.findAll();
    }

    /** {@inheritDoc} */
    public TaxConfig findById(final long pk) {
        return taxConfigService.findById(pk);
    }

    /** {@inheritDoc} */
    public List<TaxConfig> findByCriteria(final Criterion... criterion) {
        return taxConfigService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return taxConfigService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return taxConfigService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public TaxConfig findSingleByCriteria(final Criterion... criterion) {
        return taxConfigService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<TaxConfig, Long> getGenericDao() {
        return taxConfigService.getGenericDao();
    }
}
