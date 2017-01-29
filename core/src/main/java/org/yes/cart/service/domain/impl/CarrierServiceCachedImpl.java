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
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.service.domain.CarrierService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 15:48
 */
public class CarrierServiceCachedImpl implements CarrierService {

    private final CarrierService carrierService;

    public CarrierServiceCachedImpl(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * {@inheritDoc}
     */
    public List<Carrier> findCarriersByShopId(final long shopId, final boolean includeDisabled) {
        return carrierService.findCarriersByShopId(shopId, includeDisabled);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable("carrierService-getCarriersByShopIdAndCurrency")
    public List<Carrier> getCarriersByShopId(final long shopId) {
        return carrierService.getCarriersByShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Carrier> findAll() {
        return carrierService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Carrier findById(final long pk) {
        return carrierService.findById(pk);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public Carrier create(final Carrier instance) {
        return carrierService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public Carrier update(final Carrier instance) {
        return carrierService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public void delete(final Carrier instance) {
        carrierService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<Carrier> findByCriteria(final Criterion... criterion) {
        return carrierService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return carrierService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return carrierService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public Carrier findSingleByCriteria(final Criterion... criterion) {
        return carrierService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<Carrier, Long> getGenericDao() {
        return carrierService.getGenericDao();
    }
}
