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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.service.domain.GenericService;

import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BaseGenericServiceImpl<ENTITY> implements GenericService<ENTITY> {

    private final GenericDAO<ENTITY, Long> genericDao;

    public BaseGenericServiceImpl(final GenericDAO<ENTITY, Long> genericDao) {
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
    public void findAllIterator(final ResultsIteratorCallback<ENTITY> callback) {

        ResultsIterator<ENTITY> entities = null;

        try {

            entities = genericDao.findAllIterator();

            while (entities.hasNext()) {

                final ENTITY entity = entities.next();
                if (entity != null) {
                    if (!callback.withNext(entity)) {
                        break;
                    }
                }

            }

        } finally {

            if (entities != null) {
                entities.close();
            }

        }

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
    public GenericDAO<ENTITY, Long> getGenericDao() {
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
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return genericDao.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ENTITY findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return genericDao.findSingleByCriteria(eCriteria, parameters);

    }

    /**
     * Get null if string is empty.
     *
     * @param str given string
     * @return string if it not empty, otherwise null
     */
    protected String nullIfEmpty(final String str) {
        return StringUtils.defaultIfEmpty(str, null);
    }

    /**
     * Create like value.
     *
     * @param str given string
     * @return string value, that used in like operation if given string not null, otherwise null.
     */
    protected String likeValue(final String str) {
        if (StringUtils.isNotBlank(str)) {
            return "%" + str + "%";
        }
        return null;
    }


}
