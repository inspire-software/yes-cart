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

package org.yes.cart.payment.service.impl;

import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.PaymentModuleGenericService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class PaymentModuleGenericServiceImpl<ENTITY> implements PaymentModuleGenericService<ENTITY> {

    private final PaymentModuleGenericDAO<ENTITY, Long> genericDao;

    public PaymentModuleGenericServiceImpl(final PaymentModuleGenericDAO<ENTITY, Long> genericDao) {
        this.genericDao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ENTITY> findAll() {
        return genericDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENTITY findById(final long pk) {
        return genericDao.findById(pk);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ENTITY create(final ENTITY instance) {
        return genericDao.create(instance);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ENTITY update(final ENTITY instance) {
        return genericDao.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final ENTITY instance) {
        genericDao.delete(instance);
    }

    /**
     * Get generic dao
     *
     * @return generic dao
     */
    @Override
    public PaymentModuleGenericDAO<ENTITY, Long> getGenericDao() {
        return genericDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ENTITY> findByCriteria(final String eCriteria, final Object... parameters) {
        return genericDao.findByCriteria(eCriteria, parameters);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENTITY findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return genericDao.findSingleByCriteria(eCriteria, parameters);

    }


}
