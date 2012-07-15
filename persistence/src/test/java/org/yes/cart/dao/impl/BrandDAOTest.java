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

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.domain.entity.impl.BrandEntity;

import java.util.List;

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
        attributeDAO = (GenericDAO<Attribute, Long>) ctx().getBean(DaoServiceBeanKeys.ATTRIBUTE_DAO);
        brandDAO = (GenericDAO<Brand, Long>) ctx().getBean(DaoServiceBeanKeys.BRAND_DAO);
    }

    @Test
    public void testAddEmptyBrand() {
        // create simple brand without attributes
        Brand entity = new BrandEntity();
        entity.setName("brandName without attributes");
        long pk = brandDAO.create(entity).getBrandId();
        assertTrue(entity.getBrandId() != 0);
        assertTrue(entity.getAttribute().isEmpty());
        List<Brand> brands = brandDAO.findByCriteria(Restrictions.eq("brandId", pk));
        assertEquals(1, brands.size());
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
        List<Brand> brands = brandDAO.findByCriteria(
                new CriteriaTuner() {
                    public void tune(final Criteria crit) {
                        crit.setFetchMode("attribute", FetchMode.JOIN);
                    }
                },
                Restrictions.eq("brandId", pk)
        );
        assertEquals(1, brands.size());
        entityWithAttributes = brands.get(0);
        assertEquals(1, entityWithAttributes.getAttribute().size());
    }
}
