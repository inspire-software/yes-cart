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

package org.yes.cart.search.dao.impl;

import org.apache.commons.lang.StringUtils;
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

    protected final GenericDAO<T, PK> genericDao;
    protected SessionFactory sessionFactory;
    protected PlatformTransactionManager platformTransactionManager;
    protected String findAllNamedQuery;

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
        if (StringUtils.isBlank(this.findAllNamedQuery)) {
            return genericDao.findAllIterator();
        }
        return genericDao.findByNamedQueryIterator(this.findAllNamedQuery);
    }

    /** {@inheritDoc} */
    @Override
    protected T unproxyEntity(final T entity) {
        Hibernate.initialize(entity);
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    protected void endBatch(final Object tx) {
        this.sessionFactory.getCurrentSession().clear(); // release memory
    }

    /** {@inheritDoc} */
    @Override
    protected Object startTx() {
        TransactionTemplate tx = new TransactionTemplate(this.platformTransactionManager);
        tx.setReadOnly(true);
        return this.platformTransactionManager.getTransaction(tx);
    }


    /** {@inheritDoc} */
    @Override
    protected void endTx(final Object tx) {
        final TransactionStatus transaction = (TransactionStatus) tx;
        transaction.setRollbackOnly(); // Read only transactions
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

    /**
     * Sprig IoC.
     *
     * @param findAllNamedQuery named query to perform find all
     */
    public void setFindAllNamedQuery(final String findAllNamedQuery) {
        this.findAllNamedQuery = findAllNamedQuery;
    }
}
