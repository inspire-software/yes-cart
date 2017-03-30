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
import org.yes.cart.search.entityindexer.IndexFilter;

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
    private IndexBuilder<T, PK, org.apache.lucene.search.Query> indexBuilder;


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
                                    final IndexBuilder<T, PK, org.apache.lucene.search.Query> indexBuilder) {
        this.genericDAO = genericDAO;
        this.genericFTS = genericFTS;
        this.indexBuilder = indexBuilder;
    }

    @Override
    public EntityFactory getEntityFactory() {
        return genericDAO.getEntityFactory();
    }

    @Override
    public <I> I getEntityIdentifier(final Object entity) {
        return genericDAO.getEntityIdentifier(entity);
    }

    @Override
    public T findById(final PK id, final boolean lock) {
        return genericDAO.findById(id, lock);
    }

    @Override
    public T findById(final PK id) {
        return genericDAO.findById(id);
    }

    @Override
    public List<T> findAll() {
        return genericDAO.findAll();
    }

    @Override
    public ResultsIterator<T> findAllIterator() {
        return genericDAO.findAllIterator();
    }

    @Override
    public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {
        return genericDAO.findByExample(exampleInstance, excludeProperty);
    }

    @Override
    public <T1> T1 findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findSingleByNamedQuery(namedQueryName, parameters);
    }

    @Override
    public <T1> T1 findSingleByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return genericDAO.findSingleByNamedQueryCached(namedQueryName, parameters);
    }

    @Override
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQuery(hsqlQuery, parameters);
    }

    @Override
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQueryIterator(hsqlQuery, parameters);
    }

    @Override
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findSingleByQuery(hsqlQuery, parameters);
    }

    @Override
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.getScalarResultByNamedQuery(namedQueryName, parameters);
    }

    @Override
    public Object getScalarResultByNamedQueryWithInit(final String namedQueryName, final Object... parameters) {
        return genericDAO.getScalarResultByNamedQueryWithInit(namedQueryName, parameters);
    }

    @Override
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQuery(namedQueryName, parameters);
    }

    @Override
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQueryIterator(namedQueryName, parameters);
    }

    @Override
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        return genericDAO.findByNamedQueryForUpdate(namedQueryName, timeout, parameters);
    }

    @Override
    public List<T> findByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQueryCached(namedQueryName, parameters);
    }

    @Override
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQuery(namedQueryName, parameters);
    }

    @Override
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQueryIterator(namedQueryName, parameters);
    }

    @Override
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    @Override
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectsByNamedQuery(namedQueryName, parameters);
    }

    @Override
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectsRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    @Override
    public List<T> findRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    @Override
    public List<T> findByCriteria(final Criterion... criterion) {
        return genericDAO.findByCriteria(criterion);
    }

    @Override
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion... criterion) {
        return genericDAO.findByCriteria(firstResult, maxResults, criterion);
    }

    @Override
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return genericDAO.findByCriteria(firstResult, maxResults, criterion, order);
    }

    @Override
    public int findCountByCriteria(final Criterion... criterion) {
        return genericDAO.findCountByCriteria(criterion);
    }

    @Override
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return genericDAO.findByCriteria(criteriaTuner, criterion);
    }

    @Override
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion... criterion) {
        return genericDAO.findByCriteria(criteriaTuner, firstResult, maxResults, criterion);
    }

    @Override
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return genericDAO.findByCriteria(criteriaTuner, firstResult, maxResults, criterion, order);
    }

    @Override
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return genericDAO.findCountByCriteria(criteriaTuner, criterion);
    }

    @Override
    public T findSingleByCriteria(final Criterion... criterion) {
        return genericDAO.findSingleByCriteria(criterion);
    }

    @Override
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return genericDAO.findSingleByCriteria(criteriaTuner, criterion);
    }

    @Override
    public T findUniqueByCriteria(final int firstResult, final Criterion... criterion) {
        return genericDAO.findUniqueByCriteria(firstResult, criterion);
    }

    @Override
    public T create(final T entity) {
        return genericDAO.create(entity);
    }

    @Override
    public T update(final T entity) {
        return genericDAO.update(entity);
    }

    @Override
    public T saveOrUpdate(final T entity) {
        return genericDAO.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Object entity) {
        genericDAO.delete(entity);
    }

    @Override
    public void refresh(final Object entity) {
        genericDAO.refresh(entity);
    }

    @Override
    public void evict(final Object entity) {
        genericDAO.evict(entity);
    }

    @Override
    public int executeNativeUpdate(final String nativeQuery) {
        return genericDAO.executeNativeUpdate(nativeQuery);
    }

    @Override
    public List executeNativeQuery(final String nativeQuery) {
        return genericDAO.executeNativeQuery(nativeQuery);
    }

    @Override
    public List executeHsqlQuery(final String hsql) {
        return genericDAO.executeHsqlQuery(hsql);
    }

    @Override
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        return genericDAO.executeHsqlUpdate(hsql, parameters);
    }

    @Override
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        return genericDAO.executeUpdate(namedQueryName, parameters);
    }

    @Override
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        return genericDAO.executeNativeUpdate(nativeQuery, parameters);
    }

    @Override
    public void flushClear() {
        genericDAO.flushClear();
    }

    @Override
    public void flush() {
        genericDAO.flush();
    }

    @Override
    public void clear() {
        genericDAO.clear();
    }

    @Override
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

    @Override
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

    @Override
    public Pair<List<Object[]>, Integer> fullTextSearch(final org.apache.lucene.search.Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse, final String... fields) {
        return genericFTS.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse, fields);
    }

    @Override
    public Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(final org.apache.lucene.search.Query query, final List<FilteredNavigationRecordRequest> facetingRequest) {
        return genericFTS.fullTextSearchNavigation(query, facetingRequest);
    }

    @Override
    public int fullTextSearchCount(final org.apache.lucene.search.Query query) {
        return genericFTS.fullTextSearchCount(query);
    }

    @Override
    public IndexBuilder.FTIndexState getFullTextIndexState() {
        return indexBuilder.getFullTextIndexState();
    }

    @Override
    public void fullTextSearchReindex(final boolean async, final int batchSize) {
        indexBuilder.fullTextSearchReindex(async, batchSize);
    }

    @Override
    public void fullTextSearchReindex(final boolean async, final int batchSize, final IndexFilter<T> filter) {
        indexBuilder.fullTextSearchReindex(async, batchSize, filter);
    }

    @Override
    public void fullTextSearchReindex(final PK primaryKey) {
        indexBuilder.fullTextSearchReindex(primaryKey);
    }

    @Override
    public void fullTextSearchReindex(final PK primaryKey, final boolean purgeOnly) {
        indexBuilder.fullTextSearchReindex(primaryKey, purgeOnly);
    }
}
