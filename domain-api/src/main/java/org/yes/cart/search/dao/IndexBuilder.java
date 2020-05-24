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

import java.io.Serializable;

/**
 * Index builder uses {@link GenericFTS} and {@link org.yes.cart.dao.GenericDAO} to mediate data and store it in
 * full text index.
 *
 * User: denispavlov
 * Date: 10/01/2017
 * Time: 10:30
 */
public interface IndexBuilder<T, PK extends Serializable> {

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
        long getLastIndexCount();

    }

    /**
     * Index name.
     *
     * @return name
     */
    String getName();

    /**
     * @return state of full text index.
     */
    FTIndexState getFullTextIndexState();

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

}
