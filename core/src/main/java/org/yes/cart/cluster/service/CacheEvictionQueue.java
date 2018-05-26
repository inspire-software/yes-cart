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
package org.yes.cart.cluster.service;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 26/05/2018
 * Time: 08:20
 */
public interface CacheEvictionQueue {

    /**
     * Push change to queue.
     *
     * @param entityOperation operation
     * @param entityName      entity
     * @param pkValue         PK
     */
    void enqueue(String entityOperation, String entityName, Long pkValue);

    /**
     * Get first item or null id queue is empty.
     *
     * @return cache eviction.
     */
    CacheEvictionItem dequeue();

    /**
     * Clear current cache queue;
     */
    void clear();

    interface CacheEvictionItem {

        String getUser();

        String getOperation();

        String getEntityName();

        Collection<Long> getPKs();

    }

}
