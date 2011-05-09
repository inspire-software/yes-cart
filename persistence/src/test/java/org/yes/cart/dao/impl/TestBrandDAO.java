package org.yes.cart.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.domain.entity.impl.BrandEntity;

import java.util.HashSet;
import java.util.Set;


/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */

public class TestBrandDAO extends AbstractTestDAO {

    /**
     * Logger for debugging purposes
     */
    protected Logger logger = Logger.getLogger(TestBrandDAO.class);

    private GenericDAO<Etype, Long> etypeDAO;
    private GenericDAO<Attribute, Long> attributeDAO;
    private GenericDAO<AttributeGroup, Long> attributeGroupDAO;
    private GenericDAO<Brand, Long> brandDAO;

    private Etype etypeEntity;
    private AttributeGroup attributeGroupEntity;
    private Attribute attributeEntity;

    private Set<Long> cleanupPks = new HashSet<Long>();





    @Before
    public void setUp() throws Exception {
        super.setUp();
        brandDAO = (GenericDAO<Brand, Long>) ctx.getBean(DaoServiceBeanKeys.BRAND_DAO);
        etypeDAO = (GenericDAO<Etype, Long>) ctx.getBean(DaoServiceBeanKeys.ETYPE_DAO);
        attributeDAO = (GenericDAO<Attribute, Long>) ctx.getBean(DaoServiceBeanKeys.ATTRIBUTE_DAO);
        attributeGroupDAO = (GenericDAO<AttributeGroup, Long>) ctx.getBean(DaoServiceBeanKeys.ATTRIBUTE_GROUP_DAO);

        etypeEntity = etypeDAO.findById(1000L);
        attributeGroupEntity = attributeGroupDAO.findByCriteria (Restrictions.eq("code","BRAND")).get(0);
        attributeEntity = attributeDAO.findByCriteria(Restrictions.eq("code","BRAND_IMAGE")).get(0);


    }

    @After
    public void tearDown() {
        brandDAO = null;
        etypeDAO = null;
        attributeDAO = null;
        attributeGroupDAO = null;
        super.tearDown();
    }


    /**
     * Test that we are able to add brand.
     */
    @Test
    public void testAddEmptyBrand() {
        
        // create simle brand without attributes
        Brand entity = new BrandEntity();

        entity.setName("brandName without attributes");

        long pk = brandDAO.create(entity).getBrandId();

        cleanupPks.add(pk);

        assertTrue(entity.getBrandId() != 0);

        assertTrue(entity.getAttribute().isEmpty());

        Criteria checkRiteria = session.createCriteria(Brand.class).add(Restrictions.eq("brandId", pk));

        assertEquals(1, checkRiteria.list().size());

    }

    /**
     * Test that we are able to add brand.
     */
    @Test
    public void testAddBrandWithAttributeValues() {

        Brand entityWithAttributes = new BrandEntity();

        entityWithAttributes.setName("brandName with attributes");

        AttrValueBrand attrValueBrandEntity = new AttrValueEntityBrand(entityWithAttributes, attributeEntity);
        attrValueBrandEntity.setVal("brand.jpg");
        //BRAND_IMAGE

        entityWithAttributes.getAttribute().add(attrValueBrandEntity);

        long pk = brandDAO.create(entityWithAttributes).getBrandId();

        cleanupPks.add(pk);

        Criteria checkRiteria = session.createCriteria(Brand.class).add(Restrictions.eq("brandId", pk));

        assertEquals(1, checkRiteria.list().size());

        entityWithAttributes = (Brand) checkRiteria.list().get(0);

        assertEquals(1, entityWithAttributes.getAttribute().size());

    }

    @Test
    public void cleanUp() {
        for (Long pk : cleanupPks) {
            brandDAO.delete(brandDAO.findById(pk));
        }

        for (Long pk : cleanupPks) {
            assertNull(brandDAO.findById(pk));            
        }

    }


}
