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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.yes.cart.dao.CriteriaTuner;
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
    public EntityFactory getEntityFactory() {
        return delegate.getEntityFactory();
    }

    /**
     * {@inheritDoc}
     */
    public <I> I getEntityIdentifier(final Object entity) {
        return delegate.getEntityIdentifier(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T findById(final PK id, final boolean lock) {
        return delegate.findById(id, lock);
    }

    /**
     * {@inheritDoc}
     */
    public T findById(final PK id) {
        return delegate.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findAll() {
        return delegate.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findAllIterator() {
        return delegate.findAllIterator();
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findSingleByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return delegate.findSingleByNamedQueryCached(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        return delegate.findByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        return delegate.findByQueryIterator(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        return delegate.findSingleByQuery(hsqlQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.getScalarResultByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return delegate.findByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        return delegate.findByNamedQueryForUpdate(namedQueryName, timeout, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        return delegate.findByNamedQueryCached(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectByNamedQueryIterator(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findQueryObjectRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        return delegate.findQueryObjectsByNamedQuery(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findQueryObjectsRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        return delegate.findRangeByNamedQuery(namedQueryName, firstResult, maxResults, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final Criterion... criterion) {
        return delegate.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion... criterion) {
        return delegate.findByCriteria(firstResult, maxResults, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return delegate.findByCriteria(firstResult, maxResults, criterion, order);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return delegate.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return delegate.findByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion... criterion) {
        return delegate.findByCriteria(criteriaTuner, firstResult, maxResults, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        return delegate.findByCriteria(criteriaTuner, firstResult, maxResults, criterion, order);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return delegate.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        return delegate.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return delegate.findSingleByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T findUniqueByCriteria(final int firstResult, final Criterion... criterion) {
        return delegate.findUniqueByCriteria(firstResult, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public T create(final T entity) {
        return delegate.create(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T update(final T entity) {
        return delegate.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T saveOrUpdate(final T entity) {
        return delegate.saveOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Object entity) {
        delegate.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final Object entity) {
        delegate.refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void evict(final Object entity) {
        delegate.evict(entity);
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
        return delegate.executeNativeUpdate(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
        return delegate.executeNativeQuery(nativeQuery);
    }

    /**
     * {@inheritDoc}
     */
    public List executeHsqlQuery(final String hsql) {
        return delegate.executeHsqlQuery(hsql);
    }

    /**
     * {@inheritDoc}
     */
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        return delegate.executeHsqlUpdate(hsql, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        return delegate.executeUpdate(namedQueryName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        return delegate.executeNativeUpdate(nativeQuery, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void flushClear() {
        delegate.flushClear();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        delegate.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        delegate.clear();
    }

}
