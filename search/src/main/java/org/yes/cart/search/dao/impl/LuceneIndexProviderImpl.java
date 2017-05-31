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
import org.apache.lucene.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.yes.cart.search.dao.LuceneIndexProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public class LuceneIndexProviderImpl implements LuceneIndexProvider, InitializingBean, DisposableBean {

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private Directory index = null;
    private SearcherManager indexReaderManager;
    private IndexWriter indexWriter;

    private Directory facets = null;
    private SearcherTaxonomyManager facetsReaderManager;
    private DirectoryTaxonomyWriter facetsWriter;

    private final String name;
    private final String uri;

    public LuceneIndexProviderImpl(final String name, final String uri) {
        this.name = name;
        this.uri = uri;
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

        index = getInstance(uri + File.separatorChar + name + File.separatorChar + "index");
        provideIndexWriter();
        indexReaderManager = new SearcherManager(indexWriter, null);

        facets = getInstance(uri + File.separatorChar + name + File.separatorChar + "taxonomy");
        provideFacetsWriter();
        facetsReaderManager = new SearcherTaxonomyManager(indexWriter, null, facetsWriter);

    }

    Directory getInstance(final String uri) throws Exception {
        if (uri == null || uri.startsWith("ram" + File.separator)) {
            LOGFTQ.info("Starting RAM directory for {}", name);
            return new RAMDirectory();
        } else if (uri.startsWith("auto://")) {
            final Path path = fromString("auto://", uri);
            LOGFTQ.info("Starting AUTO directory for {} at {}", name, path.toFile().getAbsolutePath());
            return FSDirectory.open(path);
        } else if (uri.startsWith("mmap://")) {
            final Path path = fromString("mmap://", uri);
            LOGFTQ.info("Starting NMap directory for {} at {}", name, path.toFile().getAbsolutePath());
            return new MMapDirectory(path);
        } else if (uri.startsWith("nio://")) {
            final Path path = fromString("nio://", uri);
            LOGFTQ.info("Starting NIO directory for {} at {}", name, path.toFile().getAbsolutePath());
            return new NIOFSDirectory(path);
        } else if (uri.startsWith("simple://")) {
            final Path path = fromString("simple://", uri);
            LOGFTQ.info("Starting Simple directory for {} at {}", name, path.toFile().getAbsolutePath());
            return new SimpleFSDirectory(fromString("simple://", uri));
        }
        throw new IllegalArgumentException("Unable to load index directory from location: " + uri);
    }

    Path fromString(final String prefix, final String uri) {
        return Paths.get(uri.substring(prefix.length()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {

        LOGFTQ.info("Closing directory for {}", name);
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
        try {
            index.close();
        } catch (IOException e) {
            LOGFTQ.error("Unable to release index directory " + name + ", cause: " + e.getMessage());
        }
        try {
            facetsReaderManager.close();
        } catch (IOException e) {
            LOGFTQ.error("Unable to release facet searchers " + name + ", cause: " + e.getMessage());
        }
        try {
            if (facetsWriter != null) {
                facetsWriter.close();
            }
        } catch (IOException e) {
            LOGFTQ.error("Unable to release facet writer " + name + ", cause: " + e.getMessage());
        }
        try {
            facets.close();
        } catch (IOException e) {
            LOGFTQ.error("Unable to release facet directory " + name + ", cause: " + e.getMessage());
        }
    }
}
