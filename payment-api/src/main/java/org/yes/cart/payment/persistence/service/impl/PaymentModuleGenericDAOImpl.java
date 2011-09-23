package org.yes.cart.payment.persistence.service.impl;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
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
public class PaymentModuleGenericDAOImpl<T, PK extends Serializable> extends HibernateDaoSupport
        implements PaymentModuleGenericDAO<T, PK> {

    private final static String UNCHECKED = "unchecked";

    final private Class<T> persistentClass;

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
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = (T) getHibernateTemplate().get(getPersistentClass(), id);
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
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public T create(final T entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public T update(final T entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final T entity) {
        getHibernateTemplate().delete(entity);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<T> findByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = getSession().getNamedQuery(namedQueryName);
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
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }


    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (T) crit.uniqueResult();
    }


}
