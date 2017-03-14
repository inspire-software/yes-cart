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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.FixedBitSet;
import org.hibernate.*;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.RowCountProjection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.hibernate.search.query.dsl.DiscreteFacetContext;
import org.hibernate.search.query.dsl.FacetRangeAboveBelowContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.util.impl.ClassLoaderHelper;
import org.hibernate.search.util.impl.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entityindexer.IndexFilter;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericDAOHibernateImpl<T, PK extends Serializable>
        implements GenericFullTextSearchCapableDAO<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericDAOHibernateImpl.class);

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final Class<T> persistentClass;
    private final boolean persistentClassIndexble;
    private final EntityFactory entityFactory;
    private final EntityIndexingInterceptor entityIndexingInterceptor;
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
     * Executer that will perform indexing jobs asynchronously.
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
     * @param entityFactory {@link EntityFactory} to create the entities
     */
    @SuppressWarnings("unchecked")
    public GenericDAOHibernateImpl(final Class<T> type, final EntityFactory entityFactory) {
        this.persistentClass = type;
        this.persistentClassIndexble = null != type.getAnnotation(org.hibernate.search.annotations.Indexed.class);
        this.entityFactory = entityFactory;
        this.entityIndexingInterceptor = getInterceptor();
    }

    private EntityIndexingInterceptor getInterceptor() {
        final Indexed indexed = getPersistentClass().getAnnotation(org.hibernate.search.annotations.Indexed.class);
        if (indexed != null) {
            final Class<? extends EntityIndexingInterceptor> interceptorClass = indexed.interceptor();
            if (interceptorClass != null) {
                return ClassLoaderHelper.instanceFromClass(
                        EntityIndexingInterceptor.class,
                        interceptorClass,
                        "IndexingActionInterceptor for " + getPersistentClass().getName()
                );
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    /**
     * {@inheritDoc}
     */
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
    public T findById(PK id) {
        return findById(id, false);
    }

    private static final LockOptions UPDATE = new LockOptions(LockMode.PESSIMISTIC_WRITE);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T findById(final PK id, final boolean lock) {
        T entity;
        if (lock) {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id, UPDATE);
        } else {
            entity = (T) sessionFactory.getCurrentSession().get(getPersistentClass(), id);
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(final T exampleInstance, final String[] excludeProperty) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public <T> T findSingleByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        List<T> rez = (List<T>) this.findByNamedQueryCached(namedQueryName, parameters);
        if (!rez.isEmpty()) {
            return rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Object getScalarResultByNamedQueryWithInit(final String namedQueryName,  final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        final Object obj = query.uniqueResult();
        if (obj instanceof Product) {
            Hibernate.initialize(((Product) obj).getAttributes());

        }
        return obj;
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
    public ResultsIterator<Object> findByQueryIterator(final String hsqlQuery, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsqlQuery);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return new ResultsIteratorImpl<Object>(query.scroll(ScrollMode.FORWARD_ONLY));
    }

    /**
     * {@inheritDoc}
     */
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
    public ResultsIterator<T> findByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        if (parameters != null) {
            setQueryParameters(query, parameters);
        }
        return new ResultsIteratorImpl<T>(query.scroll(ScrollMode.FORWARD_ONLY));
    }

    /**
     * {@inheritDoc}
     */
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
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQueryCached(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        query.setCacheable(true);
        query.setCacheMode(CacheMode.NORMAL);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object> findQueryObjectByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ResultsIterator<Object> findQueryObjectByNamedQueryIterator(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<Object>(results);
    }

    /**
     * {@inheritDoc}
     */
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
    @SuppressWarnings("unchecked")
    public List<Object[]> findQueryObjectsByNamedQuery(final String namedQueryName, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(namedQueryName);
        setQueryParameters(query, parameters);
        return query.list();
    }

    /**
     * {@inheritDoc}
     */
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
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return findByCriteria();
    }

    /**
     * {@inheritDoc}
     */
    public ResultsIterator<T> findAllIterator() {
        final Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        final ScrollableResults results = crit.scroll(ScrollMode.FORWARD_ONLY);
        return new ResultsIteratorImpl<T>(results);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T saveOrUpdate(T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T create(T entity) {
        sessionFactory.getCurrentSession().save(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        sessionFactory.getCurrentSession().update(entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Object entity) {
        if (entity != null) {
            sessionFactory.getCurrentSession().delete(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(final Object entity) {
        sessionFactory.getCurrentSession().refresh(entity);
    }

    /**
     * {@inheritDoc}
     */
    public void evict(Object entity) {
        sessionFactory.getCurrentSession().evict(entity);

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        crit.setFirstResult(firstResult);
        crit.setMaxResults(maxResults);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        for (Order o : order) {
            crit.addOrder(o);
        }
        crit.setFirstResult(firstResult);
        crit.setMaxResults(maxResults);
        return crit.list();
    }

    @Override
    public int findCountByCriteria(final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        crit.setProjection(new RowCountProjection());
        return ((Number) crit.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final Criterion... criterion) {
        return findSingleByCriteria(null, criterion);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        return crit.list();

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        crit.setFirstResult(firstResult);
        crit.setMaxResults(maxResults);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(final CriteriaTuner criteriaTuner, final int firstResult, final int maxResults, final Criterion[] criterion, final Order[] order) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        for (Order o : order) {
            crit.addOrder(o);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        crit.setFirstResult(firstResult);
        crit.setMaxResults(maxResults);
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {

        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        crit.setProjection(new RowCountProjection());
        return ((Number) crit.uniqueResult()).intValue();
    }

    /**
     * Find entities by criteria.
     * @param firstResult scroll to first result.
     * @param criterion given criteria
     * @return list of found entities.
     */
    @SuppressWarnings("unchecked")
    public T findUniqueByCriteria(final int firstResult, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return (T)  crit.setFirstResult(firstResult).setMaxResults(1).uniqueResult();

    }


    /**
     * {@inheritDoc}
     */
    public T findSingleByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (criteriaTuner != null) {
            criteriaTuner.tune(crit);
        }
        return (T) crit.uniqueResult();
    }


    private Class<T> getPersistentClass() {
        return persistentClass;
    }


    public void fullTextSearchReindex(PK primaryKey, boolean purgeOnly) {
        if (persistentClassIndexble) {
            sessionFactory.getCache().evictEntity(getPersistentClass(), primaryKey);

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            fullTextSession.setFlushMode(FlushMode.MANUAL);
            fullTextSession.setCacheMode(CacheMode.IGNORE);
            fullTextSession.purge(getPersistentClass(), primaryKey);
            if (!purgeOnly) {
                T entity = findById(primaryKey);
                if(entity != null) {
                    final T unproxied = (T) HibernateHelper.unproxy(entity);

                    if (entityIndexingInterceptor != null) {
                        if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onUpdate(unproxied)) {
                            fullTextSession.index(unproxied);
                        }
                    } else {
                        fullTextSession.index(unproxied);
                    }
                }


            }
            fullTextSession.flushToIndexes(); //apply changes to indexes
            fullTextSession.clear(); //clear since the queue is processed

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
        fullTextSearchReindex(async, batchSize, null);
    }

    /**
     * {@inheritDoc}
     */
    public void fullTextSearchReindex(final boolean async, final int batchSize, final IndexFilter<T> indexFilter) {

        final boolean runAsync = async && this.indexExecutor != null;

        asyncRunningState.compareAndSet(COMPLETED, IDLE); // Completed tasks must restart

        if (asyncRunningState.compareAndSet(IDLE, RUNNING)) {  // If we are idle we can start

            if (runAsync) {
                this.indexExecutor.execute(createIndexingRunnable(true, batchSize, indexFilter)); // async
            } else {
                createIndexingRunnable(false, batchSize, indexFilter).run(); // sync
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

    private Runnable createIndexingRunnable(final boolean async, final int batchSize, final IndexFilter<T> filter) {
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
                        FullTextSession fullTextSession = Search.getFullTextSession(async ? sessionFactory.openSession() : sessionFactory.getCurrentSession());
                        fullTextSession.setFlushMode(FlushMode.MANUAL);
                        fullTextSession.setCacheMode(CacheMode.IGNORE);
                        if (filter == null) {  // only purge global full reindex because this clears all entries
                            fullTextSession.purgeAll(getPersistentClass());
                        }
                        ScrollableResults results = fullTextSession.createCriteria(persistentClass)
                                .setFetchSize(batchSize)
                                .scroll(ScrollMode.FORWARD_ONLY);

                        try {
                            while (results.next()) {

                                final T entity = (T) HibernateHelper.unproxy(results.get(0));

                                if (filter != null && filter.skipIndexing(entity)) {
                                    continue; // skip this object
                                }

                                if (entityIndexingInterceptor != null) {
                                    if (IndexingOverride.APPLY_DEFAULT == entityIndexingInterceptor.onUpdate(entity)) {
                                        fullTextSession.index(entity);
                                    }
                                } else {
                                    fullTextSession.index(entity);
                                }
                                index++;

                                if (index % batchSize == 0) {
                                    fullTextSession.flushToIndexes(); //apply changes to indexes
                                    fullTextSession.clear(); //clear since the queue is processed
                                    if (log.isInfoEnabled()) {
                                        log.info("Indexed {} items of {} class", index, persistentClass);
                                    }
                                }
                                currentIndexingCount.compareAndSet(index - 1, index);
                            }
                        } finally {
                            results.close();
                        }
                        fullTextSession.flushToIndexes(); //apply changes to indexes
                        fullTextSession.clear(); //clear since the queue is processed
                        if (log.isInfoEnabled()) {
                            log.info("Indexed {} items of {} class", index, persistentClass);
                        }
                        fullTextSession.getSearchFactory().optimize(getPersistentClass());
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query) {
        if (persistentClassIndexble) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            Query fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private static final Pair<List<Object[]>, Integer> EMPTY = new Pair<List<Object[]>, Integer>(Collections.EMPTY_LIST, 0);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Pair<List<Object[]>, Integer> fullTextSearch(final org.apache.lucene.search.Query query,
                                                        final int firstResult,
                                                        final int maxResults,
                                                        final String sortFieldName,
                                                        final boolean reverse,
                                                        final String ... fields) {

        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run {}x{} {}@{} {}", new Object[] { firstResult, maxResults, sortFieldName, reverse, query });
            }

            final boolean explain = LOGFTQ.isTraceEnabled();
            final FullTextQuery fullTextQuery = createFullTextQuery(query, firstResult, maxResults, sortFieldName, reverse);
            if (explain) {
                final List<String> allFields = new ArrayList<String>(Arrays.asList(fields));
                allFields.add(FullTextQuery.EXPLANATION);
                fullTextQuery.setProjection(allFields.toArray(new String[allFields.size()]));
            } else {
                fullTextQuery.setProjection(fields);
            }
            final List<Object[]> list = fullTextQuery.list();
            if (list != null) {
                if (explain) {
                    final StringBuilder explanation = new StringBuilder("\n");
                    explanation.append("Query: ")
                            .append(query).append("\n");
                    explanation.append("First/Max/Sort/Reverse: ")
                            .append(firstResult).append("/").append(maxResults).append("/").append(sortFieldName).append("/").append(reverse).append("\n");
                    explanation.append(list.size()).append(" result(s): \n\n");
                    for (int i = 0; i < list.size(); i++) {
                        explanation.append(i).append(" =======================================================\n");
                        final Object[] item = list.get(i);
                        for (int ii = 0; ii < item.length - 1; ii++) {
                            explanation.append(item[ii]).append(",");
                        }
                        explanation.append("\n\nreason: ").append(item[item.length - 1]).append("\n");
                    }
                    LOGFTQ.trace(explanation.toString());
                }
                return new Pair<List<Object[]>, Integer>(list, fullTextQuery.getResultSize());
            }

        }
        return EMPTY;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                                  final int firstResult,
                                  final int maxResults,
                                  final String sortFieldName,
                                  final boolean reverse) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run {}x{} {}@{} {}", new Object[] { firstResult, maxResults, sortFieldName, reverse, query });
            }

            final FullTextQuery fullTextQuery = createFullTextQuery(query, firstResult, maxResults, sortFieldName, reverse);
            final List<T> list = fullTextQuery.list();
            if (list != null) {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private FullTextQuery createFullTextQuery(final org.apache.lucene.search.Query query,
                                              final int firstResult,
                                              final int maxResults,
                                              final String sortFieldName,
                                              final boolean reverse) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
        if (sortFieldName != null) {
            Sort sort = new Sort(
                    new SortField(sortFieldName, SortField.STRING, reverse));
            fullTextQuery.setSort(sort);
        }
        fullTextQuery.setFirstResult(firstResult);
        if (maxResults > 0) {
            fullTextQuery.setMaxResults(maxResults);
        }
        return fullTextQuery;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(final org.apache.lucene.search.Query query,
                                                                             final List<FilteredNavigationRecordRequest> facetingRequest) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run facet request with base query {}", query);
            }

            if (facetingRequest == null || facetingRequest.isEmpty()) {
                return Collections.emptyMap();
            }

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(getPersistentClass()).get();

            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            fullTextQuery.setMaxResults(1);
            final FacetManager facetManager = fullTextQuery.getFacetManager();
            boolean hasMultivalue = false;
            for (final FilteredNavigationRecordRequest facetingRequestItem : facetingRequest) {
                if (facetingRequestItem.isRangeValue()) {
                    final FacetRangeAboveBelowContext facetCtx = qb.facet().name(facetingRequestItem.getFacetName())
                            .onField(facetingRequestItem.getField()).range();
                    final Iterator<Pair<String, String>> rageIt = facetingRequestItem.getRangeValues().iterator();
                    while (rageIt.hasNext()) {
                        final Pair<String, String> range = rageIt.next();
                        if (rageIt.hasNext()) {
                            facetCtx.from(range.getFirst()).to(range.getSecond()).excludeLimit();
                        } else {
                            facetManager.enableFaceting(facetCtx.from(range.getFirst()).to(range.getSecond())
                                    .orderedBy(FacetSortOrder.RANGE_DEFINITION_ODER).createFacetingRequest());
                        }
                    }
                } else {
                    final DiscreteFacetContext facetCtx = qb.facet().name(facetingRequestItem.getFacetName())
                            .onField(facetingRequestItem.getField()).discrete();
                    facetManager.enableFaceting(facetCtx
                            .includeZeroCounts(facetingRequestItem.isMultiValue())
                            .createFacetingRequest());
                    if (facetingRequestItem.isMultiValue()) {
                        hasMultivalue = true;
                    }
                }
            }

            final Map<String, List<Pair<String, Integer>>> out = new HashMap<String, List<Pair<String, Integer>>>();
            IndexReader indexReader = null;
            FixedBitSet baseBitSet = null;
            try {

                if (hasMultivalue) {
                    indexReader = fullTextSession.getSearchFactory().getIndexReaderAccessor().open(getPersistentClass());
                    CachingWrapperFilter baseQueryFilter = new CachingWrapperFilter(new QueryWrapperFilter(query));
                    try {
                        DocIdSet docIdSet = baseQueryFilter.getDocIdSet(indexReader);
                        if (docIdSet instanceof FixedBitSet) {
                            baseBitSet = (FixedBitSet) docIdSet;
                        } else {
                            baseBitSet = new FixedBitSet(1);
                        }
                    } catch (IOException e) {
                        LOGFTQ.error("Unable to create base query bit set for query {} and faceting request {}", query, facetingRequest);
                        LOGFTQ.error("Stacktrace:", e);
                        baseBitSet = new FixedBitSet(1);
                    }
                }

                for (final FilteredNavigationRecordRequest facetingRequestItem : facetingRequest) {

                    final List<Pair<String, Integer>> facetsPairs =
                            new ArrayList<Pair<String, Integer>>();

                    final List<Facet> facets =  facetManager.getFacets(facetingRequestItem.getFacetName());

                    LOGFTQ.debug("Faceting request request: {}", facetingRequestItem);

                    if (facetingRequestItem.isMultiValue() && !facetingRequestItem.isRangeValue()) {
                        // Multivalue black magic
                        for (final Facet facet : facets) {

                            final org.apache.lucene.search.Query facetQuery = new TermQuery(new Term(facet.getFieldName(), facet.getValue()));
                            try {
                                CachingWrapperFilter filter = new CachingWrapperFilter(new QueryWrapperFilter(facetQuery));
                                DocIdSet docIdSet = filter.getDocIdSet(indexReader);
                                if (docIdSet instanceof FixedBitSet) {
                                    FixedBitSet filterBitSet = (FixedBitSet) docIdSet;
                                    filterBitSet.and(baseBitSet);
                                    long count = filterBitSet.cardinality();
                                    if (count > 0L) {
                                        LOGFTQ.debug("Has facet: {}", facet);
                                        facetsPairs.add(new Pair<String, Integer>(facet.getValue(), (int) count));
                                    }
                                }
                            } catch (IOException e) {
                                LOGFTQ.error("Unable to create filter query bit set for query {} and faceting query {}", query, facetQuery);
                                LOGFTQ.error("Stacktrace:", e);
                            }

                        }
                    } else {
                        // Standard discrete values and ranges
                        for (final Facet facet : facets) {
                            LOGFTQ.debug("Has facet: {}", facet);
                            facetsPairs.add(new Pair<String, Integer>(facet.getValue(), facet.getCount()));
                        }
                    }
                    out.put(facetingRequestItem.getFacetName(), facetsPairs);
                }
            } finally {
                if (hasMultivalue) {
                    fullTextSession.getSearchFactory().getIndexReaderAccessor().close(indexReader);
                }
            }
            return out;
        }
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchCount(final org.apache.lucene.search.Query query) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Count {}", query);
            }

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            fullTextQuery.setMaxResults(1);
            return fullTextQuery.getResultSize();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        return sqlQuery.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public List executeNativeQuery(final String nativeQuery) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        return sqlQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    public List executeHsqlQuery(final String hsql) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        return query.list();
    }


    /**
     * {@inheritDoc}
     */
    public int executeHsqlUpdate(final String hsql, final Object... parameters) {
        Query query = sessionFactory.getCurrentSession().createQuery(hsql);
        setQueryParameters(query, parameters);
        return query.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
    public int executeNativeUpdate(final String nativeQuery, final Object... parameters) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(nativeQuery);
        setQueryParameters(sqlQuery, parameters);
        return sqlQuery.executeUpdate();
    }


    /**
     * {@inheritDoc}
     */
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
    public void flushClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        sessionFactory.getCurrentSession().clear();
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
