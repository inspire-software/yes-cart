package org.yes.cart.search.dao.impl;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
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
    protected PlatformTransactionManager platformTransactionManager;

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
        TransactionTemplate tx = new TransactionTemplate(this.platformTransactionManager);
        return this.platformTransactionManager.getTransaction(tx);
    }


    /** {@inheritDoc} */
    @Override
    protected void endTx(final Object tx) {
        final TransactionStatus transaction = (TransactionStatus) tx;
        transaction.setRollbackOnly();; // Read only transactions
        this.platformTransactionManager.commit(transaction);
    }

    /**
     * Sprig IoC.
     *
     * @param sessionFactory session factory
     */
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Sprig IoC.
     *
     * @param platformTransactionManager platform manager
     */
    public void setPlatformTransactionManager(final PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }
}
