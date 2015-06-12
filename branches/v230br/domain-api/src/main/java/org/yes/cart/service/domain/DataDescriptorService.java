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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.DataDescriptor;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/06/2015
 * Time: 12:57
 */
public interface DataDescriptorService extends GenericService<DataDescriptor> {

    /**
     * Find descriptor by name.
     *
     * @param name name
     *
     * @return single descriptor
     */
    DataDescriptor findByName(String name);

    /**
     * Find descriptors by given names.
     *
     * @param names names
     *
     * @return all descriptos matching given names
     */
    List<DataDescriptor> findByNames(Collection<String> names);

}
