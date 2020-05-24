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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 15:57
 */
public class CarrierSlaServiceCachedImpl implements CarrierSlaService {

    private final CarrierSlaService carrierSlaService;

    public CarrierSlaServiceCachedImpl(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("carrierSlaService-getById")
    public CarrierSla getById(final long carrierSlaId) {
        return carrierSlaService.getById(carrierSlaId);
    }

    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findByCarrier(final long carrierId) {
        return carrierSlaService.findByCarrier(carrierId);
    }

    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findCarrierSlas(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return carrierSlaService.findCarrierSlas(start, offset, sort, sortDescending, filter);
    }

    /** {@inheritDoc} */
    @Override
    public int findCarrierSlaCount(final Map<String, List> filter) {
        return carrierSlaService.findCarrierSlaCount(filter);
    }

    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findAll() {
        return carrierSlaService.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<CarrierSla> callback) {
        carrierSlaService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<CarrierSla> callback) {
        carrierSlaService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public CarrierSla findById(final long pk) {
        return carrierSlaService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla create(final CarrierSla instance) {
        return carrierSlaService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla update(final CarrierSla instance) {
        return carrierSlaService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public void delete(final CarrierSla instance) {
        carrierSlaService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<CarrierSla> findByCriteria(final String eCriteria, final Object... parameters) {
        return carrierSlaService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return carrierSlaService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public CarrierSla findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return carrierSlaService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<CarrierSla, Long> getGenericDao() {
        return carrierSlaService.getGenericDao();
    }
}
