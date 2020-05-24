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

package org.yes.cart.service.domain;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;

import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface GenericService<T> {

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Scroll all entities.
     *
     * @param callback callback for next item (return false to terminate forcefully)
     */
    void findAllIterator(ResultsIteratorCallback<T> callback);

    /**
     * Scroll entities.
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     * @param callback callback for next item (return false to terminate forcefully)
     */
    void findByCriteriaIterator(String eCriteria, Object[] parameters, ResultsIteratorCallback<T> callback);

    /**
     * Get object by given primary key.
     *
     * @param pk pk value.
     * @return instance if found, otherwise null.
     */
    T findById(long pk);

    /**
     * Persist instance.
     *
     * @param instance instance to persist
     * @return persisted instance
     */
    T create(T instance);

    /**
     * Update instance.
     *
     * @param instance instance to update
     * @return persisted instance
     */
    T update(T instance);

    /**
     * delete instance.
     *
     * @param instance instance to delete
     */
    void delete(T instance);

    /**
     * Find entities by criteria.
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    List<T> findByCriteria(String eCriteria, Object... parameters);

    /**
     * Find count by criteria.
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    int findCountByCriteria(String eCriteria, Object... parameters);

    /**
     * Find single entity by criteria.
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return single entity or null if not found.
     */
    T findSingleByCriteria(String eCriteria, Object... parameters);

    /**
     * Get generic dao
     *
     * @return generic dao
     */
    GenericDAO<T, Long> getGenericDao();


}