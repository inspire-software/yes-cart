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

import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierSlaService;

import java.util.List;

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
    @Cacheable("carrierSlaService-getById")
    public CarrierSla getById(final long carrierSlaId) {
        return findById(carrierSlaId);
    }

    /** {@inheritDoc} */
    public List<CarrierSla> findByCarrier(final long carrierId) {
        return getGenericDao().findByNamedQuery("CARRIER.SLA.BY.CARRIER", carrierId);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla create(final CarrierSla instance) {
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public CarrierSla update(final CarrierSla instance) {
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "carrierSlaService-getById"
    }, allEntries = false, key = "#instance.carrierslaId")
    public void delete(final CarrierSla instance) {
        super.delete(instance);
    }
}
