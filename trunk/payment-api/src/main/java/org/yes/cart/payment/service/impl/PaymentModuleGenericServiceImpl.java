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

package org.yes.cart.payment.service.impl;

import org.hibernate.criterion.Criterion;
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
    public List<ENTITY> findAll() {
        return genericDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public ENTITY getById(final long pk) {
        return genericDao.findById(pk);
    }


    /**
     * {@inheritDoc}
     */
    public ENTITY create(final ENTITY instance) {
        return genericDao.create(instance);
    }


    /**
     * {@inheritDoc}
     */
    public ENTITY update(final ENTITY instance) {
        return genericDao.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final ENTITY instance) {
        genericDao.delete(instance);
    }

    /**
     * Get generic dao
     *
     * @return generic dao
     */
    public PaymentModuleGenericDAO<ENTITY, Long> getGenericDao() {
        return genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<ENTITY> findByCriteria(final Criterion... criterion) {
        return genericDao.findByCriteria(criterion);

    }

    /**
     * {@inheritDoc}
     */
    public ENTITY findSingleByCriteria(final Criterion... criterion) {
        return genericDao.findSingleByCriteria(criterion);

    }


}
