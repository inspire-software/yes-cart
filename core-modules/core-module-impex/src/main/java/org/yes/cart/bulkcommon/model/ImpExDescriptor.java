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

package org.yes.cart.bulkcommon.model;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface ImpExDescriptor<C extends ImpExContext> {

    /**
     * Get export context if one provided or null.
     *
     * @return import context
     */
    C getContext();

    /**
     * Get fully qualified entity interface. For example - org.yes.cart.domain.entity.Brand
     *
     * @return fully qualified entity interface
     */
    String getEntityType();

    /**
     * Get entity interface. For example - org.yes.cart.domain.entity.Brand
     *
     * @return entity interface
     */
    Class getEntityTypeClass();

}
