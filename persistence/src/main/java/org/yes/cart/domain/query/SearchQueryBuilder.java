/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.domain.query;

import org.apache.lucene.search.Query;

/**
 * Basic interface for full text search query builder. Each builder should only
 * create relevant criteria. It must nit add criteria that is irrelevant.
 * It is up to {@link LuceneQueryFactory} to combine builders to form
 * valid query.
 *
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 22:16
 */
public interface SearchQueryBuilder {

    /**
     * Create strict boolean query for given value.
     *
     * @param shopId shop PK
     * @param parameter parameter name
     * @param value value can be collection or single value depending on builder impl
     *
     * @return boolean query
     */
    Query createStrictQuery(long shopId, String parameter, Object value);

    /**
     * Create relaxed boolean query for given value.
     *
     * @param shopId shop PK
     * @param parameter parameter name
     * @param value value can be collection or single value depending on builder impl
     *
     * @return boolean query
     */
    Query createRelaxedQuery(long shopId, String parameter, Object value);

}
