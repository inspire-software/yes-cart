package org.yes.cart.search.dao.impl;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.LuceneIndexProvider;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 28/04/2017
 * Time: 08:24
 */
public class IndexBuilderLuceneHibernateTxAwareImpl<T, PK extends Serializable> extends IndexBuilderLuceneImpl<T, PK> {

    private final GenericDAO<T, PK> genericDao;
    protected SessionFactory sessionFactory;

    public IndexBuilderLuceneHibernateTxAwareImpl(final LuceneDocumentAdapter<T, PK> documentAdapter,
                                                  final LuceneIndexProvider indexProvider,
                                                  final GenericDAO<T, PK> genericDao) {
        super(documentAdapter, indexProvider);
        this.genericDao = genericDao;
    }

    /** {@inheritDoc} */
    @Override
    protected T findById(final PK primaryKey) {
        return genericDao.findById(primaryKey);
    }

    /** {@inheritDoc} */
    @Override
    protected ResultsIterator<T> findAllIterator() {
        return genericDao.findAllIterator();
    }

    /** {@inheritDoc} */
    @Override
    protected T unproxyEntity(final T entity) {
        Hibernate.initialize(entity);
        return entity;
    }


    /** {@inheritDoc} */
    @Override
    protected Object startTx() {
        return sessionFactory.getCurrentSession().beginTransaction();
    }


    /** {@inheritDoc} */
    @Override
    protected void endTx(final Object tx) {
        final Transaction transaction = (Transaction) tx;
        transaction.rollback(); // Read only transactions
    }

    /**
     * Sprig IoC.
     *
     * @param sessionFactory session factory
     */
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
