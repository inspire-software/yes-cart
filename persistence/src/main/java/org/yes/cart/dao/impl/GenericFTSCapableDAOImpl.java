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

package org.yes.cart.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.yes.cart.dao.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.GenericFTS;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericFTSCapableDAOImpl<T, PK extends Serializable>
        implements GenericFTSCapableDAO<T, PK, org.apache.lucene.search.Query> {

    private GenericDAO<T, PK> genericDAO;
    private GenericFTS<PK, org.apache.lucene.search.Query> genericFTS;
    private IndexBuilder<T, PK> indexBuilder;


    /**
     * Default constructor.
     *
     * @param genericDAO    entity DAO
     * @param genericFTS    FTS
     * @param indexBuilder  indexing support
     */
    @SuppressWarnings("unchecked")
    public GenericFTSCapableDAOImpl(final GenericDAO<T, PK> genericDAO,
                                    final GenericFTS<PK, org.apache.lucene.search.Query> genericFTS,
                                    final IndexBuilder<T, PK> indexBuilder) {
        this.genericDAO = genericDAO;
        this.genericFTS = genericFTS;
        this.indexBuilder = indexBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public EntityFactory getEntityFactory() {
        return genericDAO.getEntityFactory();
    }

    /**
     * {@inheritDoc}
     */
    public <I> I getEntityIdentifier(final Object entity) {
        return genericDAO.getEntityIdentifier(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T findById(final PK id, final boolean lock) {
        return genericDAO.findById(id, lock);
    }

    /**
     * {@inheritDoc}
     */
    public T findById(final PK id) {
        return genericDAO.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findAll() {
        return genericDAO.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findAllIterator() {
        return genericDAO.findAllIterator();
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findSingleByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return genericDAO.findSingleByNamedQueryCached(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQueryIterator(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findSingleByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.getScalarResultByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        return genericDAO.findByNamedQueryForUpdate(namedQueryName, timeout, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQueryCached(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectsByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectsRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final Criterion... criterion) {
        return genericDAO.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion... criterion) {
        return genericDAO.findByCriteria(firstResult, maxResults, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return genericDAO.findByCriteria(firstResult, maxResults, criterion, order);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return genericDAO.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return genericDAO.findByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion... criterion) {
        return genericDAO.findByCriteria(criteriaTuner, firstResult, maxResults, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return genericDAO.findByCriteria(criteriaTuner, firstResult, maxResults, criterion, order);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        return genericDAO.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return genericDAO.findSingleByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T create(final T entity) {
        return genericDAO.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T update(final T entity) {
        return genericDAO.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T saveOrUpdate(final T entity) {
        return genericDAO.saveOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Object entity) {
        genericDAO.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final Object entity) {
        genericDAO.refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void evict(final Object entity) {
        genericDAO.evict(entity);
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
        return genericDAO.executeNativeUpdate(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
        return genericDAO.executeNativeQuery(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    public List executeHsqlQuery(final String hsql) {
        return genericDAO.executeHsqlQuery(hsql);
    }

    /**
     * {@inheritDoc}
     */
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        return genericDAO.executeHsqlUpdate(hsql, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        return genericDAO.executeUpdate(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        return genericDAO.executeNativeUpdate(nativeQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void flushClear() {
        genericDAO.flushClear();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        genericDAO.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        genericDAO.clear();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query) {
        final List<PK> pks = genericFTS.fullTextSearch(query);
        final List<T> entities = new ArrayList<T>();
        if (CollectionUtils.isNotEmpty(pks)) {
            for (final PK pk : pks) {
                final T entity = genericDAO.findById(pk);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
        final List<PK> pks = genericFTS.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse);
        final List<T> entities = new ArrayList<T>();
        if (CollectionUtils.isNotEmpty(pks)) {
            for (final PK pk : pks) {
                final T entity = genericDAO.findById(pk);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    /**
     * {@inheritDoc}
     */
    public Pair<List<Object[]>, Integer> fullTextSearch(final org.apache.lucene.search.Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse, final String... fields) {
        return genericFTS.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse, fields);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(final org.apache.lucene.search.Query query, final List<FilteredNavigationRecordRequest> facetingRequest) {
        return genericFTS.fullTextSearchNavigation(query, facetingRequest);
    }

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchCount(final org.apache.lucene.search.Query query) {
        return genericFTS.fullTextSearchCount(query);
    }

    /**
     * {@inheritDoc}
     */
    public IndexBuilder.FTIndexState getFullTextIndexState() {
        return indexBuilder.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final boolean async, final int batchSize) {
        indexBuilder.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final PK primaryKey) {
        indexBuilder.fullTextSearchReindex(primaryKey);
    }

    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final PK primaryKey, final boolean purgeOnly) {
        indexBuilder.fullTextSearchReindex(primaryKey, purgeOnly);
    }

}
