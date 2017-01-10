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

package org.yes.cart.dao;

import org.yes.cart.domain.entityindexer.IndexFilter;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Generic DAO that is FT capable.
 *
 * User: denispavlov
 * Date: 10/01/2017
 * Time: 10:30
 */
public interface GenericFullTextSearchCapableDAO<T, PK extends Serializable> extends GenericDAO<T, PK> {

    interface FTIndexState {

        /**
         * Returns true if full reindex job is running.
         *
         * @return true if it is in progress
         */
        boolean isFullTextSearchReindexInProgress();

        /**
         * Returns true if full reindex job is running.
         *
         * @return true if it is in progress
         */
        boolean isFullTextSearchReindexCompleted();

        /**
         * Last index count (either in progress or completed).
         *
         * @return count
         */
        int getLastIndexCount();

    }

    /**
     * @return state of full text index.
     */
    FTIndexState getFullTextIndexState();

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     * @param batchSize batch size for re-indexing
     *
     * @return document quantity in index
     */
    void fullTextSearchReindex(boolean async, int batchSize);

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     * @param batchSize batch size for re-indexing
     * @param filter indexing filter
     *
     * @return document quantity in index
     */
    void fullTextSearchReindex(boolean async, int batchSize, IndexFilter<T> filter);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     *
     * @return document quantity in index
     */
    void fullTextSearchReindex(PK primaryKey);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     * @param purgeOnly true in case if need purge without reindexing from search index
     *
     * @return document quantity in index
     */
    void fullTextSearchReindex(PK primaryKey, boolean purgeOnly);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return list of found entities
     */
    List<T> fullTextSearch(org.apache.lucene.search.Query query);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firstResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     *
     * @return list of found entities
     */
    List<T> fullTextSearch(final org.apache.lucene.search.Query query,
                           int firstResult,
                           int maxResults,
                           String sortFieldName,
                           boolean reverse);

    /**
     * Get the full text search result.
     *
     * @param query         lucene search query
     * @param firstResult   first row of result
     * @param maxResults    size of result set
     * @param sortFieldName optional  sort field name
     * @param reverse       reverse the search result
     * @param fields        list of fields for projections
     *
     * @return list of found entities
     */
    Pair<List<Object[]>, Integer> fullTextSearch(org.apache.lucene.search.Query query,
                                                 int firstResult,
                                                 int maxResults,
                                                 String sortFieldName,
                                                 boolean reverse,
                                                 String ... fields);

    /**
     * Get the full text search result. The map returned by this method should be a single use only.
     * i.e. DO NOT CACHE this method. There are no benefits to this as final FilterNavigationRecord's are already
     * cached and it will make it harder to work with this map as some entries must be thrown away (e.g. zero counts
     * for multi value), sorted (e.g. multivalue).
     *
     * @param query lucene search query
     * @param facetingRequest faceting request context
     *
     * @return list of facets with values and their counts
     */
    Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(org.apache.lucene.search.Query query,
                                                                      List<FilteredNavigationRecordRequest> facetingRequest);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return count items in result
     */
    int fullTextSearchCount(org.apache.lucene.search.Query query);

}
