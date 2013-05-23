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

package org.yes.cart.domain.dto.adapter.impl;


import org.yes.cart.dao.EntityFactory;

/**
 * Adaptor to port Domain entity factory to GeDA bean factory
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class EntityFactoryToBeanFactoryAdaptor implements com.inspiresoftware.lib.dto.geda.adapter.BeanFactory {

    private final EntityFactory entityFactory;

    public EntityFactoryToBeanFactoryAdaptor(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    /** {@inheritDoc} */
    public Class getClazz(final String entityBeanKey) {
        return entityFactory.getImplClass(entityBeanKey);
    }

    /** {@inheritDoc} */
    public Object get(final String entityBeanKey) {
        return entityFactory.getByKey(entityBeanKey);
    }

}
