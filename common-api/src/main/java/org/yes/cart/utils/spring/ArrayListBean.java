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

import java.util.List;

/**
 * Basic list bean, which can be extended in XML without creating a copy
 * or replacing an instance in Spring context.
 *
 * User: denispavlov
 * Date: 27/03/2021
 * Time: 09:02
 */
public interface ArrayListBean<E> extends List<E> {

    /**
     * Get parent of this list.
     *
     * @return parent list that is being extended, or null for extension points
     */
    ArrayListBean getParent();

    /**
     * Name of this extension point.
     *
     * @return name
     */
    String getName();

}
