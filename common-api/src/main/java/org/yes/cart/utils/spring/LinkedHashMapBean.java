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

package org.yes.cart.utils.spring;

import java.util.Map;

/**
 * Basic hash map bean, which can be extended in XML without creating a copy
 * or replacing an instance in Spring context.
 *
 * User: denispavlov
 * Date: 31/03/2018
 * Time: 16:14
 */
public interface LinkedHashMapBean<K, V> extends Map<K, V> {

    /**
     * Get parent of this map.
     *
     * @return parent map that is being extended, or null for extension points
     */
    LinkedHashMapBean getParent();

    /**
     * Name of this extension point.
     *
     * @return name
     */
    String getName();

}
