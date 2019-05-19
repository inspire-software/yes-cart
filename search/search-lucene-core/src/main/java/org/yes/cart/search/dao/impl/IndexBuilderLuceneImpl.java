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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.LuceneIndexProvider;
import org.yes.cart.search.dao.entity.AdapterUtils;
import org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils;
import org.yes.cart.search.query.impl.LuceneSearchUtil;
import org.yes.cart.utils.TimeContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: denispavlov
 * Date: 06/04/2017
 * Time: 17:04
 */
public abstract class IndexBuilderLuceneImpl<T, PK extends Serializable> implements IndexBuilder<T, PK>  {

    private static final Logger LOG = LoggerFactory.getLogger(IndexBuilderLuceneImpl.class);

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final LuceneDocumentAdapter<T, PK> documentAdapter;
    private final LuceneIndexProvider indexProvider;

    private TaskExecutor indexExecutor;

    public IndexBuilderLuceneImpl(final LuceneDocumentAdapter<T, PK> documentAdapter,
                                  final LuceneIndexProvider indexProvider) {
        this.documentAdapter = documentAdapter;
        this.indexProvider = indexProvider;
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
     * Extension hook for persistence layer.
     *
     * @param primaryKey PK
     *
     * @return entity to index
     */
    protected abstract T findById(final PK primaryKey);

    /**
     * {@inheritDoc}
     */
    @Override
    public void fullTextSearchReindex(final PK primaryKey, final boolean purgeOnly) {

        boolean remove = purgeOnly;

        Pair<PK, Document[]> documents = null;
        if (!purgeOnly) {
            final T entity = this.findById(primaryKey);
            documents = this.documentAdapter.toDocument(entity);
            remove = documents == null || documents.getSecond() == null || documents.getSecond().length == 0;
        }

        final long indexTime = now();
        final String name = indexProvider.getName();
        final IndexWriter iw = indexProvider.provideIndexWriter();
        try {

            long counts[] = new long[] { 0L, 0L, 0L };
            fullTextSearchReindexSingleEntity(iw, name, documents, remove, indexTime, counts);

            if (LOGFTQ.isTraceEnabled()) {
                LOGFTQ.trace("Processed index entity {} with PK {}, added: {}, removed: {}, failed: {}",
                        name, primaryKey, counts[0], counts[1], counts[2]);
            }

            // Refresh ensures we use an updated index
            indexProvider.refreshIfNecessary();

        } catch (Exception exp) {
            LOGFTQ.error("Unable to remove " + name + " document with _PK:" + primaryKey, exp);
        }

    }

    /**
     * Process single entity update in the FT index.
     *
     * @param iw         index writer
     * @param indexName  index name
     * @param documents  documents to index and PK
     * @param remove     remove only
     * @param indexTime  time of this index (added as field to added documents)
     * @param counts     counts[3] = { added, removed, failed }
     *
     * @throws IOException error
     */
    protected void fullTextSearchReindexSingleEntity(final IndexWriter iw,
                                                      final String indexName,
                                                      final Pair<PK, Document[]> documents,
                                                      final boolean remove,
                                                      final long indexTime,
                                                      final long[] counts) throws IOException {

        final PK primaryKey = documents.getFirst();

        // Remove all documents with primary key (could be multiple)
        iw.deleteDocuments(new Term(AdapterUtils.FIELD_PK, String.valueOf(primaryKey)));
        counts[1]++;
        LOGFTQ.trace("Removing {} document _PK:{}", indexName, primaryKey);

        if (!remove) {
            // Add documents
            final FacetsConfig facetsConfig = new FacetsConfig();
            for (final Document document : documents.getSecond()) {
                try {
                    LuceneDocumentAdapterUtils.addNumericField(document, AdapterUtils.FIELD_INDEXTIME, indexTime, false);
                    for (final IndexableField ixf : document) {
                        if (ixf.fieldType() == SortedSetDocValuesFacetField.TYPE) {
                            SortedSetDocValuesFacetField facetField = (SortedSetDocValuesFacetField) ixf;
                            facetsConfig.setIndexFieldName(facetField.dim, facetField.dim);
                            facetsConfig.setMultiValued(facetField.dim, true); // TODO: revisit this but for now all fields assumed to have multivalue
                        }
                    }
                    iw.addDocument(facetsConfig.build(document));
                    counts[0]++;
                } catch (Exception sde) {
                    LOGFTQ.error("Updating {} document _PK:{} failed ... cause: {}", indexName, documents.getFirst(), sde.getMessage());
                    counts[2]++;
                }
            }
            LOGFTQ.trace("Updating {} document _PK:{}", indexName, primaryKey);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fullTextSearchReindex(final PK primaryKey) {
        fullTextSearchReindex(primaryKey, false);
    }

    private final int IDLE = -3;
    private final int COMPLETED = -1;
    private final int LASTUPDATE = -2;
    private final int RUNNING = 0;

    private final AtomicInteger asyncRunningState = new AtomicInteger(IDLE);
    private final AtomicLong currentIndexingCount = new AtomicLong(0);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return indexProvider.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
                } catch (InterruptedException e) {
                    // OK
                }
            }

        }

    }

    /**
     * Extension hook for persistence layer.
     *
     * @return start transaction
     */
    protected abstract Object startTx();

    /**
     * Extension hook for persistence layer.
     *
     * @return scroll through results
     */
    protected abstract ResultsIterator<T> findAllIterator();

    /**
     * Extension hook for persistence layer.
     *
     * @param entity entity
     *
     * @return entity with lazy relationships initialised.
     */
    protected abstract T unproxyEntity(T entity);

    /**
     * Extension hook called on each batch commit.
     *
     * @param tx current tx object
     */
    protected abstract void endBatch(Object tx);

    /**
     * Extension hook for persistence layer.
     *
     * @param tx tx to be committed
     */
    protected abstract void endTx(Object tx);

    private Runnable createIndexingRunnable(final boolean async, final int batchSize) {
        return () -> {
            long index = 0;
            long counts[] = new long[] { 0L, 0L, 0L };

            final Logger log = LOGFTQ;

            Object tx = null;
            try {
                TimeContext.setNow(); // TODO: Time Machine
                currentIndexingCount.set(0);

                final String name = indexProvider.getName();

                if (log.isInfoEnabled()) {
                    log.info("Full reindex for {} class", name);
                }

                if (async) {
                    tx = startTx();
                }

                final long indexTime = now();
                final IndexWriter iw = indexProvider.provideIndexWriter();

                final ResultsIterator<T> all = findAllIterator();

                try {

                    while (all.hasNext()) {

                        final T entity = unproxyEntity(all.next());

                        final Pair<PK, Document[]> documents = documentAdapter.toDocument(entity);
                        boolean remove = documents == null || documents.getSecond() == null || documents.getSecond().length == 0;

                        fullTextSearchReindexSingleEntity(iw, name, documents, remove, indexTime, counts);

                        index++;

                        if (index % batchSize == 0) {
                            // TODO: may need to revisit this in favour of iw.flush()
                            iw.commit();  //apply changes to indexes
                            indexProvider.refreshIfNecessary(); // make changes visible
                            endBatch(tx);
                            if (log.isInfoEnabled()) {
                                log.info("Indexed {} items of {} class", index, indexProvider.getName());
                            }
                        }
                        currentIndexingCount.compareAndSet(index - 1, index);
                    }

                    // Remove unindexed values
                    iw.deleteDocuments(LongPoint.newRangeQuery(AdapterUtils.FIELD_INDEXTIME, 0, indexTime - 1));

                } finally {
                    all.close();
                }

                iw.commit();  //apply changes to indexes
                indexProvider.refreshIfNecessary(); // make changes visible
                endBatch(tx);
                if (log.isInfoEnabled()) {
                    log.info("Indexed {} items of {} class, added: {}, removed: {}, failed: {}", index, indexProvider.getName(), counts[0], counts[1], counts[2]);
                }
                iw.forceMerge(1, true); // optimise the index
            } catch (Exception exp) {
                LOGFTQ.error("Error during indexing", exp);
            } finally {
                asyncRunningState.set(COMPLETED);
                if (async) {
                    try {
                        endTx(tx);
                    } catch (Exception exp) {
                        // OK
                    }
                    LuceneSearchUtil.destroy(); // ensure analysers are unloaded
                }
                if (log.isInfoEnabled()) {
                    log.info("Full reindex for {} class ... COMPLETED", indexProvider.getName());
                }
                TimeContext.destroy();
            }
        };
    }

    long now() {
        return TimeContext.getMillis();
    }

    static class FTIndexStateImpl implements FTIndexState {

        private boolean fullTextSearchReindexInProgress = false;
        private boolean fullTextSearchReindexCompleted = false;
        private long lastIndexCount = 0;

        public FTIndexStateImpl(final boolean fullTextSearchReindexInProgress, final boolean fullTextSearchReindexCompleted, final long lastIndexCount) {
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
        public long getLastIndexCount() {
            return lastIndexCount;
        }
    }

}
