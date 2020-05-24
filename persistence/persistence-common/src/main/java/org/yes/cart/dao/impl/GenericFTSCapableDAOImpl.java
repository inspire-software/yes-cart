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

package org.yes.cart.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.i18n.I18NModel;
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
        implements GenericFTSCapableDAO<T, PK, Object> {

    private final GenericDAO<T, PK> genericDAO;
    private final GenericFTS<PK, Object> genericFTS;
    private final IndexBuilder<T, PK> indexBuilder;


    /**
     * Default constructor.
     *
     * @param genericDAO    entity DAO
     * @param genericFTS    FTS
     * @param indexBuilder  indexing support
     */
    @SuppressWarnings("unchecked")
    public GenericFTSCapableDAOImpl(final GenericDAO<T, PK> genericDAO,
                                    final GenericFTS<PK, Object> genericFTS,
                                    final IndexBuilder<T, PK> indexBuilder) {
        this.genericDAO = genericDAO;
        this.genericFTS = genericFTS;
        this.indexBuilder = indexBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityFactory getEntityFactory() {
        return genericDAO.getEntityFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I> I getEntityIdentifier(final Object entity) {
        return genericDAO.getEntityIdentifier(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(final PK id, final boolean lock) {
        return genericDAO.findById(id, lock);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(final PK id) {
        return genericDAO.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        return genericDAO.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findAllIterator() {
        return genericDAO.findAllIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findByCriteriaIterator(final String eCriteria, final Object... parameters) {
        return genericDAO.findByCriteriaIterator(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findSingleByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByQuery(final String hsqlQuery, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findRangeByQuery(hsqlQuery, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findCountByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findByQueryIterator(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        return genericDAO.findSingleByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.getScalarResultByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        return genericDAO.findByNamedQueryForUpdate(namedQueryName, timeout, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findQueryObjectsByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findQueryObjectsRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByNamedQuery(final String namedQueryName, final Object... parameters) {
        return genericDAO.findCountByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByCriteria(final String eCriteria, final Object... parameters) {
        return genericDAO.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByCriteria(final String eCriteria, final int firstResult, final int maxResults, final Object... parameters) {
        return genericDAO.findRangeByCriteria(eCriteria, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return genericDAO.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return genericDAO.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T create(final T entity) {
        return genericDAO.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(final T entity) {
        return genericDAO.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T saveOrUpdate(final T entity) {
        return genericDAO.saveOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Object entity) {
        genericDAO.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final Object entity) {
        genericDAO.refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(final Object entity) {
        genericDAO.evict(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery) {
        return genericDAO.executeNativeUpdate(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeNativeQuery(final String nativeQuery) {
        return genericDAO.executeNativeQuery(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeHsqlQuery(final String hsql) {
        return genericDAO.executeHsqlQuery(hsql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        return genericDAO.executeHsqlUpdate(hsql, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        return genericDAO.executeUpdate(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        return genericDAO.executeNativeUpdate(nativeQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushClear() {
        genericDAO.flushClear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        genericDAO.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        genericDAO.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> fullTextSearchRaw(final String query) {
        final List<PK> pks = genericFTS.fullTextSearchRaw(query);
        return getEntitiesByPks(pks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> fullTextSearch(final Object query) {
        final List<PK> pks = genericFTS.fullTextSearch(query);
        return getEntitiesByPks(pks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> fullTextSearch(final Object query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
        final List<PK> pks = genericFTS.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse);
        return getEntitiesByPks(pks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<List<Object[]>, Integer> fullTextSearch(final Object query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse, final String... fields) {
        return genericFTS.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse, fields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> fullTextSearchNavigation(final Object query, final List<FilteredNavigationRecordRequest> facetingRequest) {
        return genericFTS.fullTextSearchNavigation(query, facetingRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fullTextSearchCount(final Object query) {
        return genericFTS.fullTextSearchCount(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexBuilder.FTIndexState getFullTextIndexState() {
        return indexBuilder.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fullTextSearchReindex(final boolean async, final int batchSize) {
        indexBuilder.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fullTextSearchReindex(final PK primaryKey) {
        indexBuilder.fullTextSearchReindex(primaryKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fullTextSearchReindex(final PK primaryKey, final boolean purgeOnly) {
        indexBuilder.fullTextSearchReindex(primaryKey, purgeOnly);
    }

    private List<T> getEntitiesByPks(final List<PK> pks) {
        final List<T> entities = new ArrayList<>();
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


}
