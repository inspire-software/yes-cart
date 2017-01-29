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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;

import java.util.List;

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
    @Cacheable("carrierSlaService-getById")
    public CarrierSla getById(final long carrierSlaId) {
        return carrierSlaService.getById(carrierSlaId);
    }

    /** {@inheritDoc} */
    public List<CarrierSla> findByCarrier(final long carrierId) {
        return carrierSlaService.findByCarrier(carrierId);
    }

    /** {@inheritDoc} */
    public List<CarrierSla> findAll() {
        return carrierSlaService.findAll();
    }

    /** {@inheritDoc} */
    public CarrierSla findById(final long pk) {
        return carrierSlaService.findById(pk);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla create(final CarrierSla instance) {
        return carrierSlaService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla update(final CarrierSla instance) {
        return carrierSlaService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public void delete(final CarrierSla instance) {
        carrierSlaService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<CarrierSla> findByCriteria(final Criterion... criterion) {
        return carrierSlaService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return carrierSlaService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return carrierSlaService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public CarrierSla findSingleByCriteria(final Criterion... criterion) {
        return carrierSlaService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<CarrierSla, Long> getGenericDao() {
        return carrierSlaService.getGenericDao();
    }
}
