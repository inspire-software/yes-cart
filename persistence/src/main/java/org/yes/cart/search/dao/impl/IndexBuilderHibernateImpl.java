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

import org.hibernate.*;
//import org.hibernate.search.FullTextSession;
//import org.hibernate.search.Search;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
//import org.hibernate.search.indexes.interceptor.IndexingOverride;
//import org.hibernate.search.util.impl.ClassLoaderHelper;
//import org.hibernate.search.util.impl.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.dao.impl.ResultsIteratorImpl;
import org.yes.cart.search.dao.IndexBuilder;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class IndexBuilderHibernateImpl<T, PK extends Serializable>
        implements IndexBuilder<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(IndexBuilderHibernateImpl.class);

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final Class<T> persistentClass;
    private final boolean persistentClassIndexble;
//    private final EntityIndexingInterceptor entityIndexingInterceptor;
    protected SessionFactory sessionFactory;

    private TaskExecutor indexExecutor;

    /**
     * Set the Hibernate SessionFactory to be used by this DAO.
     * Will automatically create a HibernateTemplate for the given SessionFactory.
     */
    public final void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    /**
     * Executor that will perform indexing jobs asynchronously.
     *
     * @param indexExecutor index executor
     */
    public void setIndexExecutor(final TaskExecutor indexExecutor) {
        this.indexExecutor = indexExecutor;
    }

    /**
     * Default constructor.
     *
     * @param type          - entity type
     */
    @SuppressWarnings("unchecked")
    public IndexBuilderHibernateImpl(final Class<T> type) {
        this.persistentClass = type;
        this.persistentClassIndexble = false; // null != type.getAnnotation(Indexed.class);
        //this.entityIndexingInterceptor = getInterceptor();
    }

