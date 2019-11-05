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

import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;

import java.io.Serializable;
import java.util.List;

/**
 * Delegate DAO is a wrapper class so that we can configure transaction properties
 * in spring at DAO level.
 *
 * User: denispavlov
 * Date: 06/04/2017
 * Time: 17:38
 */
public class DelegateDAOImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {

    private final GenericDAO<T, PK> delegate;

    public DelegateDAOImpl(final GenericDAO<T, PK> delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityFactory getEntityFactory() {
        return delegate.getEntityFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I> I getEntityIdentifier(final Object entity) {
        return delegate.getEntityIdentifier(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(final PK id, final boolean lock) {
        return delegate.findById(id, lock);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(final PK id) {
        return delegate.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        return delegate.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findAllIterator() {
        return delegate.findAllIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findByCriteriaIterator(final String eCriteria, final Object... parameters) {
        return delegate.findByCriteriaIterator(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findSingleByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        return delegate.findByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByQuery(final String hsqlQuery, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findRangeByQuery(hsqlQuery, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByQuery(final String hsqlQuery, final Object... parameters) {
        return delegate.findCountByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        return delegate.findByQueryIterator(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        return delegate.findSingleByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.getScalarResultByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return delegate.findByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        return delegate.findByNamedQueryForUpdate(namedQueryName, timeout, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findQueryObjectRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectsByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findQueryObjectsRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findCountByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByCriteria(final String eCriteria, final Object... parameters) {
        return delegate.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findRangeByCriteria(final String eCriteria, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findRangeByCriteria(eCriteria, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return delegate.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return delegate.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T create(final T entity) {
        return delegate.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T update(final T entity) {
        return delegate.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T saveOrUpdate(final T entity) {
        return delegate.saveOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Object entity) {
        delegate.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final Object entity) {
        delegate.refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(final Object entity) {
        delegate.evict(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery) {
        return delegate.executeNativeUpdate(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeNativeQuery(final String nativeQuery) {
        return delegate.executeNativeQuery(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeHsqlQuery(final String hsql) {
        return delegate.executeHsqlQuery(hsql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        return delegate.executeHsqlUpdate(hsql, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        return delegate.executeUpdate(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        return delegate.executeNativeUpdate(nativeQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushClear() {
        delegate.flushClear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        delegate.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delegate.clear();
    }

}
