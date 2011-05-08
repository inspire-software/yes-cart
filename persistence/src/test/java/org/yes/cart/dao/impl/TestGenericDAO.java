package org.yes.cart.dao.impl;

import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.entity.Brand;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class TestGenericDAO extends AbstractTestDAO {


    private GenericDAO<Brand, Long> brandDao;

    private EntityFactory entityFactory;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        brandDao = (GenericDAO<Brand, Long>) ctx.getBean(DaoServiceBeanKeys.BRAND_DAO);
        entityFactory = brandDao.getEntityFactory();
    }

    @After
    public void tearDown() {
        entityFactory = null;
        brandDao = null;
        super.tearDown();
    }

    public void cleanUp() {
        // nothing to do

    }


    @Test
    public void testUpdateWithNativeQuery() {

        String sql = "update tbrand set description = 'zzz' where brand_id = :1";
        assertEquals(1, brandDao.executeNativeQuery(sql, 101));
        Brand brand = brandDao.findSingleByCriteria(Restrictions.eq("brandId", 101L));
        assertEquals("zzz", brand.getDescription());


        sql = "update tbrand set description = 'NewRobotics' where name = :1";
        assertEquals(1, brandDao.executeNativeQuery(sql, "FutureRobots"));
        brand = brandDao.findSingleByCriteria(Restrictions.eq("name", "FutureRobots") );
        assertEquals("NewRobotics", brand.getDescription());


        sql = "update tbrand set description = 'OldRobotics' where brand_id = :1 and name = :2";
        assertEquals(1, brandDao.executeNativeQuery(sql, 101, "FutureRobots"));
        brand = brandDao.findSingleByCriteria(Restrictions.eq("brandId", 101L),
                Restrictions.eq("name", "FutureRobots") );
        assertEquals("OldRobotics", brand.getDescription());        

    }

    @Test
    public void testDeleteWithNativeQuery() {

        Brand brand = entityFactory.getByIface(Brand.class);
        brand.setName("name");
        brand.setDescription("description");
        brand = brandDao.create(brand);


        String sql = "delete from tbrand where brand_id = :1";
        assertEquals(1, brandDao.executeNativeQuery(sql, brand.getBrandId()));
        brand = brandDao.findSingleByCriteria(Restrictions.eq("brandId", brand.getBrandId()));
        assertNull(brand);

        brand = entityFactory.getByIface(Brand.class);
        brand.setName("name2");
        brand.setDescription("description2");
        brand = brandDao.create(brand);


        sql = "delete from tbrand where name = :1 and description= :2 ";
        assertEquals(1, brandDao.executeNativeQuery(sql, "name2", "description2"));
        brand = brandDao.findSingleByCriteria(Restrictions.eq("name", "name2") );
        assertNull(brand);

    }

}
