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

import org.hibernate.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Identifiable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericDAOHibernateImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDAOHibernateImpl.class);

    private final Class<T> persistentClass;
    private final EntityFactory entityFactory;
    protected SessionFactory sessionFactory;

    private final String selectAllHql;
    private final String selectCountHql;


    /**
	 * Set the Hibernate SessionFactory to be used by this DAO.
	 * Will automatically create a HibernateTemplate for the given SessionFactory.
	 */
	public final void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

	}



    /**
     * Default constructor.
     *
     * @param type          - entity type
     * @param entityFactory {@link EntityFactory} to create the entities
     */
    @SuppressWarnings("unchecked")
    public GenericDAOHibernateImpl(final Class<T> type, final EntityFactory entityFactory) {
        this.persistentClass = type;
        this.entityFactory = entityFactory;

        this.selectAllHql = "select e from " + type.getSimpleName() + " e ";
        this.selectCountHql = "select count(e) from " + type.getSimpleName() + " e ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <I> I getEntityIdentifier(final Object entity) {
        if (entity == null) {
            // That's ok - it is null
            return null;
        } if (entity instanceof HibernateProxy && !Hibernate.isInitialized(entity)) {
            // Avoid Lazy select by getting identifier from session meta
            // If hibernate proxy is initialised then DO NOT use this approach as chances
            // are that this is detached entity from cache which is not associate with the
            // session and will result in exception
            return (I) sessionFactory.getCurrentSession().getIdentifier(entity);
        } else if (entity instanceof Identifiable) {
            // If it is not proxy or it is initialised then we can use identifiable
            return (I) Long.valueOf(((Identifiable) entity).getId());
        }
        throw new IllegalArgumentException("Cannot get PK from object: " + entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findById(PK id) {
        return findById(id, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = sessionFactory.getCurrentSession().get(getPersistentClass(), id, LockOptions.UPGRADE);
        } else {
            entity = sessionFactory.getCurrentSession().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        List<T> rez = this.findByNamedQuery(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        setQueryParameters(query, parameters);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return new ResultsIteratorImpl<>(query.scroll(ScrollMode.FORWARD_ONLY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object findSingleByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        setQueryParameters(query, parameters);
        final List rez = query.list();
        int size = rez.size();
        switch (size) {
            case 0: {
                return null;
            }
            case 1: {
                return rez.get(0);
            }
            default: {
                LOG.error("#findSingleByQuery has more than one result for {}, [{}]", hsqlQuery, parameters);
                return rez.get(0);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        // TODO: figure out how to perform HQL queries with "fetch collection" using FORWARD_ONLY scroll mode
        /*
            DerbyDriver complains about scrolling with on FORWARD_ONLY with fetch collections

            Example: select p from ProductEntity p left join fetch p.productCategory
                     would fail at Derby ResultSet level complaining about type TYPE_SCROLL_INSENSITIVE

         */
        return new ResultsIteratorImpl<>(query.scroll(ScrollMode.SCROLL_INSENSITIVE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQueryForUpdate(final String namedQueryName, final int timeout, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        LockOptions opts = new LockOptions(LockMode.PESSIMISTIC_WRITE);
        opts.setTimeOut(timeout);
        query.setLockOptions(opts);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<>(results);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> findQueryObjectRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsRangeByNamedQuery(final String namedQueryName, final int firstResult, final int maxResults, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findRangeByNamedQuery(final String namedQueryName,
                                         final int firstResult,
                                         final int maxResults,
                                         final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        setQueryParameters(query, parameters);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        final Query query = sessionFactory.getCurrentSession().createQuery(this.selectAllHql);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ResultsIterator<T> findAllIterator() {
        final Query query = sessionFactory.getCurrentSession().createQuery(this.selectAllHql);
        final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<>(results);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T create(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T update(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Object entity) {
        if (entity != null) {
            sessionFactory.getCurrentSession().delete(entity);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(final Object entity) {
        sessionFactory.getCurrentSession().refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(Object entity) {
        sessionFactory.getCurrentSession().evict(entity);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ResultsIterator<T> findByCriteriaIterator(final String eCriteria, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectAllHql.concat(eCriteria) : this.selectAllHql);
        setQueryParameters(query, parameters);
        return new ResultsIteratorImpl<>(query.scroll(ScrollMode.FORWARD_ONLY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final String eCriteria, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectAllHql.concat(eCriteria) : this.selectAllHql);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findRangeByCriteria(final String eCriteria, final int firstResult, final int maxResults, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectAllHql.concat(eCriteria) : this.selectAllHql);
        setQueryParameters(query, parameters);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectCountHql.concat(eCriteria) : this.selectCountHql);
        setQueryParameters(query, parameters);
        return ((Number) query.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findSingleByCriteria(final String eCriteria, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectAllHql.concat(eCriteria) : this.selectAllHql);
        setQueryParameters(query, parameters);
        query.setMaxResults(1);
        final List<T> rez = query.list();
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    private Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery) {
        NativeQuery sqlQuery = sessionFactory.getCurrentSession().createNativeQuery(nativeQuery);
        return sqlQuery.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeNativeQuery(final String nativeQuery) {
        NativeQuery sqlQuery = sessionFactory.getCurrentSession().createNativeQuery(nativeQuery);
        return sqlQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List executeHsqlQuery(final String hsql) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        setQueryParameters(query, parameters);
        return query.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        NativeQuery sqlQuery = sessionFactory.getCurrentSession().createNativeQuery(nativeQuery);
        setQueryParameters(sqlQuery, parameters);
        return sqlQuery.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate(final String namedQueryName, final Object... parameters) {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.executeUpdate();
    }

    private void setQueryParameters(final Query query, final Object[] parameters) {
        if (parameters != null) {
            int idx = 1;
            for (Object param : parameters) {
                if (param instanceof Collection) {
                    query.setParameterList(String.valueOf(idx), (Collection) param);
                } else {
                    query.setParameter(String.valueOf(idx), param);
                }
                idx++;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        sessionFactory.getCurrentSession().clear();
    }

}
