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

package org.yes.cart.bulkcommon.service;

import org.yes.cart.domain.entity.DataGroup;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 13:55
 */
public interface DataDescriptorResolver<T> {

    /**
     * Get descriptor by name.
     *
     * @param name name
     *
     * @return descriptor object
     */
    T getByName(String name);

    /**
     * Get descriptors specified by group in correct order.
     *
     * @param group group name
     *
     * @return list of descriptor objects
     */
    Map<String, T> getByGroup(String group);

    /**
     * Get resolvable groups.
     *
     * @return list of groups
     */
    List<DataGroup> getGroups();

    /**
     * Register tuplizer.
     *
     * @param tuplizer tuplizer
     */
    void register(DataDescriptorTuplizer<T> tuplizer);

}
