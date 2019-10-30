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

package org.yes.cart.dao;

import java.io.Serializable;
import java.util.List;


/**
 * Generic DAO service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface GenericDAO<T, PK extends Serializable> {

    /**
     * Get the entity factory to create entities.
     *
     * @return configured instance of {@link EntityFactory}
     */
    EntityFactory getEntityFactory();

    /**
     * Get value of PK from an object.
     *
     * @param entity entity object
     * @param <I> type
     *
     * @return id value
     */
    <I> I getEntityIdentifier(Object entity);

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
     * Get all entities iterator (scroll results as opposed to load all)
     *
     * @return scrollable results iterator
     */
    ResultsIterator<T> findAllIterator();

    /**
     * Find by hsql query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2 order by x.foo asc"
     *
     * @param hsqlQuery  query
     * @param parameters parameters
     *
     * @return list of objects.
     */
    List<Object> findByQuery(String hsqlQuery, Object... parameters);


    /**
     * Find entities within HSQL query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2 order by x.foo asc"
     *
     * @param hsqlQuery      query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findRangeByQuery(String hsqlQuery,
                             int firstResult,
                             int maxResults,
                             Object... parameters);


    /**
     * Find count by criteria.
     * E.g. "select count(x) from XEntity x where x.foo = ?1 and x.bar = ?2"
     *
     * @param hsqlQuery HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    int findCountByQuery(String hsqlQuery, Object... parameters);


    /**
     * Find by hsql query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2 order by x.foo asc"
     *
     * @param hsqlQuery  query
     * @param parameters parameters
     *
     * @return list of objects.
     */
    ResultsIterator<Object> findByQueryIterator(String hsqlQuery, Object... parameters);


    /**
     * Find single entity, that returned by named query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. "select x from XEntity x where x.foo = ?1 and x.bar = ?2 order by x.foo asc"
     *
     * @param hsqlQuery  HSQL query string
     * @param parameters optional parameters for named query
     *
     * @return single entity
     */
    Object findSingleByQuery(String hsqlQuery, Object... parameters);


    /**
     * Executes aggregate named query, that return single scalar value.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return single entity
     */
    Object getScalarResultByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return single entity   or null if not found
     */
    T findSingleByNamedQuery(String namedQueryName, Object... parameters);


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
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found entities
     */
    ResultsIterator<T> findByNamedQueryIterator(String namedQueryName, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName    name of query
     * @param timeout           timeout to lock object for
     * @param parameters        optional parameters for named query
     *
     * @return list of found entities
     */
    List<T> findByNamedQueryForUpdate(String namedQueryName, int timeout, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object> findQueryObjectByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    ResultsIterator<Object> findQueryObjectByNamedQueryIterator(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object> findQueryObjectRangeByNamedQuery(String namedQueryName,
                                                  int firstResult,
                                                  int maxResults,
                                                  Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object[]> findQueryObjectsByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     * @param parameters     optional parameters for named query
     *
     * @return list of found objects
     */
    List<Object[]> findQueryObjectsRangeByNamedQuery(String namedQueryName,
                                                     int firstResult,
                                                     int maxResults,
                                                     Object... parameters);


    /**
     * Find entities within named query.
     *
     * Sorting is predefined in named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @param firstResult    first row of result
     * @param maxResults     size of result set
     *
     * @return list of found entities
     */
    List<T> findRangeByNamedQuery(String namedQueryName,
                                  int firstResult,
                                  int maxResults,
                                  Object... parameters);


    /**
     * Find count by criteria.
     *
     * @param namedQueryName HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    int findCountByNamedQuery(String namedQueryName, Object... parameters);


    /**
     * Find entities by criteria.
     * E.g. " where e.foo = ?1 and e.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. " where e.foo = ?1 and e.bar = ?2 order by e.foo asc"
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return iterator of found entities.
     */
    ResultsIterator<T> findByCriteriaIterator(String eCriteria, Object... parameters);

    /**
     * Find entities by criteria.
     * E.g. " where e.foo = ?1 and e.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. " where e.foo = ?1 and e.bar = ?2 order by e.foo asc"
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    List<T> findByCriteria(String eCriteria, Object... parameters);

    /**
     * Find entities by criteria.
     * E.g. " where e.foo = ?1 and e.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. " where e.foo = ?1 and e.bar = ?2 order by e.foo asc"
     *
     * @param eCriteria         HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param firstResult       first result
     * @param maxResults        max results
     * @param parameters        parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    List<T> findRangeByCriteria(String eCriteria,
                                int firstResult,
                                int maxResults,
                                Object... parameters);

    /**
     * Find count by criteria.
     * E.g. " where e.foo = ?1 and e.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. " where e.foo = ?1 and e.bar = ?2 order by e.foo asc"
     *
     * @param eCriteria HQL criteria, to reference entity use "e", e.g. " where e.attr1 = ? and e.attr2 = ?"
     * @param parameters parameters to fill in for question marks
     *
     * @return list of found entities.
     */
    int findCountByCriteria(String eCriteria, Object... parameters);

    /**
     * Find single entity by criteria.
     * E.g. " where e.foo = ?1 and e.bar = ?2"
     *
     * Sorting should be defined in HSQL query.
     * E.g. " where e.foo = ?1 and e.bar = ?2 order by e.foo asc"
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
    void delete(Object entity);

    /**
     * Re-read given entity to get latest state from DB.
     *
     * @param entity to refresh
     */
    void refresh(Object entity);

    /**
     * Evict given entity from the first and second level cache.
     *
     * @param entity to evict
     */
    void evict(Object entity);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     *
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery);

    /**
     * Execute native sql.
     *
     * @param nativeQuery native sql
     *
     * @return result of select.
     */
    List executeNativeQuery(String nativeQuery);

    /**
     * Execute hsql.
     *
     * @param hsql hibernate sql
     *
     * @return result of select.
     */
    List executeHsqlQuery(String hsql);

    /**
     * Execute update.
     *
     * @param hsql       hibernate sql
     * @param parameters hsql query parameters.
     *
     * @return quantity of updated records.
     */
    int executeHsqlUpdate(String hsql, Object... parameters);

    /**
     * Execute update.
     *
     * @param namedQueryName named query name
     * @param parameters     parameters
     *
     * @return quantity of updated records.
     */
    int executeUpdate(String namedQueryName, Object... parameters);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     * @param parameters  sql query parameters.
     *
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery, Object... parameters);

    /**
     * Flush clear session.
     */
    void flushClear();

    /**
     * Flush session.
     */
    void flush();

    /**
     * Clear session.
     */
    void clear();
}
