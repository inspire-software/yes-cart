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

package org.yes.cart.search.dao;

import org.yes.cart.domain.misc.Pair;

/**
 * User: denispavlov
 * Date: 31/03/2017
 * Time: 08:36
 */
public interface DocumentAdapter<T, PK, D> {

    /**
     * Adapet persistent entity into an index document object.
     *
     * @param entity entity to adapt
     *
     * @return pair of primary key and document. if document is null this denotes that
     *         this entity should not be indexed (or removed from index)
     */
    Pair<PK, D> adapt(T entity);

}
