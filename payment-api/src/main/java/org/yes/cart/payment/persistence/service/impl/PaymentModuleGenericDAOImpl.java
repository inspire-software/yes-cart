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

package org.yes.cart.payment.persistence.service.impl;

import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * Generic DAO service for payment modules.
 * Each module can have separate storage.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class PaymentModuleGenericDAOImpl<T, PK extends Serializable>
        implements PaymentModuleGenericDAO<T, PK> {

    private final Class<T> persistentClass;

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
     * @param type - entity type
     */
    @SuppressWarnings("unchecked")
    public PaymentModuleGenericDAOImpl(final Class<T> type) {
        this.persistentClass = type;

        this.selectAllHql = "select e from " + type.getSimpleName() + " e ";
        this.selectCountHql = "select count(e) from " + type.getSimpleName() + " e ";
    }

    /**
     * {@inheritDoc}
     */
    public T findById(final PK id) {
        return findById(id, false);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id, LockOptions.UPGRADE);
        } else {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQuery(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        setQueryParameters(query, parameters);
        return query.list();
    }



    /**
     * {@inheritDoc}
     */
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
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        final Query query = sessionFactory.getCurrentSession().createQuery(this.selectAllHql);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T create(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final T entity) {
        if (entity != null) {
            sessionFactory.getCurrentSession().delete(entity);
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final String eCriteria, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(eCriteria != null ? this.selectAllHql.concat(eCriteria) : this.selectAllHql);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
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


}
