package org.yes.cart.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.domain.entity.impl.BrandEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class BrandDAOTest extends AbstractTestDAO {

    private GenericDAO<Attribute, Long> attributeDAO;
    private GenericDAO<Brand, Long> brandDAO;

    @Before
    public void setUp() {
        attributeDAO = (GenericDAO<Attribute, Long>) ctx.getBean(DaoServiceBeanKeys.ATTRIBUTE_DAO);
        brandDAO = (GenericDAO<Brand, Long>) ctx.getBean(DaoServiceBeanKeys.BRAND_DAO);
    }

    @Test
    public void testAddEmptyBrand() {
        // create simle brand without attributes
        Brand entity = new BrandEntity();
        entity.setName("brandName without attributes");
        long pk = brandDAO.create(entity).getBrandId();
        assertTrue(entity.getBrandId() != 0);
        assertTrue(entity.getAttribute().isEmpty());
        Criteria checkRiteria = session.createCriteria(Brand.class).add(Restrictions.eq("brandId", pk));
        assertEquals(1, checkRiteria.list().size());
    }

    @Test
    public void testAddBrandWithAttributeValues() {
        Brand entityWithAttributes = new BrandEntity();
        entityWithAttributes.setName("brandName with attributes");
        Attribute attributeEntity = attributeDAO.findByCriteria(Restrictions.eq("code", "BRAND_IMAGE")).get(0);
        AttrValueBrand attrValueBrandEntity = new AttrValueEntityBrand(entityWithAttributes, attributeEntity);
        attrValueBrandEntity.setVal("brand.jpg");
        //BRAND_IMAGE
        entityWithAttributes.getAttribute().add(attrValueBrandEntity);
        long pk = brandDAO.create(entityWithAttributes).getBrandId();
        Criteria checkRiteria = session.createCriteria(Brand.class).add(Restrictions.eq("brandId", pk));
        assertEquals(1, checkRiteria.list().size());
        entityWithAttributes = (Brand) checkRiteria.list().get(0);
        assertEquals(1, entityWithAttributes.getAttribute().size());
    }
}
