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

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;

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
public interface GenericFTSCapableDAO<T, PK extends Serializable, FTQ> extends GenericDAO<T, PK> {

    /**
     * @return state of full text index.
     */
    IndexBuilder.FTIndexState getFullTextIndexState();

    /**
     * Force reindex the all entities.
     *
     * @param async true if async required
     * @param batchSize batch size for re-indexing
     */
    void fullTextSearchReindex(boolean async, int batchSize);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     */
    void fullTextSearchReindex(PK primaryKey);

    /**
     * Force reindex given entity.
     *
     * @param primaryKey to reindex.
     * @param purgeOnly true in case if need purge without reindexing from search index
     */
    void fullTextSearchReindex(PK primaryKey, boolean purgeOnly);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return list of found entities
     */
    List<T> fullTextSearch(FTQ query);

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
    List<T> fullTextSearch(FTQ query,
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
    Pair<List<Object[]>, Integer> fullTextSearch(FTQ query,
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
    Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> fullTextSearchNavigation(FTQ query,
                                                                                       List<FilteredNavigationRecordRequest> facetingRequest);


    /**
     * Get the full text search result.
     *
     * @param query lucene search query
     *
     * @return count items in result
     */
    int fullTextSearchCount(FTQ query);

}
