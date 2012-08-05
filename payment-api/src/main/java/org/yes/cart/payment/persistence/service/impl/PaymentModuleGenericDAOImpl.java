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

package org.yes.cart.payment.persistence.service.impl;

import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;

import java.io.Serializable;
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

    private final static String UNCHECKED = "unchecked";

    private final  Class<T> persistentClass;

    private   SessionFactory sessionFactory;


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
    @SuppressWarnings(UNCHECKED)
    public PaymentModuleGenericDAOImpl(
            final Class<T> type) {
        this.persistentClass = type;
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
    @SuppressWarnings(UNCHECKED)
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * Get persistent class.
     *
     * @return persistent class.
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<T> findAll() {
        return findByCriteria();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public T saveOrUpdate(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public T create(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public T update(final T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }


    /**
     * {@inheritDoc}
     */
    public List<Object> findByQuery(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        int idx = 1;
        if (parameters != null) {
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            int idx = 1;
            for (Object param : parameters) {
                query.setParameter(String.valueOf(idx), param);
                idx++;
            }
        }
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T> T findSingleByNamedQuery(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQuery(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<T> findByCriteria(final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }


    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (T) crit.uniqueResult();
    }


}