//    private EntityIndexingInterceptor getInterceptor() {
//        final Indexed indexed = getPersistentClass().getAnnotation(Indexed.class);
//        if (indexed != null) {
//            final Class<? extends EntityIndexingInterceptor> interceptorClass = indexed.interceptor();
//            if (interceptorClass != null) {
//                return ClassLoaderHelper.instanceFromClass(
//                        EntityIndexingInterceptor.class,
//                        interceptorClass,
//                        "IndexingActionInterceptor for " + getPersistentClass().getName()
//                );
//            }
//        }
//        return null;
//    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findAllIterator() {
        final Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        final ScrollableResults results = crit.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<T>(results);
    }

    private Class<T> getPersistentClass() {
        return persistentClass;
    }


    public void fullTextSearchReindex(PK primaryKey, boolean purgeOnly) {
        if (persistentClassIndexble) {
//            sessionFactory.getCache().evictEntity(getPersistentClass(), primaryKey);
//
//            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
//            fullTextSession.setFlushMode(FlushMode.MANUAL);
//            fullTextSession.setCacheMode(CacheMode.IGNORE);
//            fullTextSession.purge(getPersistentClass(), primaryKey);
//            if (!purgeOnly) {
//                T entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), primaryKey);
//                if(entity != null) {
//                    final T unproxied = (T) HibernateHelper.unproxy(entity);
//
//                    if (entityIndexingInterceptor != null) {
//                        if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onUpdate(unproxied)) {
//                            fullTextSession.index(unproxied);
//                        }
//                    } else {
//                        fullTextSession.index(unproxied);
//                    }
//                }
//
//
//            }
//            fullTextSession.flushToIndexes(); //apply changes to indexes
//            fullTextSession.clear(); //clear since the queue is processed
//
        }
    }


    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final PK primaryKey) {
        fullTextSearchReindex(primaryKey, false);
    }

    private final int IDLE = -3;
    private final int COMPLETED = -1;
    private final int LASTUPDATE = -2;
    private final int RUNNING = 0;

    private final AtomicInteger asyncRunningState = new AtomicInteger(IDLE);
    private final AtomicInteger currentIndexingCount = new AtomicInteger(0);



    /**
     * {@inheritDoc}
     */
    public FTIndexState getFullTextIndexState() {

        return new FTIndexStateImpl(
                asyncRunningState.get() == RUNNING,
                asyncRunningState.get() == COMPLETED,
                currentIndexingCount.get()
        );
    }

    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final boolean async, final int batchSize) {

        final boolean runAsync = async && this.indexExecutor != null;

        asyncRunningState.compareAndSet(COMPLETED, IDLE); // Completed tasks must restart

        if (asyncRunningState.compareAndSet(IDLE, RUNNING)) {  // If we are idle we can start

            if (runAsync) {
                this.indexExecutor.execute(createIndexingRunnable(true, batchSize)); // async
            } else {
                createIndexingRunnable(false, batchSize).run(); // sync
            }

        } else if (!runAsync) {
            // If we are not async but we have a running task, need to wait for it to imitate sync
            while (asyncRunningState.get() == RUNNING) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) { }
            }

        }

    }

    private Runnable createIndexingRunnable(final boolean async, final int batchSize) {
        return new Runnable() {
            @Override
            public void run() {
                int index = 0;
                final Logger log = LOGFTQ;
                try {

                    if (persistentClassIndexble) {

                        currentIndexingCount.set(0);

                        if (log.isInfoEnabled()) {
                            log.info("Full reindex for {} class", persistentClass);
                        }
//                        FullTextSession fullTextSession = Search.getFullTextSession(async ? sessionFactory.openSession() : sessionFactory.getCurrentSession());
//                        fullTextSession.setFlushMode(FlushMode.MANUAL);
//                        fullTextSession.setCacheMode(CacheMode.IGNORE);
//                        ScrollableResults results = fullTextSession.createCriteria(persistentClass)
//                                .setFetchSize(batchSize)
//                                .scroll(ScrollMode.FORWARD_ONLY);
//
//                        try {
//                            while (results.next()) {
//
//                                final T entity = (T) HibernateHelper.unproxy(results.get(0));
//
//                                if (entityIndexingInterceptor != null) {
//                                    if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onUpdate(entity)) {
//                                        fullTextSession.index(entity);
//                                    }
//                                } else {
//                                    fullTextSession.index(entity);
//                                }
//                                index++;
//
//                                if (index % batchSize == 0) {
//                                    fullTextSession.flushToIndexes(); //apply changes to indexes
//                                    fullTextSession.clear(); //clear since the queue is processed
//                                    if (log.isInfoEnabled()) {
//                                        log.info("Indexed {} items of {} class", index, persistentClass);
//                                    }
//                                }
//                                currentIndexingCount.compareAndSet(index - 1, index);
//                            }
//                        } finally {
//                            results.close();
//                        }
//                        fullTextSession.flushToIndexes(); //apply changes to indexes
//                        fullTextSession.clear(); //clear since the queue is processed
//                        if (log.isInfoEnabled()) {
//                            log.info("Indexed {} items of {} class", index, persistentClass);
//                        }
//                        fullTextSession.getSearchFactory().optimize(getPersistentClass());
                    }
                } catch (Exception exp) {
                    LOGFTQ.error("Error during indexing", exp);
                } finally {
                    asyncRunningState.set(COMPLETED);
                    if (async) {
                        try {
                            if (persistentClassIndexble) {
                                sessionFactory.getCurrentSession().close();
                            }
                        } catch (Exception exp) { }
                    }
                    if (log.isInfoEnabled()) {
                        log.info("Full reindex for {} class ... COMPLETED", persistentClass);
                    }
                }
            }
        };
    }

    static class FTIndexStateImpl implements FTIndexState {

        private boolean fullTextSearchReindexInProgress = false;
        private boolean fullTextSearchReindexCompleted = false;
        private int lastIndexCount = 0;

        public FTIndexStateImpl(final boolean fullTextSearchReindexInProgress, final boolean fullTextSearchReindexCompleted, final int lastIndexCount) {
            this.fullTextSearchReindexInProgress = fullTextSearchReindexInProgress;
            this.fullTextSearchReindexCompleted = fullTextSearchReindexCompleted;
            this.lastIndexCount = lastIndexCount;
        }

        @Override
        public boolean isFullTextSearchReindexInProgress() {
            return fullTextSearchReindexInProgress;
        }

        @Override
        public boolean isFullTextSearchReindexCompleted() {
            return fullTextSearchReindexCompleted;
        }

        @Override
        public int getLastIndexCount() {
            return lastIndexCount;
        }
    }

}
