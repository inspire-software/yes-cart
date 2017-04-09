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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.yes.cart.search.dao.LuceneIndexProvider;

import java.io.IOException;

/**
 * In Memory implementation of Lucene index provider.
 *
 * FOR TEST PURPOSES ONLY as it uses small byte buffers and result poor performance on large indexes.
 * see {@link RAMDirectory} for more details.
 *
 *
 * User: denispavlov
 * Date: 31/03/2017
 * Time: 08:56
 */
public class RAMLuceneIndexProviderImpl implements LuceneIndexProvider, InitializingBean, DisposableBean {

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private RAMDirectory index = null;
    private SearcherManager indexReaderManager;
    private IndexWriter indexWriter;

    private RAMDirectory facets = null;
    private SearcherTaxonomyManager facetsReaderManager;
    private DirectoryTaxonomyWriter facetsWriter;

    private final String name;

    public RAMLuceneIndexProviderImpl(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexSearcher provideIndexReader() {
        try {
            return indexReaderManager.acquire();
        } catch (IOException e) {
            LOGFTQ.error("Unable to acquire index reader " + name + ", cause: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseIndexReader(final IndexSearcher searcher) {
        try {
            indexReaderManager.release(searcher);
        } catch (IOException e) {
            LOGFTQ.error("Unable to release index reader " + name + ", cause: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWriter provideIndexWriter() {
        if (indexWriter == null) {
            final IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            try {
                return indexWriter = new IndexWriter(index, config);
            } catch (IOException e) {
                LOGFTQ.error("Unable to acquire index writer " + name + ", cause: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return indexWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshIndexIfNecessary() {
        try {
            indexReaderManager.maybeRefreshBlocking();
        } catch (IOException e) {
            LOGFTQ.error("Unable to refresh index " + name + ", cause: " + e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SearcherTaxonomyManager.SearcherAndTaxonomy provideFacetsReader() {
        try {
            return facetsReaderManager.acquire();
        } catch (IOException e) {
            LOGFTQ.error("Unable to acquire facets reader " + name + ", cause: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseFacetsReader(final SearcherTaxonomyManager.SearcherAndTaxonomy searcher) {
        try {
            facetsReaderManager.release(searcher);
        } catch (IOException e) {
            LOGFTQ.error("Unable to release facets reader " + name + ", cause: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryTaxonomyWriter provideFacetsWriter() {
        if (facetsWriter == null) {
            try {
                return facetsWriter = new DirectoryTaxonomyWriter(facets, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            } catch (IOException e) {
                LOGFTQ.error("Unable to acquire facets writer " + name + ", cause: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return facetsWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshFacetsIfNecessary() {
        try {
            facetsReaderManager.maybeRefreshBlocking();
        } catch (IOException e) {
            LOGFTQ.error("Unable to refresh facets " + name + ", cause: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshIfNecessary() {
        this.refreshIndexIfNecessary();
        this.refreshFacetsIfNecessary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        LOGFTQ.info("Starting RAM directory for {}", name);

        index = new RAMDirectory();
        provideIndexWriter();
        indexReaderManager = new SearcherManager(indexWriter, null);

        facets = new RAMDirectory();
        provideFacetsWriter();
        facetsReaderManager = new SearcherTaxonomyManager(indexWriter, null, facetsWriter);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {

        LOGFTQ.info("Closing RAM directory for {}", name);
        try {
            indexReaderManager.close();
        } catch (IOException e) {
            LOGFTQ.error("Unable to release index searchers " + name + ", cause: " + e.getMessage());
        }
        try {
            if (indexWriter != null) {
                indexWriter.close();
            }
        } catch (IOException e) {
            LOGFTQ.error("Unable to release index writer " + name + ", cause: " + e.getMessage());
        }
        index.close();
    }
}
