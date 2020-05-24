/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.search.dao;

import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

/**
 * User: denispavlov
 * Date: 31/03/2017
 * Time: 08:55
 */
public interface LuceneIndexProvider {

    /**
     * Index name.
     *
     * @return name
     */
    String getName();

    /**
     * Searcher for given index. Note that provided index searcher operated on cached
     * index, so changes in index will not be visible to this searcher unless
     * {@link #refreshIndexIfNecessary()} is triggered.
     *
     * @return searcher
     */
    IndexSearcher provideIndexReader();

    /**
     * Release searcher.
     *
     * @param searcher searcher
     */
    void releaseIndexReader(IndexSearcher searcher);

    /**
     * Writer for given index. There can be only single writer for index, which is open
     * as application context starts up and is closed automatically when application context
     * is shutdown.
     *
     * @return writer
     */
    IndexWriter provideIndexWriter();

    /**
     * Refreshing the index is necessary do that readers can "see" the most up
     * to date version of index. If refresh is not called the readers only see
     * the index state as it was when this provider was constructed.
     *
     * It is recommended to only trigger this when we know that changes have
     * been made. This is why this method is not part of {@link #provideIndexReader()}.
     */
    void refreshIndexIfNecessary();


    /**
     * Searcher for given index. Note that provided index searcher operated on cached
     * index, so changes in index will not be visible to this searcher unless
     * {@link #refreshFacetsIfNecessary()} is triggered.
     *
     * @return searcher
     */
    SearcherTaxonomyManager.SearcherAndTaxonomy provideFacetsReader();

    /**
     * Release searcher.
     *
     * @param searcher searcher
     */
    void releaseFacetsReader(SearcherTaxonomyManager.SearcherAndTaxonomy searcher);

    /**
     * Writer for given index. There can be only single writer for index, which is open
     * as application context starts up and is closed automatically when application context
     * is shutdown.
     *
     * @return writer
     */
    DirectoryTaxonomyWriter provideFacetsWriter();

    /**
     * Refreshing the index is necessary do that readers can "see" the most up
     * to date version of index. If refresh is not called the readers only see
     * the index state as it was when this provider was constructed.
     *
     * It is recommended to only trigger this when we know that changes have
     * been made. This is why this method is not part of {@link #provideIndexReader()}.
     */
    void refreshFacetsIfNecessary();

    /**
     * Refresh index and facets.
     */
    void refreshIfNecessary();


}
