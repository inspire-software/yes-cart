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

package org.yes.cart.dao;

import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.Collection;
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
     * Find entity by Id.
     *
     * @param id   primary key
     * @param lock true if need lock for update.
     * @return instance of T or null if not found
     */
    T findById(PK id, boolean lock);

    /**
     * Find entity by Id.
     *
     * @param id primary key
     * @return instance of T or null if not found
     */
    T findById(PK id);

    /**
     * Get the All entities.
     *
     * @return lis tof all entities
     */
    List<T> findAll();

    /**
     * Find entities, that mach given example.
     *
     * @param exampleInstance pattern
     * @param excludeProperty property to exclude
     * @return list of found entities
     */
    List<T> findByExample(T exampleInstance, String[] excludeProperty);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return single entity   or null if not found
     */
    <T> T findSingleByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find single entity, that returned by named query.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return single entity   or null if not found
     */
    <T> T findSingleByNamedQueryCached(String namedQueryName, Object... parameters);

    /**
     * Find by hsql query.
     *
     * @param hsqlQuery  query
     * @param parameters parameters
     * @return list of objects.
     */
    /*List<T> ???*/List<Object> findByQuery(String hsqlQuery, Object... parameters);


    /**
     * Find single entity, that returned by named query.
     *
     * @param hsqlQuery  HSQL query string
     * @param parameters optional parameters for named query
     * @return single entity
     */
    Object findSingleByQuery(String hsqlQuery, Object... parameters);


    /**
     * Executes agregate named query, that return single scalar value.
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return single entity
     */
    Object getScalarResultByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Executes aggregate named query, that return single scalar value. And force load collections .
     *
     * @param namedQueryName      name of query
     * @param parameters          optional parameters for named query
     * @return single entity
     */
    Object getScalarResultByNamedQueryWithInit(String namedQueryName,  Object... parameters);


    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return list of found entities
     */
    List<T> findByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param timeout timeout to lock object for
     * @param parameters     optional parameters for named query
     * @return list of found entities
     */
    List<T> findByNamedQueryForUpdate(String namedQueryName, int timeout, Object... parameters);

    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return list of found entities
     */
    List<T> findByNamedQueryCached(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return list of found objects
     */
    List<Object> findQueryObjectByNamedQuery(String namedQueryName, Object... parameters);

    /**
     * Find "query objects" within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @return list of found objects
     */
    List<Object[]> findQueryObjectsByNamedQuery(String namedQueryName, Object... parameters);


    /**
     * Find "query objects" within named query that use IN clause.
     *
     * @param namedQueryName name of query
     * @param parameter      list parameter for named query
     * @return list of found objects
     *
     * @deprecated use generic object query instead
     */
    @Deprecated
    List<Object[]> findQueryObjectsByNamedQueryWithList(String namedQueryName, List parameter);

    /**
     * Find "query objects" within named query that use IN clause.
     *
     * @param namedQueryName name of query
     * @param listParameter  list parameter for named query
     * @param parameters     optional parameters for named query
     * @return list of found objects
     *
     * @deprecated use generic object query instead
     */
    @Deprecated
    List<T> findQueryObjectsByNamedQueryWithList(String namedQueryName, Collection<Object> listParameter, Object... parameters);



    /**
     * Find entities within named query .
     *
     * @param namedQueryName name of query
     * @param parameters     optional parameters for named query
     * @param firtsResult    first row of result
     * @param maxResults     size of result set
     * @return list of found entities
     */
    List<T> findRangeByNamedQuery(String namedQueryName,
                                  int firtsResult,
                                  int maxResults,
                                  Object... parameters);

    /**
     * Find entities by criteria.
     *
     * @param criterion given criterias
     * @return list of found entities.
     */
    List<T> findByCriteria(Criterion... criterion);

    /**
     * Find entities by criteria.
     *
     * @param criteriaTuner optional criteria tuner.
     * @param criterion     given criterias
     * @return list of found entities.
     */
    List<T> findByCriteria(CriteriaTuner criteriaTuner, Criterion... criterion);

    /**
     * Find single entity by criteria.
     *
     * @param criterion given criterias
     * @return single entity or null if not found.
     */
    T findSingleByCriteria(Criterion... criterion);

    /**
     * Find single entity by criteria.
     *
     * @param criteriaTuner optional criteria tuner.
     * @param criterion     given criterias
     * @return single entity or null if not found.
     */
    T findSingleByCriteria(CriteriaTuner criteriaTuner, Criterion... criterion);

    /**
     * Find entities by criteria.
     *
     * @param firstResult scroll to firts result.
     * @param criterion   given criterias
     * @return list of found entities.
     */
    T findUniqueByCriteria(int firstResult, Criterion... criterion);


    /**
     * Persist the new enitity in DB.
     *
     * @param entity entity to persist
     * @return persisted entity.
     */
    T create(T entity);

    /**
     * Update the enitity in DB.
     *
     * @param entity entity to update
     * @return updated entity.
     */
    T update(T entity);

    /**
     * Save or update the entity. Please, use #create or #update instead of this method.
     *
     * @param entity entity to update
     * @return updated entity.
     */
    T saveOrUpdate(T entity);

    /**
     * Delete the given entity.
     *
     * @param entity to delete
     */
    void delete(Object entity);

    /**
     * Evict given entity from the first and second level cache.
     *
     * @param entity to evict
     */
    void evict(Object entity);

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     * @return document quantity in index
     */
    int fullTextSearchReindex(boolean async);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     * @return document quantity in index
     */
    int fullTextSearchReindex(PK primaryKey);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     * @param purgeOnly true in case if need purge without reindexing from search index
     * @return document quantity in index
     */
    int fullTextSearchReindex(PK primaryKey, boolean purgeOnly);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     * @return list of found entities
     */
    List<T> fullTextSearch(org.apache.lucene.search.Query query);

    /**
     * Get the full text search result.
     *
     * @param query       lucene search query
     * @param firtsResult first row of result
     * @param maxResults  size of result set
     * @return list of found entities
     */
    List<T> fullTextSearch(org.apache.lucene.search.Query query,
                           int firtsResult,
                           int maxResults);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firtsResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     * @return list of found entities
     */
    List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                           int firtsResult,
                           int maxResults,
                           String sortFieldName,
                           boolean reverse);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firtsResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     * @param fields        list of fields for projections
     * @return list of found entities
     */
    List<Object[]> fullTextSearch(final org.apache.lucene.search.Query query,
                                  int firtsResult,
                                  int maxResults,
                                  String sortFieldName,
                                  boolean reverse,
                                  String ... fields);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firtsResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional sort field name, result will be asc ordered
     * @return list of found entities
     */
    List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                           int firtsResult,
                           int maxResults,
                           String sortFieldName);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     * @return count iterms in result
     */
    int getResultCount(org.apache.lucene.search.Query query);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     * @return quantity of updated / deleted rows
     */
    int executeNativeUpdate(String nativeQuery);

    /**
     * Execute native sql.
     *
     * @param nativeQuery native sql
     * @return result of select.
     */
    List executeNativeQuery(String nativeQuery);

    /**
     * Execute hsql.
     *
     * @param hsql hibernate sql
     * @return result of select.
     */
    List executeHsqlQuery(String hsql);

    /**
     * Execute update.
     *
     * @param hsql       hibernate sql
     * @param parameters hsql query parameters.
     * @return quantity of updated resords.
     */
    int executeHsqlUpdate(String hsql, Object... parameters);

    /**
     * Execute update.
     *
     * @param namedQueryName named quesry name
     * @param parameters     parameters
     * @return quantity of updated resords.
     */
    int executeUpdate(String namedQueryName, Object... parameters);

    /**
     * Execute native delete / update sql.
     *
     * @param nativeQuery native sql
     * @param parameters  sql query parameters.
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
