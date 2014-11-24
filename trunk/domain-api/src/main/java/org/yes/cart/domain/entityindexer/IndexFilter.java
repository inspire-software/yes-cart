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

package org.yes.cart.domain.entityindexer;

/**
 * User: denispavlov
 * Date: 24/11/2014
 * Time: 20:30
 */
public interface IndexFilter<T> {

    /**
     * Skip index if this method returns true.
     *
     * @param entity entity to check.
     *
     * @return if returns true then reindexing should be skipped for this entity
     */
    boolean skipIndexing(T entity);

}
