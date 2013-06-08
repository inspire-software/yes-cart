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

package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.impl.AddressEntity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class EntityFactoryImplTest extends AbstractTestDAO {

    EntityFactory entityFactory;

    @Before
    public void setUp()  {
        entityFactory = (EntityFactory) ctx().getBean("internalEntityFactory");
    }

    @Test
    public void testGet() {
        AddressEntity entity = entityFactory.getByIface(Address.class);
        assertNotNull(entity);
        try {
            entityFactory.getByIface(AddressEntity.class);
        } catch (Throwable t) {
            assertTrue(true);
        }
    }
}
