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

package org.yes.cart.payment.persistence.service;

import java.io.Serializable;
import java.util.List;

/**
 * Generic DAO service for payment modules.
 */
public interface PaymentModuleGenericDAO<T, PK extends Serializable> {

    /**
     * Find entity by Id.
     *
     * @param id   primary key
     * @param lock true if need lock for update.
     *
     * @return instance of T or null if not found
     */
    T findById(PK id, boolean lock);

    /**
     * Find entity by Id.
     *
     * @param id primary key
     *
     * @return instance of T or null if not found
     */
    T findById(PK id);

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<T> findAll();

    /**
     * Find by hsql query.
     *
     * @param hsqlQuery  query
     * @param parameters parameters
     *
     * @return list of objects.
     */
    List<Object> findByQuery(String hsqlQuery, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return entity or null if not found
     */
    T findSingleByNamedQuery(String namedQueryName, Object... parameters);

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
     * Find entities by criteria.
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param firstResult first result
     * @param maxResults max results
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    List<T> findRangeByCriteria(String eCriteria,
                                int firstResult,
                                int maxResults,
                                Object... parameters);


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
     * Persist the new entity in DB.
     *
     * @param entity entity to persist
     *
     * @return persisted entity.
     */
    T create(T entity);

    /**
     * Update the entity in DB.
     *
     * @param entity entity to update
     *
     * @return updated entity.
     */
    T update(T entity);

    /**
     * Save or update the entity. Please, use #create or #update instead of this method.
     *
     * @param entity entity to save or update
     *
     * @return saved or updated entity
     */
    T saveOrUpdate(T entity);

    /**
     * Delete the given entity.
     *
     * @param entity to delete
     */
    void delete(T entity);

}
