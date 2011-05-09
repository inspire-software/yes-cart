package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.impl.AddressEntity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class EntityFactoryImplTest extends AbstractTestDAO {

    EntityFactory entityFactory;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        entityFactory = (EntityFactory) ctx.getBean("internalEntityFactory");
    }

    public void cleanUp() {
        entityFactory = null;
        //do nothing
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
