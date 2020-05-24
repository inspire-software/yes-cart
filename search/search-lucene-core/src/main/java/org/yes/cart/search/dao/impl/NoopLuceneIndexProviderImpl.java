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

package org.yes.cart.search.dao.impl;

import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.yes.cart.search.dao.LuceneIndexProvider;

/**
 * User: denispavlov
 * Date: 08/08/2017
 * Time: 15:56
 */
public class NoopLuceneIndexProviderImpl implements LuceneIndexProvider {

    @Override
    public String getName() {
        return "noop";
    }

    @Override
    public IndexSearcher provideIndexReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseIndexReader(final IndexSearcher searcher) {

    }

    @Override
    public IndexWriter provideIndexWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshIndexIfNecessary() {

    }

    @Override
    public SearcherTaxonomyManager.SearcherAndTaxonomy provideFacetsReader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseFacetsReader(final SearcherTaxonomyManager.SearcherAndTaxonomy searcher) {

    }

    @Override
    public DirectoryTaxonomyWriter provideFacetsWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshFacetsIfNecessary() {

    }

    @Override
    public void refreshIfNecessary() {

    }
}
