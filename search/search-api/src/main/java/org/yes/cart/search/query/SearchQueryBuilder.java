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

package org.yes.cart.search.query;

import org.yes.cart.search.dto.NavigationContext;

import java.util.List;

/**
 * Basic interface for full text search query builder. Each builder should only
 * create relevant criteria. It must nit add criteria that is irrelevant.
 * It is up to {@link org.yes.cart.search.SearchQueryFactory} to combine builders to form
 * valid query.
 *
 * User: denispavlov
 * Date: 15/11/2014
 * Time: 22:16
 */
public interface SearchQueryBuilder<T> {

    /**
     * Create strict boolean query for given value.
     *
     * @param navigationContext navigation context
     * @param parameter parameter name
     * @param value value can be collection or single value depending on builder impl
     *
     * @return create query chain to try in order of strictness
     */
    List<T> createQueryChain(NavigationContext<T> navigationContext, String parameter, Object value);

}
